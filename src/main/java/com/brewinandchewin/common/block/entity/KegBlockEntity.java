package com.brewinandchewin.common.block.entity;

import java.util.ArrayList;
import java.util.Optional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.brewinandchewin.common.block.KegBlock;
import com.brewinandchewin.common.block.entity.container.KegContainer;
import com.brewinandchewin.common.block.entity.inventory.KegItemHandler;
import com.brewinandchewin.common.crafting.KegRecipe;
import com.brewinandchewin.common.crafting.KegRecipe.Range;
import com.brewinandchewin.core.registry.BCBlockEntityTypes;
import com.brewinandchewin.core.tag.BCTags;
import com.brewinandchewin.core.utility.BCTextUtils;

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.mixin.accessor.RecipeManagerAccessor;
import vectorwing.farmersdelight.common.utility.ItemUtils;


public class KegBlockEntity extends SyncedBlockEntity implements MenuProvider, Nameable {
	public static final int MEAL_DISPLAY_SLOT = 5;
	public static final int CONTAINER_SLOT = 6;
	public static final int OUTPUT_SLOT = 7;
	public static final int INVENTORY_SIZE = OUTPUT_SLOT + 1;
	public static final int TEMPERATURE_LOOKUP_RANGE = 1;

	private final ItemStackHandler inventory;
	private final LazyOptional<IItemHandler> inputHandler;
	private final LazyOptional<IItemHandler> outputHandler;

	private int fermentTime;
	private int fermentTimeTotal;
	private ItemStack mealContainerStack;
	private Component customName;
	public int heat;
	public int cold;

	protected final ContainerData kegData;
	private final Object2IntOpenHashMap<ResourceLocation> experienceTracker;

	private ResourceLocation lastRecipeID;
	private boolean checkNewRecipe;

	public KegBlockEntity(BlockPos pos, BlockState state) {
		super(BCBlockEntityTypes.KEG.get(), pos, state);
		this.inventory = createHandler();
		this.inputHandler = LazyOptional.of(() -> new KegItemHandler(inventory, Direction.UP));
		this.outputHandler = LazyOptional.of(() -> new KegItemHandler(inventory, Direction.DOWN));
		this.mealContainerStack = ItemStack.EMPTY;
		this.kegData = createIntArray();
		this.experienceTracker = new Object2IntOpenHashMap<>();
		this.checkNewRecipe = true;
	}

	@Override
	public void load(CompoundTag compound) {
		super.load(compound);
		inventory.deserializeNBT(compound.getCompound("Inventory"));
		fermentTime = compound.getInt("FermentTime");
		fermentTimeTotal = compound.getInt("FermentTimeTotal");
		mealContainerStack = ItemStack.of(compound.getCompound("Container"));
		if (compound.contains("CustomName", 8)) {
			customName = Component.Serializer.fromJson(compound.getString("CustomName"));
		}
		CompoundTag compoundRecipes = compound.getCompound("RecipesUsed");
		for (String key : compoundRecipes.getAllKeys()) {
			experienceTracker.put(new ResourceLocation(key), compoundRecipes.getInt(key));
		}
		heat = compound.getInt("Heat");
		cold = compound.getInt("Cold");
	}

	@Override
	public void saveAdditional(CompoundTag compound) {
		super.saveAdditional(compound);
		compound.putInt("FermentTime", fermentTime);
		compound.putInt("FermentTimeTotal", fermentTimeTotal);
		compound.put("Container", mealContainerStack.serializeNBT());
		if (customName != null) {
			compound.putString("CustomName", Component.Serializer.toJson(customName));
		}
		compound.put("Inventory", inventory.serializeNBT());
		CompoundTag compoundRecipes = new CompoundTag();
		experienceTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
		compound.put("RecipesUsed", compoundRecipes);
		compound.putInt("Heat", heat);
		compound.putInt("Cold", cold);
	}

	private CompoundTag writeItems(CompoundTag compound) {
		saveAdditional(compound);
		compound.put("Container", mealContainerStack.serializeNBT());
		compound.put("Inventory", inventory.serializeNBT());
		return compound;
	}

	public CompoundTag writeMeal(CompoundTag compound) {
		if (getMeal().isEmpty()) return compound;

		ItemStackHandler drops = new ItemStackHandler(INVENTORY_SIZE);
		for (int i = 0; i < INVENTORY_SIZE; ++i) {
			drops.setStackInSlot(i, i == MEAL_DISPLAY_SLOT ? inventory.getStackInSlot(i) : ItemStack.EMPTY);
		}
		if (customName != null) {
			compound.putString("CustomName", Component.Serializer.toJson(customName));
		}
		compound.put("Container", mealContainerStack.serializeNBT());
		compound.put("Inventory", drops.serializeNBT());
		return compound;
	}

	// ======== BASIC FUNCTIONALITY ========

	public static void fermentingTick(Level level, BlockPos pos, BlockState state, KegBlockEntity keg) {
		boolean didInventoryChange = false;
		keg.updateTemperature();
		if (keg.hasInput()) {
			Optional<KegRecipe> recipe = keg.getMatchingRecipe(new RecipeWrapper(keg.inventory));
			if (recipe.isPresent() && keg.canFerment(recipe.get())) {
				didInventoryChange = keg.processFermenting(recipe.get());
			} else {
				keg.fermentTime = 0;
			}
		} else if (keg.fermentTime > 0) {
			keg.fermentTime = Mth.clamp(keg.fermentTime - 2, 0, keg.fermentTimeTotal);
		}

		ItemStack mealStack = keg.getMeal();
		if (!mealStack.isEmpty()) {
			if (!keg.doesMealHaveContainer(mealStack)) {
				keg.moveMealToOutput();
				didInventoryChange = true;
			} else if (!keg.inventory.getStackInSlot(CONTAINER_SLOT).isEmpty()) {
				keg.useStoredContainersOnMeal();
				didInventoryChange = true;
			}
		}

		if (didInventoryChange) {
			keg.updateTemperature();
			keg.inventoryChanged();
		}
	}


	public static void animationTick(Level level, BlockPos pos, BlockState state, KegBlockEntity keg) {
	}

	public void updateTemperature() {
		ArrayList<BlockState> states = new ArrayList<>();
		int range = TEMPERATURE_LOOKUP_RANGE;
		for (int x = -range; x <= range; x++) {
			for (int y = -range; y <= range; y++) {
				for (int z = -range; z <= range; z++) {
					states.add(level.getBlockState(worldPosition.offset(x, y, z)));
				}
			}
		}
		heat = states.stream().filter(s -> s.is(BCTags.HOT_BLOCK)).mapToInt(s -> 1).sum();
		cold = states.stream().filter(s -> s.is(BCTags.COLD_BLOCK)).mapToInt(s -> 1).sum();
		
		level.sendBlockUpdated(worldPosition, getBlockState(), getBlockState(), 2);
		level.blockEntityChanged(worldPosition);
		level.updateNeighbourForOutputSignal(worldPosition, getBlockState().getBlock());
	}
	
	private Optional<KegRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
		if (level == null) return Optional.empty();

		if (lastRecipeID != null) {
			Recipe<RecipeWrapper> recipe = ((RecipeManagerAccessor) level.getRecipeManager())
					.getRecipeMap(KegRecipe.TYPE)
					.get(lastRecipeID);
			if (recipe instanceof KegRecipe) {
				if (recipe.matches(inventoryWrapper, level)) {
					return Optional.of((KegRecipe) recipe);
				}
				if (recipe.getResultItem().sameItem(getMeal())) {
					return Optional.empty();
				}
			}
		}

		if (checkNewRecipe) {
			Optional<KegRecipe> recipe = level.getRecipeManager().getRecipeFor(KegRecipe.TYPE, inventoryWrapper, level);
			if (recipe.isPresent()) {
				lastRecipeID = recipe.get().getId();
				return recipe;
			}
		}

		checkNewRecipe = false;
		return Optional.empty();
	}

	public ItemStack getContainer() {
		if (!mealContainerStack.isEmpty()) {
			return mealContainerStack;
		} else {
			return getMeal().getContainerItem();
		}
	}

	private boolean hasInput() {
		for (int i = 0; i < MEAL_DISPLAY_SLOT; ++i) {
			if (!inventory.getStackInSlot(i).isEmpty()) return true;
		}
		return false;
	}

	protected boolean canFerment(KegRecipe recipe) {
		Range i = recipe.getTemperature();
		int j = recipe.getTemperatureJei();
		if (hasInput() && (i.contains(heat - cold) || j == 3)) {
			ItemStack resultStack = recipe.getResultItem();
			if (resultStack.isEmpty()) {
				return false;
			} else {
				ItemStack storedMealStack = inventory.getStackInSlot(MEAL_DISPLAY_SLOT);
				if (storedMealStack.isEmpty()) {
					return true;
				} else if (!storedMealStack.sameItem(resultStack)) {
					return false;
				} else if (storedMealStack.getCount() + resultStack.getCount() <= inventory.getSlotLimit(MEAL_DISPLAY_SLOT)) {
					return true;
				} else {
					return storedMealStack.getCount() + resultStack.getCount() <= resultStack.getMaxStackSize();
				}
			}
		} else {
			return false;
		}
	}

	private boolean processFermenting(KegRecipe recipe) {
		if (level == null) return false;

		++fermentTime;
		fermentTimeTotal = recipe.getfermentTime();
		if (fermentTime < fermentTimeTotal) {
			return false;
		}

		fermentTime = 0;
		mealContainerStack = recipe.getOutputContainer();
		ItemStack resultStack = recipe.getResultItem();
		ItemStack storedMealStack = inventory.getStackInSlot(MEAL_DISPLAY_SLOT);
		if (storedMealStack.isEmpty()) {
			inventory.setStackInSlot(MEAL_DISPLAY_SLOT, resultStack.copy());
		} else if (storedMealStack.sameItem(resultStack)) {
			storedMealStack.grow(resultStack.getCount());
		}
		trackRecipeExperience(recipe);

		for (int i = 0; i < MEAL_DISPLAY_SLOT; ++i) {
			ItemStack slotStack = inventory.getStackInSlot(i);
			if (slotStack.hasContainerItem()) {
				Direction direction = getBlockState().getValue(KegBlock.FACING).getCounterClockWise();
				double x = worldPosition.getX() + 0.5 + (direction.getStepX() * 0.25);
				double y = worldPosition.getY() + 0.7;
				double z = worldPosition.getZ() + 0.5 + (direction.getStepZ() * 0.25);
				ItemUtils.spawnItemEntity(level, inventory.getStackInSlot(i).getContainerItem(), x, y, z,
						direction.getStepX() * 0.08F, 0.25F, direction.getStepZ() * 0.08F);
			}
			if (!slotStack.isEmpty())
				slotStack.shrink(1);
		}
		return true;
	}

	public void trackRecipeExperience(@Nullable Recipe<?> recipe) {
		if (recipe != null) {
			ResourceLocation recipeID = recipe.getId();
			experienceTracker.addTo(recipeID, 1);
		}
	}

	public void clearUsedRecipes(Player player) {
		grantStoredRecipeExperience(player.level, player.position());
		experienceTracker.clear();
	}

	public void grantStoredRecipeExperience(Level world, Vec3 pos) {
		for (Object2IntMap.Entry<ResourceLocation> entry : experienceTracker.object2IntEntrySet()) {
			world.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> splitAndSpawnExperience(world, pos, entry.getIntValue(), ((KegRecipe) recipe).getExperience()));
		}
	}

	private static void splitAndSpawnExperience(Level world, Vec3 pos, int craftedAmount, float experience) {
		int expTotal = Mth.floor((float) craftedAmount * experience);
		float expFraction = Mth.frac((float) craftedAmount * experience);
		if (expFraction != 0.0F && Math.random() < (double) expFraction) {
			++expTotal;
		}

		while (expTotal > 0) {
			int expValue = ExperienceOrb.getExperienceValue(expTotal);
			expTotal -= expValue;
			world.addFreshEntity(new ExperienceOrb(world, pos.x, pos.y, pos.z, expValue));
		}
	}

	public ItemStackHandler getInventory() {
		return inventory;
	}

	public ItemStack getMeal() {
		return inventory.getStackInSlot(MEAL_DISPLAY_SLOT);
	}

	public NonNullList<ItemStack> getDroppableInventory() {
		NonNullList<ItemStack> drops = NonNullList.create();
		for (int i = 0; i < INVENTORY_SIZE; ++i) {
			if (i != MEAL_DISPLAY_SLOT) {
				drops.add(inventory.getStackInSlot(i));
			}
		}
		return drops;
	}

	private void moveMealToOutput() {
		ItemStack mealStack = inventory.getStackInSlot(MEAL_DISPLAY_SLOT);
		ItemStack outputStack = inventory.getStackInSlot(OUTPUT_SLOT);
		int mealCount = Math.min(mealStack.getCount(), mealStack.getMaxStackSize() - outputStack.getCount());
		if (outputStack.isEmpty()) {
			inventory.setStackInSlot(OUTPUT_SLOT, mealStack.split(mealCount));
		} else if (outputStack.getItem() == mealStack.getItem()) {
			mealStack.shrink(mealCount);
			outputStack.grow(mealCount);
		}
	}

	private void useStoredContainersOnMeal() {
		ItemStack mealStack = inventory.getStackInSlot(MEAL_DISPLAY_SLOT);
		ItemStack containerInputStack = inventory.getStackInSlot(CONTAINER_SLOT);
		ItemStack outputStack = inventory.getStackInSlot(OUTPUT_SLOT);

		if (isContainerValid(containerInputStack) && outputStack.getCount() < outputStack.getMaxStackSize()) {
			int smallerStackCount = Math.min(mealStack.getCount(), containerInputStack.getCount());
			int mealCount = Math.min(smallerStackCount, mealStack.getMaxStackSize() - outputStack.getCount());
			if (outputStack.isEmpty()) {
				containerInputStack.shrink(mealCount);
				inventory.setStackInSlot(OUTPUT_SLOT, mealStack.split(mealCount));
			} else if (outputStack.getItem() == mealStack.getItem()) {
				mealStack.shrink(mealCount);
				containerInputStack.shrink(mealCount);
				outputStack.grow(mealCount);
			}
		}
	}

	public ItemStack useHeldItemOnMeal(ItemStack container) {
		if (isContainerValid(container) && !getMeal().isEmpty()) {
			container.shrink(1);
			return getMeal().split(1);
		}
		return ItemStack.EMPTY;
	}

	private boolean doesMealHaveContainer(ItemStack meal) {
		return !mealContainerStack.isEmpty() || meal.hasContainerItem();
	}

	public boolean isContainerValid(ItemStack containerItem) {
		if (containerItem.isEmpty()) return false;
		if (!mealContainerStack.isEmpty()) {
			return mealContainerStack.sameItem(containerItem);
		} else {
			return getMeal().getContainerItem().sameItem(containerItem);
		}
	}

	@Override
	public Component getName() {
		return customName != null ? customName : BCTextUtils.getTranslation("container.keg");
	}

	@Override
	public Component getDisplayName() {
		return getName();
	}

	@Override
	@Nullable
	public Component getCustomName() {
		return customName;
	}

	public void setCustomName(Component name) {
		customName = name;
	}

	@Override
	public AbstractContainerMenu createMenu(int id, Inventory player, Player entity) {
		return new KegContainer(id, player, this, kegData);
	}

	@Override
	@Nonnull
	public <T> LazyOptional<T> getCapability(Capability<T> cap, @Nullable Direction side) {
		if (cap.equals(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)) {
			if (side == null || side.equals(Direction.UP)) {
				return inputHandler.cast();
			} else {
				return outputHandler.cast();
			}
		}
		return super.getCapability(cap, side);
	}

	@Override
	public void setRemoved() {
		super.setRemoved();
		inputHandler.invalidate();
		outputHandler.invalidate();
	}

	@Override
	public CompoundTag getUpdateTag() {
		return writeItems(new CompoundTag());
	}

	private ItemStackHandler createHandler() {
		return new ItemStackHandler(INVENTORY_SIZE) {
			@Override
			protected void onContentsChanged(int slot) {
				if (slot >= 0 && slot < MEAL_DISPLAY_SLOT) {
					checkNewRecipe = true;
				}
				inventoryChanged();
			}
		};
	}

	private ContainerData createIntArray() {
		return new ContainerData() {
			@Override
			public int get(int index) {
				return switch (index) {
					case 0 -> KegBlockEntity.this.fermentTime;
					case 1 -> KegBlockEntity.this.fermentTimeTotal;
					default -> 0;
				};
			}

			@Override
			public void set(int index, int value) {
				switch (index) {
					case 0 -> KegBlockEntity.this.fermentTime = value;
					case 1 -> KegBlockEntity.this.fermentTimeTotal = value;
				}
			}

			@Override
			public int getCount() {
				return 2;
			}
		};
	}
}