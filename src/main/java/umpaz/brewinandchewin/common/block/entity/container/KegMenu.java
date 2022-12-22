package umpaz.brewinandchewin.common.block.entity.container;

import com.mojang.datafixers.util.Pair;
import net.minecraft.client.gui.screens.inventory.CraftingScreen;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.recipebook.ServerPlaceRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.StackedContents;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.PotionUtils;
import net.minecraft.world.item.alchemy.Potions;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.material.Fluids;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import org.jetbrains.annotations.NotNull;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.block.KegBlock;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;
import umpaz.brewinandchewin.common.block.entity.inventory.KegServerPlaceRecipe;
import umpaz.brewinandchewin.common.registry.BCBlocks;
import umpaz.brewinandchewin.common.registry.BCMenuTypes;
import vectorwing.farmersdelight.common.block.entity.container.CookingPotMealSlot;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class KegMenu extends RecipeBookMenu<RecipeWrapper>
{
    public static final ResourceLocation EMPTY_CONTAINER_SLOT_MUG = new ResourceLocation(BrewinAndChewin.MODID, "item/empty_container_slot_mug");

    public final KegBlockEntity tileEntity;
    public final ItemStackHandler inventory;
    private final ContainerData kegData;
    private final ContainerLevelAccess canInteractWithCallable;
    protected final Level level;
    //fluid tank access
    public final FluidTank fluidTank;

    public KegMenu(final int windowId, final Inventory playerInventory, final KegBlockEntity tileEntity, ContainerData kegDataIn) {
        super(BCMenuTypes.KEG.get(), windowId);
        this.tileEntity = tileEntity;
        this.inventory = tileEntity.getInventory();
        this.fluidTank = tileEntity.getFluidTank();
        this.kegData = kegDataIn;
        this.level = playerInventory.player.level;
        this.canInteractWithCallable = ContainerLevelAccess.create(tileEntity.getLevel(), tileEntity.getBlockPos());

        // Ingredient Slots - 2 Rows x 2 Columns
        int startX = 8;
        int startY = 18;
        int inputStartX = 33;
        int inputStartY = 28;
        int borderSlotSize = 18;
        for (int row = 0; row < 2; ++row) {
            for (int column = 0; column < 2; ++column) {
                this.addSlot(new SlotItemHandler(inventory, (row * 2) + column,
                        inputStartX + (column * borderSlotSize),
                        inputStartY + (row * borderSlotSize)));
            }
        }

        // Fluid Recipe Slot
        //Here I've placed the logic in the "slot". This can also be done inside the itemhandler
        this.addSlot(new SlotItemHandler(inventory, 4, 85, 18) {
            //Only allow items with a fluid in it
            @Override
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent() ||
                PotionUtils.getPotion(stack).equals(Potions.WATER);
            }

            //Handle fluid interactions when placed
            @Override
            public void set(@NotNull ItemStack stack) {
                //Drain to tank if possible
                AtomicReference<ItemStack> result = new AtomicReference<>(stack);
                stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).ifPresent(cap -> {
                    FluidUtil.tryFluidTransfer(KegMenu.this.fluidTank,cap, 1000, true );
                    //for buckets, this will be the empty bucket, for others, this should be the same as stack
                    result.set(cap.getContainer());
                });
                //Handle bottles
                if (PotionUtils.getPotion(stack).equals(Potions.WATER)) {
                    int amount = KegMenu.this.fluidTank.fill(new FluidStack(Fluids.WATER, 333), IFluidHandler.FluidAction.SIMULATE);
                    //Cancel if the full bottle can't be emptied;
                    if (amount != 333) {
                        return;
                    }
                    //Actually fill, and add 1 if it would have been 999.
                    KegMenu.this.fluidTank.fill(new FluidStack(Fluids.WATER, KegMenu.this.fluidTank.getFluidAmount() == 666? 334 : 333), IFluidHandler.FluidAction.EXECUTE);
                    //Empty bottle
                    result.set(new ItemStack(Items.GLASS_BOTTLE));
                }
                super.set(result.get());
            }
        });

        // Drink Display
        this.addSlot(new CookingPotMealSlot(inventory, 5, 122, 23));

        // Mug Input
        this.addSlot(new SlotItemHandler(inventory, 6, 90, 55)
        {
            @OnlyIn(Dist.CLIENT)
            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_CONTAINER_SLOT_MUG);
            }
        });

        // Mug Output
        this.addSlot(new KegResultSlot(playerInventory.player, tileEntity, inventory, 7, 122, 55));

        // Main Player Inventory
        int startPlayerInvY = startY * 4 + 12;
        for (int row = 0; row < 3; ++row) {
            for (int column = 0; column < 9; ++column) {
                this.addSlot(new Slot(playerInventory, 9 + (row * 9) + column, startX + (column * borderSlotSize),
                        startPlayerInvY + (row * borderSlotSize)));
            }
        }

        // Hotbar
        for (int column = 0; column < 9; ++column) {
            this.addSlot(new Slot(playerInventory, column, startX + (column * borderSlotSize), 142));
        }

        this.addDataSlots(kegDataIn);
    }

    private static KegBlockEntity getTileEntity(final Inventory playerInventory, final FriendlyByteBuf data) {
        Objects.requireNonNull(playerInventory, "playerInventory cannot be null");
        Objects.requireNonNull(data, "data cannot be null");
        final BlockEntity tileAtPos = playerInventory.player.level.getBlockEntity(data.readBlockPos());
        if (tileAtPos instanceof KegBlockEntity) {
            return (KegBlockEntity) tileAtPos;
        }
        throw new IllegalStateException("Tile entity is not correct! " + tileAtPos);
    }

    public KegMenu(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
        this(windowId, playerInventory, getTileEntity(playerInventory, data), new SimpleContainerData(4));
    }

    @Override
    public boolean stillValid(Player playerIn) {
        return stillValid(canInteractWithCallable, playerIn, BCBlocks.KEG.get());
    }

    @Override
    public ItemStack quickMoveStack(Player playerIn, int index) {
        int indexDrinkDisplay = 5;
        int indexContainerInput = 6;
        int indexOutput = 7;
        int startPlayerInv = indexOutput + 1;
        int endPlayerInv = startPlayerInv + 36;
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemstack1 = slot.getItem();
            itemstack = itemstack1.copy();
            if (index == indexOutput) {
                if (!this.moveItemStackTo(itemstack1, startPlayerInv, endPlayerInv, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (index > indexOutput) {
                if (itemstack1.getItem() == Items.BOWL && !this.moveItemStackTo(itemstack1, indexContainerInput, indexContainerInput + 1, false)) {
                    return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(itemstack1, 0, indexDrinkDisplay, false)) {
                    return ItemStack.EMPTY;
                } else if (!this.moveItemStackTo(itemstack1, indexContainerInput, indexOutput, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (!this.moveItemStackTo(itemstack1, startPlayerInv, endPlayerInv, false)) {
                return ItemStack.EMPTY;
            }

            if (itemstack1.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemstack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(playerIn, itemstack1);
        }
        return itemstack;
    }

    @OnlyIn(Dist.CLIENT)
    public int getFermentProgressionScaled() {
        int i = this.kegData.get(0);
        int j = this.kegData.get(1);
        return j != 0 && i != 0 ? i * 33 / j : 0;
    }

    @OnlyIn(Dist.CLIENT)
    public int getFermentingTicks() {
        return this.kegData.get(0);
    }

    @OnlyIn(Dist.CLIENT)
    public int getTemperature() {
        return this.kegData.get(2);
    }

    @Override
    public void fillCraftSlotsStackedContents(StackedContents helper) {
        for (int i = 0; i < inventory.getSlots(); i++) {
            helper.accountSimpleStack(inventory.getStackInSlot(i));
        }
    }

    @Override
    public void clearCraftingContent() {
        for (int i = 0; i < 5; i++) {
            this.inventory.setStackInSlot(i, ItemStack.EMPTY);
        }
    }

    @Override
    public boolean recipeMatches(Recipe<? super RecipeWrapper> recipe) {
        return recipe.matches(new RecipeWrapper(inventory), level);
    }

    @Override
    public int getResultSlotIndex() {
        return 6;
    }

    @Override
    public int getGridWidth() {
        return 2;
    }

    @Override
    public int getGridHeight() {
        return 2;
    }

    @Override
    public int getSize() {
        return 6;
    }

    @Override
    public RecipeBookType getRecipeBookType() {
        return BrewinAndChewin.RECIPE_TYPE_FERMENTING;
    }

    @Override
    public boolean shouldMoveToInventory(int slot) {
        return slot < 5;
    }

    @Override
    public void handlePlacement(boolean p_40119_, Recipe<?> recipe, ServerPlayer player) {
        new KegServerPlaceRecipe(this).recipeClicked(player, recipe, p_40119_);
    }

}