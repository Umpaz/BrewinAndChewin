package com.brewinandchewin.client.gui;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.brewinandchewin.common.block.entity.container.KegContainer;
import com.brewinandchewin.core.BrewinAndChewin;
import com.brewinandchewin.core.utility.BCTextUtils;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

@ParametersAreNonnullByDefault
public class KegScreen extends AbstractContainerScreen<KegContainer>
{
	private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(BrewinAndChewin.MODID, "textures/gui/keg.png");
	private static final Rectangle PROGRESS_ARROW = new Rectangle(77, 44, 0, 9);
	private static final Rectangle FRIGID_BAR = new Rectangle(77, 39, 6, 4);
	private static final Rectangle COLD_BAR = new Rectangle(83, 39, 7, 4);
	private static final Rectangle WARM_BAR = new Rectangle(96, 39, 7, 4);
	private static final Rectangle HOT_BAR = new Rectangle(103, 39, 7, 4);

	private static final Rectangle BUBBLE_1 = new Rectangle(74, 14, 9, 24);
	private static final Rectangle BUBBLE_2 = new Rectangle(103, 14, 9, 24);
	private static final int[] BUBBLELENGTHS = new int[]{24, 20, 16, 12, 8, 4, 0};

	public KegScreen(KegContainer screenContainer, Inventory inv, Component titleIn) {
		super(screenContainer, inv, titleIn);
		this.leftPos = 0;
		this.topPos = 0;
		this.imageWidth = 176;
		this.imageHeight = 166;
		this.titleLabelX = 32;
		this.titleLabelY = 17;
	}

	@Override
	public void render(PoseStack ms, final int mouseX, final int mouseY, float partialTicks) {
		this.renderBackground(ms);
		super.render(ms, mouseX, mouseY, partialTicks);
		this.renderTemperatureTooltip(ms, mouseX, mouseY);
		this.renderMealDisplayTooltip(ms, mouseX, mouseY);
	}
	
	private void renderTemperatureTooltip(PoseStack ms, int mouseX, int mouseY) {
		if (this.isHovering(77, 39, 27, 4, mouseX, mouseY)) {
			List<Component> tooltip = new ArrayList<>();
			MutableComponent key = null;
			int i = this.menu.getTemperature();
			if (i < -8) {
				key = BCTextUtils.getTranslation("container.keg.frigid");
			}
			if (i < -4 && i > -9) {
				key = BCTextUtils.getTranslation("container.keg.cold");
			}
			if (i < 5 && i > -5) {
				key = BCTextUtils.getTranslation("container.keg.normal");
			}
			if (i > 4 && i < 9) {
				key = BCTextUtils.getTranslation("container.keg.warm");
			}
			if (i > 8) {
				key = BCTextUtils.getTranslation("container.keg.hot");
			}
			tooltip.add(key);
			this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
		}
	}

	protected void renderMealDisplayTooltip(PoseStack ms, int mouseX, int mouseY) {
		if (this.minecraft != null && this.minecraft.player != null && this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
			if (this.hoveredSlot.index == 5) {
				List<Component> tooltip = new ArrayList<>();

				ItemStack mealStack = this.hoveredSlot.getItem();
				tooltip.add(((MutableComponent) mealStack.getItem().getDescription()).withStyle(mealStack.getRarity().color));

				ItemStack containerStack = this.menu.tileEntity.getContainer();
				String container = !containerStack.isEmpty() ? containerStack.getItem().getDescription().getString() : "";

				tooltip.add(BCTextUtils.getTranslation("container.keg.served_in", container).withStyle(ChatFormatting.GRAY));

				this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
			} else {
				this.renderTooltip(ms, this.hoveredSlot.getItem(), mouseX, mouseY);
			}
		}
	}

	@Override
	protected void renderLabels(PoseStack ms, int mouseX, int mouseY) {
		super.renderLabels(ms, mouseX, mouseY);
		this.font.draw(ms, this.playerInventoryTitle, 8.0f, (float) (this.imageHeight - 96 + 2), 4210752);
	}

	@Override
	protected void renderBg(PoseStack ms, float partialTicks, int mouseX, int mouseY) {
		// Render UI background
		RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
		if (this.minecraft == null)
			return;
		
		RenderSystem.setShaderTexture(0, BACKGROUND_TEXTURE);
		this.blit(ms, this.leftPos, this.topPos, 0, 0, this.imageWidth, this.imageHeight);
		
		
		// Render progress arrow
		int l = this.menu.getFermentProgressionScaled();
		this.blit(ms, this.leftPos + PROGRESS_ARROW.x, this.topPos + PROGRESS_ARROW.y, 176, 28, l + 1, PROGRESS_ARROW.height);
		
		int temp = this.menu.getTemperature();
		if (temp < -4 && temp > -9) {
			this.blit(ms, this.leftPos + COLD_BAR.x, this.topPos + COLD_BAR.y, 182, 0, COLD_BAR.width, COLD_BAR.height);
		}
		if (temp < -8) {
			this.blit(ms, this.leftPos + COLD_BAR.x, this.topPos + COLD_BAR.y, 182, 0, COLD_BAR.width, COLD_BAR.height);
			this.blit(ms, this.leftPos + FRIGID_BAR.x, this.topPos + FRIGID_BAR.y, 176, 0, FRIGID_BAR.width, FRIGID_BAR.height);
		}
		if (temp > 4 && temp < 9) {
			this.blit(ms, this.leftPos + WARM_BAR.x, this.topPos + WARM_BAR.y, 195, 0, WARM_BAR.width, WARM_BAR.height);
		}
		if (temp > 8) {
			this.blit(ms, this.leftPos + WARM_BAR.x, this.topPos + WARM_BAR.y, 195, 0, WARM_BAR.width, WARM_BAR.height);
			this.blit(ms, this.leftPos + HOT_BAR.x, this.topPos + HOT_BAR.y, 202, 0, HOT_BAR.width, HOT_BAR.height);
		}
		
		int i = this.menu.getFermentingTicks();
		if (i > 0) {
			int j;
			j = BUBBLELENGTHS[i / 5 % 7];
			this.blit(ms, this.leftPos + BUBBLE_1.x, this.topPos + BUBBLE_1.y, 176, 4, BUBBLE_1.width, BUBBLE_1.height - j);
			this.blit(ms, this.leftPos + BUBBLE_2.x, this.topPos + BUBBLE_2.y, 186, 4, BUBBLE_2.width, BUBBLE_2.height - j);
		}
	}
}
