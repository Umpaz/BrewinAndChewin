package umpaz.brewinandchewin.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.ChatFormatting;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.client.gui.screens.recipebook.RecipeUpdateListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ClickType;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.widget.FluidWidget;
import umpaz.brewinandchewin.common.block.entity.container.KegMenu;
import umpaz.brewinandchewin.common.utility.BCTextUtils;
import vectorwing.farmersdelight.common.Configuration;
import vectorwing.farmersdelight.common.utility.TextUtils;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

@ParametersAreNonnullByDefault
public class KegScreen extends AbstractContainerScreen<KegMenu> implements RecipeUpdateListener
{
    private static final ResourceLocation RECIPE_BUTTON_LOCATION = new ResourceLocation("textures/gui/recipe_button.png");
    private static final ResourceLocation BACKGROUND_TEXTURE = new ResourceLocation(BrewinAndChewin.MODID, "textures/gui/keg.png");
    private static final Rectangle PROGRESS_ARROW = new Rectangle(77, 44, 0, 9);
    private static final Rectangle FRIGID_BAR = new Rectangle(77, 39, 6, 4);
    private static final Rectangle COLD_BAR = new Rectangle(83, 39, 7, 4);
    private static final Rectangle WARM_BAR = new Rectangle(96, 39, 7, 4);
    private static final Rectangle HOT_BAR = new Rectangle(103, 39, 7, 4);

    private static final Rectangle BUBBLE_1 = new Rectangle(74, 14, 9, 24);
    private static final Rectangle BUBBLE_2 = new Rectangle(103, 14, 9, 24);
    private static final int[] BUBBLELENGTHS = new int[]{24, 20, 16, 12, 8, 4, 0};

    private final KegRecipeBookComponent recipeBookComponent = new KegRecipeBookComponent();
    private boolean widthTooNarrow;

    public KegScreen(KegMenu screenContainer, Inventory inv, Component titleIn) {
        super(screenContainer, inv, titleIn);
    }

    @Override
    public void init() {
        super.init();
        this.widthTooNarrow = this.width < 379;
        this.titleLabelX = 32;
        this.titleLabelY = 17;
        this.recipeBookComponent.init(this.width, this.height, this.minecraft, this.widthTooNarrow, this.menu);
        this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        if (Configuration.ENABLE_RECIPE_BOOK_COOKING_POT.get()) {
            this.addRenderableWidget(new ImageButton(this.leftPos + 5, this.height / 2 - 49, 20, 18, 0, 0, 19, RECIPE_BUTTON_LOCATION, (button) ->
            {
                this.recipeBookComponent.toggleVisibility();
                this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
                ((ImageButton) button).setPosition(this.leftPos + 5, this.height / 2 - 49);
            }));
        } else {
            this.recipeBookComponent.hide();
            this.leftPos = this.recipeBookComponent.updateScreenPosition(this.width, this.imageWidth);
        }
        this.addWidget(this.recipeBookComponent);
        this.addRenderableOnly(new FluidWidget(this,this.leftPos + 85,this.topPos + 18,16,16, this.menu.fluidTank));
        this.setInitialFocus(this.recipeBookComponent);
    }

    @Override
    protected void containerTick() {
        super.containerTick();
        this.recipeBookComponent.tick();
    }

    @Override
    public void render(PoseStack ms, final int mouseX, final int mouseY, float partialTicks) {
        this.renderBackground(ms);

        if (this.recipeBookComponent.isVisible() && this.widthTooNarrow) {
            this.renderBg(ms, partialTicks, mouseX, mouseY);
            this.recipeBookComponent.render(ms, mouseX, mouseY, partialTicks);
        } else {
            this.recipeBookComponent.render(ms, mouseX, mouseY, partialTicks);
            super.render(ms, mouseX, mouseY, partialTicks);
            this.recipeBookComponent.renderGhostRecipe(ms, this.leftPos, this.topPos, false, partialTicks);
        }

        this.renderTemperatureTooltip(ms, mouseX, mouseY);
        this.renderDrinkDisplayTooltip(ms, mouseX, mouseY);
        this.recipeBookComponent.renderTooltip(ms, this.leftPos, this.topPos, mouseX, mouseY);
    }

    private void renderTemperatureTooltip(PoseStack ms, int mouseX, int mouseY) {
        if (this.isHovering(77, 39, 27, 4, mouseX, mouseY)) {
            List<Component> tooltip = new ArrayList<>();
            MutableComponent key = null;
            int i = this.menu.getTemperature();

            if (i < -4) {
                key = BCTextUtils.getTranslation("container.keg.frigid", i);
            } else if (i < -1) {
                key = BCTextUtils.getTranslation("container.keg.cold", i);
            } else if (i < 2) {
                key = BCTextUtils.getTranslation("container.keg.normal", i);
            } else if (i < 5) {
                key = BCTextUtils.getTranslation("container.keg.warm", i);
            } else {
                key = BCTextUtils.getTranslation("container.keg.hot", i);
            }
            tooltip.add(key);
            this.renderComponentTooltip(ms, tooltip, mouseX, mouseY);
        }
    }

    protected void renderDrinkDisplayTooltip(PoseStack ms, int mouseX, int mouseY) {
        if (this.minecraft != null && this.minecraft.player != null && this.menu.getCarried().isEmpty() && this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
            if (this.hoveredSlot.index == 5) {
                List<Component> tooltip = new ArrayList<>();

                ItemStack drinkStack = this.hoveredSlot.getItem();
                tooltip.add(((MutableComponent) drinkStack.getItem().getDescription()).withStyle(drinkStack.getRarity().color));

                ItemStack containerStack = this.menu.tileEntity.getContainer();
                String container = !containerStack.isEmpty() ? containerStack.getItem().getDescription().getString() : "";

                tooltip.add(TextUtils.getTranslation("container.cooking_pot.served_on", container).withStyle(ChatFormatting.GRAY));

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
        if (temp < -1) {
            this.blit(ms, this.leftPos + COLD_BAR.x, this.topPos + COLD_BAR.y, 182, 0, COLD_BAR.width, COLD_BAR.height);
        } if (temp < -4) {
            this.blit(ms, this.leftPos + FRIGID_BAR.x, this.topPos + FRIGID_BAR.y, 176, 0, FRIGID_BAR.width, FRIGID_BAR.height);
        } if (temp > 1) {
            this.blit(ms, this.leftPos + WARM_BAR.x, this.topPos + WARM_BAR.y, 195, 0, WARM_BAR.width, WARM_BAR.height);
        } if (temp > 4) {
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

    @Override
    protected boolean isHovering(int x, int y, int width, int height, double mouseX, double mouseY) {
        return (!this.widthTooNarrow || !this.recipeBookComponent.isVisible()) && super.isHovering(x, y, width, height, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int buttonId) {
        if (this.recipeBookComponent.mouseClicked(mouseX, mouseY, buttonId)) {
            this.setFocused(this.recipeBookComponent);
            return true;
        }
        return this.widthTooNarrow && this.recipeBookComponent.isVisible() || super.mouseClicked(mouseX, mouseY, buttonId);
    }

    @Override
    protected boolean hasClickedOutside(double mouseX, double mouseY, int x, int y, int buttonIdx) {
        boolean flag = mouseX < (double) x || mouseY < (double) y || mouseX >= (double) (x + this.imageWidth) || mouseY >= (double) (y + this.imageHeight);
        return flag && this.recipeBookComponent.hasClickedOutside(mouseX, mouseY, this.leftPos, this.topPos, this.imageWidth, this.imageHeight, buttonIdx);
    }

    @Override
    protected void slotClicked(Slot slot, int mouseX, int mouseY, ClickType clickType) {
        super.slotClicked(slot, mouseX, mouseY, clickType);
        this.recipeBookComponent.slotClicked(slot);
    }

    @Override
    public void recipesUpdated() {
        this.recipeBookComponent.recipesUpdated();
    }

    @Override
    public void removed() {
        this.recipeBookComponent.removed();
        super.removed();
    }

    @Override
    @Nonnull
    public RecipeBookComponent getRecipeBookComponent() {
        return this.recipeBookComponent;
    }
}

