package umpaz.brewinandchewin.common.block.entity;

import com.google.common.collect.Lists;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.Nameable;
import net.minecraft.world.entity.ExperienceOrb;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.RecipeHolder;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import umpaz.brewinandchewin.common.block.KegBlock;
import umpaz.brewinandchewin.common.block.entity.container.KegMenu;
import umpaz.brewinandchewin.common.block.entity.inventory.KegItemHandler;
import umpaz.brewinandchewin.common.crafting.KegRecipe;
import umpaz.brewinandchewin.common.registry.BCBlockEntityTypes;
import umpaz.brewinandchewin.common.registry.BCItems;
import umpaz.brewinandchewin.common.registry.BCRecipeTypes;
import umpaz.brewinandchewin.common.utility.BCTextUtils;
import vectorwing.farmersdelight.common.block.entity.SyncedBlockEntity;
import vectorwing.farmersdelight.common.mixin.accessor.RecipeManagerAccessor;
import vectorwing.farmersdelight.common.tag.ModTags;
import vectorwing.farmersdelight.common.utility.ItemUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class KegBlockEntity extends SyncedBlockEntity implements MenuProvider, Nameable, RecipeHolder
{
    public static final int DRINK_DISPLAY_SLOT = 5;
    public static final int CONTAINER_SLOT = 6;
    public static final int OUTPUT_SLOT = 7;
    public static final int INVENTORY_SIZE = OUTPUT_SLOT + 1;

    private final ItemStackHandler inventory;
    private final LazyOptional<IItemHandler> inputHandler;
    private final LazyOptional<IItemHandler> outputHandler;

    //tank
    private final FluidTank fluidTank = new FluidTank(1000) {
        @Override
        protected void onContentsChanged() {
            KegBlockEntity.this.setChanged();
        }
    };
    private final LazyOptional<IFluidHandler> fluidHandler = LazyOptional.of(() -> fluidTank);

    private int fermentTime;
    private int fermentTimeTotal;
    private int kegTemperature;
    private ItemStack drinkContainerStack;
    private Component customName;

    protected final ContainerData kegData;
    private final Object2IntOpenHashMap<ResourceLocation> usedRecipeTracker;

    private ResourceLocation lastRecipeID;
    private boolean checkNewRecipe;

    public KegBlockEntity(BlockPos pos, BlockState state) {
        super(BCBlockEntityTypes.KEG.get(), pos, state);
        this.inventory = createHandler();
        this.inputHandler = LazyOptional.of(() -> new KegItemHandler(inventory, Direction.UP));
        this.outputHandler = LazyOptional.of(() -> new KegItemHandler(inventory, Direction.DOWN));
        this.drinkContainerStack = ItemStack.EMPTY;
        this.kegData = createIntArray();
        this.usedRecipeTracker = new Object2IntOpenHashMap<>();
        this.checkNewRecipe = true;
    }

    public static ItemStack getDrinkFromItem(ItemStack kegStack) {
        if (!kegStack.is(BCItems.KEG.get())) {
            return ItemStack.EMPTY;
        }
        CompoundTag compound = kegStack.getTagElement("BlockEntityTag");
        if (compound != null) {
            CompoundTag inventoryTag = compound.getCompound("Inventory");
            if (inventoryTag.contains("Items", 7)) {
                ItemStackHandler handler = new ItemStackHandler();
                handler.deserializeNBT(inventoryTag);
                return handler.getStackInSlot(5);
            }
        }

        return ItemStack.EMPTY;
    }

    @Override
    public void load(CompoundTag compound) {
        super.load(compound);
        inventory.deserializeNBT(compound.getCompound("Inventory"));
        fermentTime = compound.getInt("FermentTime");
        fermentTimeTotal = compound.getInt("FermentTimeTotal");
        kegTemperature = compound.getInt("KegTemperature");
        drinkContainerStack = ItemStack.of(compound.getCompound("Container"));
        if (compound.contains("CustomName", 8)) {
            customName = Component.Serializer.fromJson(compound.getString("CustomName"));
        }
        CompoundTag compoundRecipes = compound.getCompound("RecipesUsed");
        for (String key : compoundRecipes.getAllKeys()) {
            usedRecipeTracker.put(new ResourceLocation(key), compoundRecipes.getInt(key));
        }
        //load tank
        fluidTank.readFromNBT(compound.getCompound("FluidTank"));
    }

    @Override
    public void saveAdditional(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.putInt("FermentTime", fermentTime);
        compound.putInt("FermentTimeTotal", fermentTimeTotal);
        compound.putInt("KegTemperature", kegTemperature);
        compound.put("Container", drinkContainerStack.serializeNBT());
        if (customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(customName));
        }
        compound.put("Inventory", inventory.serializeNBT());
        CompoundTag compoundRecipes = new CompoundTag();
        usedRecipeTracker.forEach((recipeId, craftedAmount) -> compoundRecipes.putInt(recipeId.toString(), craftedAmount));
        compound.put("RecipesUsed", compoundRecipes);
        //save tank
        compound.put("FluidTank", fluidTank.writeToNBT(new CompoundTag()));
    }

    private CompoundTag writeItems(CompoundTag compound) {
        super.saveAdditional(compound);
        compound.put("Container", drinkContainerStack.serializeNBT());
        compound.put("Inventory", inventory.serializeNBT());
        //also add fluid
        compound.put("FluidTank", fluidTank.writeToNBT(new CompoundTag()));
        return compound;
    }

    public CompoundTag writeDrink(CompoundTag compound) {
        if (getDrink().isEmpty()) return compound;

        ItemStackHandler drops = new ItemStackHandler(INVENTORY_SIZE);
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            drops.setStackInSlot(i, i == DRINK_DISPLAY_SLOT ? inventory.getStackInSlot(i) : ItemStack.EMPTY);
        }
        if (customName != null) {
            compound.putString("CustomName", Component.Serializer.toJson(customName));
        }
        compound.put("Container", drinkContainerStack.serializeNBT());
        compound.put("Inventory", drops.serializeNBT());
        return compound;
    }

    public static void fermentingTick(Level level, BlockPos pos, BlockState state, KegBlockEntity keg) {
        keg.updateTemperature();
        boolean didInventoryChange = false;
        if (keg.hasInput()) {
            Optional<KegRecipe> recipe = keg.getMatchingRecipe(new RecipeWrapper(keg.inventory));
            if (recipe.isPresent() && keg.canFerment(recipe.get())) {
                didInventoryChange = keg.processFermenting(recipe.get(), keg);
            } else {
                keg.fermentTime = 0;
            }
        } else if (keg.fermentTimeTotal > 0) {
            keg.fermentTime = Mth.clamp(keg.fermentTime - 2, 0, keg.fermentTimeTotal);
        }

        ItemStack drinkStack = keg.getDrink();
        if (!drinkStack.isEmpty()) {
            if (!keg.doesDrinkHaveContainer(drinkStack)) {
                keg.moveDrinkToOutput();
                didInventoryChange = true;
            } else if (!keg.inventory.getStackInSlot(CONTAINER_SLOT).isEmpty()) {
                keg.useStoredContainersOnDrink();
                didInventoryChange = true;
            }
        }

        if (didInventoryChange) {
            keg.inventoryChanged();
        }
    }

    public void updateTemperature() {
        ArrayList<BlockState> states = new ArrayList<>();
        int range = 1;
        for (int x = -range; x <= range; x++) {
            for (int y = -range; y <= range; y++) {
                for (int z = -range; z <= range; z++) {
                    states.add(level.getBlockState(worldPosition.offset(x, y, z)));
                }
            }
        }

        int heat = states.stream().filter(s -> s.is(ModTags.HEAT_SOURCES)).filter(s -> s.hasProperty(BlockStateProperties.LIT)).filter(s -> s.getValue(BlockStateProperties.LIT)).mapToInt(s -> 1).sum();
        heat += states.stream().filter(s -> s.is(ModTags.HEAT_SOURCES)).filter(s -> !s.hasProperty(BlockStateProperties.LIT)).mapToInt(s -> 1).sum();
        BlockState stateBelow = level.getBlockState(worldPosition.below());
        if (stateBelow.is(ModTags.HEAT_CONDUCTORS)) {
            BlockState stateFurtherBelow = level.getBlockState(worldPosition.below(2));
            if (stateFurtherBelow.is(ModTags.HEAT_SOURCES)) {
                if (stateFurtherBelow.hasProperty(BlockStateProperties.LIT)) {
                    if (stateFurtherBelow.getValue(BlockStateProperties.LIT)) {
                        heat += 1;
                    }
                } else {
                    heat +=1;
                }
            }
        }
        int cold = states.stream().filter(s -> s.is(BlockTags.BIRCH_LOGS)).mapToInt(s -> 1).sum();
        float temperature = level.getBiome(worldPosition).get().getBaseTemperature();
        if (temperature <= 0) {
            cold += 2;
        } else if (temperature == 2) {
            heat += 2;
        }
        kegTemperature = heat - cold;
        if (level.dimensionType().ultraWarm()) {
            kegTemperature = 5;
        }
    }

    public String getTemperature() {
        if (kegTemperature < -4) {
            return "frigid";
        } else if (kegTemperature < -1) {
            return "cold";
        } else if (kegTemperature < 2) {
            return "normal";
        } else if (kegTemperature < 5) {
            return "warm";
        } else if (kegTemperature > 4) {
            return "hot";
        }
        return "normal";
    }

    public static void animationTick(Level level, BlockPos pos, BlockState state, KegBlockEntity keg) {
    }

    private Optional<KegRecipe> getMatchingRecipe(RecipeWrapper inventoryWrapper) {
        if (level == null) return Optional.empty();

        if (lastRecipeID != null) {
            Recipe<RecipeWrapper> recipe = ((RecipeManagerAccessor) level.getRecipeManager())
                    .getRecipeMap(BCRecipeTypes.FERMENTING.get())
                    .get(lastRecipeID);
            if (recipe instanceof KegRecipe) {
                if (recipe.matches(inventoryWrapper, level)) {
                    return Optional.of((KegRecipe) recipe);
                }
                if (recipe.getResultItem().sameItem(getDrink())) {
                    return Optional.empty();
                }
            }
        }

        if (checkNewRecipe) {
            Optional<KegRecipe> recipe = level.getRecipeManager().getRecipeFor(BCRecipeTypes.FERMENTING.get(), inventoryWrapper, level);
            if (recipe.isPresent()) {
                lastRecipeID = recipe.get().getId();
                return recipe;
            }
        }

        checkNewRecipe = false;
        return Optional.empty();
    }

    public ItemStack getContainer() {
        if (!drinkContainerStack.isEmpty()) {
            return drinkContainerStack;
        } else {
            return getDrink().getCraftingRemainingItem();
        }
    }

    private boolean hasInput() {
        for (int i = 0; i < DRINK_DISPLAY_SLOT; ++i) {
            if (!inventory.getStackInSlot(i).isEmpty()) return true;
        }
        return false;
    }

    protected boolean canFerment(KegRecipe recipe) {
        String recipeTemp = recipe.getTemperature();
        if (hasInput() && (recipeTemp.equals("normal") || recipeTemp.equals(getTemperature()))) {
            ItemStack resultStack = recipe.getResultItem();
            if (resultStack.isEmpty()) {
                return false;
            } else {
                ItemStack storedMealStack = inventory.getStackInSlot(DRINK_DISPLAY_SLOT);
                if (storedMealStack.isEmpty()) {
                    return true;
                } else if (!storedMealStack.sameItem(resultStack)) {
                    return false;
                } else if (storedMealStack.getCount() + resultStack.getCount() <= inventory.getSlotLimit(DRINK_DISPLAY_SLOT)) {
                    return true;
                } else {
                    return storedMealStack.getCount() + resultStack.getCount() <= resultStack.getMaxStackSize();
                }
            }
        } else {
            return false;
        }
    }

    private boolean processFermenting(KegRecipe recipe, KegBlockEntity keg) {
        if (level == null) return false;

        ++fermentTime;
        fermentTimeTotal = recipe.getFermentTime();
        if (fermentTime < fermentTimeTotal) {
            return false;
        }

        fermentTime = 0;
        drinkContainerStack = recipe.getOutputContainer();
        ItemStack resultStack = recipe.getResultItem();
        ItemStack storedDrinkStack = inventory.getStackInSlot(DRINK_DISPLAY_SLOT);
        if (storedDrinkStack.isEmpty()) {
            inventory.setStackInSlot(DRINK_DISPLAY_SLOT, resultStack.copy());
        } else if (storedDrinkStack.sameItem(resultStack)) {
            storedDrinkStack.grow(resultStack.getCount());
        }
        keg.setRecipeUsed(recipe);

        for (int i = 0; i < DRINK_DISPLAY_SLOT; ++i) {
            ItemStack slotStack = inventory.getStackInSlot(i);
            if (slotStack.hasCraftingRemainingItem()) {
                Direction direction = getBlockState().getValue(KegBlock.FACING).getCounterClockWise();
                double x = worldPosition.getX() + 0.5 + (direction.getStepX() * 0.25);
                double y = worldPosition.getY() + 0.7;
                double z = worldPosition.getZ() + 0.5 + (direction.getStepZ() * 0.25);
                ItemUtils.spawnItemEntity(level, inventory.getStackInSlot(i).getCraftingRemainingItem(), x, y, z,
                        direction.getStepX() * 0.08F, 0.25F, direction.getStepZ() * 0.08F);
            }
            if (!slotStack.isEmpty())
                slotStack.shrink(1);
        }
        return true;
    }

    @Override
    public void setRecipeUsed(@Nullable Recipe<?> recipe) {
        if (recipe != null) {
            ResourceLocation recipeID = recipe.getId();
            usedRecipeTracker.addTo(recipeID, 1);
        }
    }

    @Nullable
    @Override
    public Recipe<?> getRecipeUsed() {
        return null;
    }

    @Override
    public void awardUsedRecipes(Player player) {
        List<Recipe<?>> usedRecipes = getUsedRecipesAndPopExperience(player.level, player.position());
        player.awardRecipes(usedRecipes);
        usedRecipeTracker.clear();
    }

    public List<Recipe<?>> getUsedRecipesAndPopExperience(Level level, Vec3 pos) {
        List<Recipe<?>> list = Lists.newArrayList();

        for (Object2IntMap.Entry<ResourceLocation> entry : usedRecipeTracker.object2IntEntrySet()) {
            level.getRecipeManager().byKey(entry.getKey()).ifPresent((recipe) -> {
                list.add(recipe);
                splitAndSpawnExperience((ServerLevel) level, pos, entry.getIntValue(), ((KegRecipe) recipe).getExperience());
            });
        }

        return list;
    }

    private static void splitAndSpawnExperience(ServerLevel level, Vec3 pos, int craftedAmount, float experience) {
        int expTotal = Mth.floor((float) craftedAmount * experience);
        float expFraction = Mth.frac((float) craftedAmount * experience);
        if (expFraction != 0.0F && Math.random() < (double) expFraction) {
            ++expTotal;
        }

        ExperienceOrb.award(level, pos, expTotal);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    //get Tank
    public FluidTank getFluidTank() {return fluidTank;}

    public ItemStack getDrink() {
        return inventory.getStackInSlot(DRINK_DISPLAY_SLOT);
    }

    public NonNullList<ItemStack> getDroppableInventory() {
        NonNullList<ItemStack> drops = NonNullList.create();
        for (int i = 0; i < INVENTORY_SIZE; ++i) {
            if (i != DRINK_DISPLAY_SLOT) {
                drops.add(inventory.getStackInSlot(i));
            }
        }
        return drops;
    }

    private void moveDrinkToOutput() {
        ItemStack drinkStack = inventory.getStackInSlot(DRINK_DISPLAY_SLOT);
        ItemStack outputStack = inventory.getStackInSlot(OUTPUT_SLOT);
        int mealCount = Math.min(drinkStack.getCount(), drinkStack.getMaxStackSize() - outputStack.getCount());
        if (outputStack.isEmpty()) {
            inventory.setStackInSlot(OUTPUT_SLOT, drinkStack.split(mealCount));
        } else if (outputStack.getItem() == drinkStack.getItem()) {
            drinkStack.shrink(mealCount);
            outputStack.grow(mealCount);
        }
    }

    private void useStoredContainersOnDrink() {
        ItemStack drinkStack = inventory.getStackInSlot(DRINK_DISPLAY_SLOT);
        ItemStack containerInputStack = inventory.getStackInSlot(CONTAINER_SLOT);
        ItemStack outputStack = inventory.getStackInSlot(OUTPUT_SLOT);

        if (isContainerValid(containerInputStack) && outputStack.getCount() < outputStack.getMaxStackSize()) {
            int smallerStackCount = Math.min(drinkStack.getCount(), containerInputStack.getCount());
            int mealCount = Math.min(smallerStackCount, drinkStack.getMaxStackSize() - outputStack.getCount());
            if (outputStack.isEmpty()) {
                containerInputStack.shrink(mealCount);
                inventory.setStackInSlot(OUTPUT_SLOT, drinkStack.split(mealCount));
            } else if (outputStack.getItem() == drinkStack.getItem()) {
                drinkStack.shrink(mealCount);
                containerInputStack.shrink(mealCount);
                outputStack.grow(mealCount);
            }
        }
    }

    public ItemStack useHeldItemOnDrink(ItemStack container) {
        if (isContainerValid(container) && !getDrink().isEmpty()) {
            container.shrink(1);
            return getDrink().split(1);
        }
        return ItemStack.EMPTY;
    }

    private boolean doesDrinkHaveContainer(ItemStack meal) {
        return !drinkContainerStack.isEmpty() || meal.hasCraftingRemainingItem();
    }

    public boolean isContainerValid(ItemStack containerItem) {
        if (containerItem.isEmpty()) return false;
        if (!drinkContainerStack.isEmpty()) {
            return drinkContainerStack.sameItem(containerItem);
        } else {
            return getDrink().getCraftingRemainingItem().sameItem(containerItem);
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
        return new KegMenu(id, player, this, kegData);
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
        //provide cap
        if (cap.equals(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)) {
            return fluidHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void setRemoved() {
        super.setRemoved();
        inputHandler.invalidate();
        outputHandler.invalidate();
        //invalidate
        fluidHandler.invalidate();
    }

    @Override
    public CompoundTag getUpdateTag() {
        return writeItems(new CompoundTag());
    }

    private ItemStackHandler createHandler() {
        return new ItemStackHandler(INVENTORY_SIZE)
        {
            @Override
            protected void onContentsChanged(int slot) {
                if (slot >= 0 && slot < DRINK_DISPLAY_SLOT) {
                    checkNewRecipe = true;
                }
                inventoryChanged();
            }
        };
    }

    private ContainerData createIntArray() {
        return new ContainerData()
        {
            @Override
            public int get(int index) {
                return switch (index) {
                    case 0 -> KegBlockEntity.this.fermentTime;
                    case 1 -> KegBlockEntity.this.fermentTimeTotal;
                    case 2 -> KegBlockEntity.this.kegTemperature;
                    default -> 0;
                };
            }

            @Override
            public void set(int index, int value) {
                switch (index) {
                    case 0 -> KegBlockEntity.this.fermentTime = value;
                    case 1 -> KegBlockEntity.this.fermentTimeTotal = value;
                    case 2 -> KegBlockEntity.this.kegTemperature = value;
                }
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
    }
}