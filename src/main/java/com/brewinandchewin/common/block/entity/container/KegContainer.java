package com.brewinandchewin.common.block.entity.container;

import java.util.Objects;

import com.brewinandchewin.common.block.entity.KegBlockEntity;
import com.brewinandchewin.core.BrewinAndChewin;
import com.brewinandchewin.core.registry.BCBlocks;
import com.brewinandchewin.core.registry.BCContainerTypes;
import com.brewinandchewin.core.registry.BCItems;
import com.mojang.datafixers.util.Pair;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;


public class KegContainer extends AbstractContainerMenu
{
	public static final ResourceLocation EMPTY_CONTAINER_SLOT_MUG = new ResourceLocation(BrewinAndChewin.MODID, "item/empty_container_slot_mug");

	public final KegBlockEntity tileEntity;
	public final ItemStackHandler inventory;
	private final ContainerData kegData;
	private final ContainerLevelAccess canInteractWithCallable;

	public KegContainer(final int windowId, final Inventory playerInventory, final KegBlockEntity tileEntity, ContainerData kegDataIn) {
		super(BCContainerTypes.KEG.get(), windowId);
		this.tileEntity = tileEntity;
		this.inventory = tileEntity.getInventory();
		this.kegData = kegDataIn;
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
		this.addSlot(new SlotItemHandler(inventory, 4, 85, 18));

		// Meal Display
		this.addSlot(new KegMealSlot(inventory, 5, 122, 23));

		// Bowl Input
		this.addSlot(new SlotItemHandler(inventory, 6, 90, 55)
		{
			@OnlyIn(Dist.CLIENT)
			public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
				return Pair.of(InventoryMenu.BLOCK_ATLAS, EMPTY_CONTAINER_SLOT_MUG);
			}
		});

		// Bowl Output
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

	public KegContainer(final int windowId, final Inventory playerInventory, final FriendlyByteBuf data) {
		this(windowId, playerInventory, getTileEntity(playerInventory, data), new SimpleContainerData(4));
	}

	@Override
	public boolean stillValid(Player playerIn) {
		return stillValid(canInteractWithCallable, playerIn, BCBlocks.KEG.get());
	}

	@Override
	public ItemStack quickMoveStack(Player playerIn, int index) {
		int indexMealDisplay = 5;
		int indexContainerInput = 6;
		int indexOutput = 7;
		int startPlayerInv = indexOutput + 1;
		int endPlayerInv = startPlayerInv + 36;
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.slots.get(index);
		if (slot != null && slot.hasItem()) {
			ItemStack itemstack1 = slot.getItem();
			itemstack = itemstack1.copy();
			if (index == indexOutput) {
				if (!this.moveItemStackTo(itemstack1, startPlayerInv, endPlayerInv, true)) {
					return ItemStack.EMPTY;
				}
			} else if (index > indexOutput) {
				if (itemstack1.getItem() == BCItems.TANKARD.get() && !this.moveItemStackTo(itemstack1, indexContainerInput, indexContainerInput + 1, false)) {
					return ItemStack.EMPTY;
				} else if (!this.moveItemStackTo(itemstack1, 0, indexMealDisplay, false)) {
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
	
	public int getFermentingTicks() {
		return this.kegData.get(0);
	}

	@OnlyIn(Dist.CLIENT)
	public int getFermentProgressionScaled() {
		int i = this.kegData.get(0);
		int j = this.kegData.get(1);
		return j != 0 && i != 0 ? i * 33 / j : 0;
	}
	
	@OnlyIn(Dist.CLIENT)
	public int getTemperature() {
		return this.tileEntity.heat - this.tileEntity.cold;
	}
}
