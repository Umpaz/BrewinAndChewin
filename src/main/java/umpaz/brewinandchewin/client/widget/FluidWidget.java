package umpaz.brewinandchewin.client.widget;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.AbstractTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FastColor;
import net.minecraftforge.client.extensions.common.IClientFluidTypeExtensions;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;

import java.util.Arrays;

public class FluidWidget extends AbstractWidget {

    private final FluidTank fluidTank;
    private final Screen screen;

    public FluidWidget(Screen screen, int x, int y, int width, int height, FluidTank fluidTank) {
        super(x, y, width, height, Component.literal("").append(fluidTank.getFluid().getDisplayName()).append(" " + fluidTank.getFluid().getAmount() + " mB"));
        this.fluidTank = fluidTank;
        this.screen = screen;
    }

    @Override
    public void render(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
        Minecraft minecraft = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.defaultBlendFunc();
        RenderSystem.enableDepthTest();
        if (!fluidTank.isEmpty()) {
            FluidStack fluidStack = fluidTank.getFluid();
            IClientFluidTypeExtensions props = IClientFluidTypeExtensions.of(fluidStack.getFluid());
            ResourceLocation still = props.getStillTexture(fluidStack);
            if (still != null) {
                AbstractTexture texture = minecraft.getTextureManager().getTexture(TextureAtlas.LOCATION_BLOCKS);
                if (texture instanceof TextureAtlas atlas) {
                    TextureAtlasSprite sprite = atlas.getSprite(still);
                    RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);

                    int color = props.getTintColor();
                    RenderSystem.setShaderColor(FastColor.ARGB32.red(color) / 255.0F, FastColor.ARGB32.green(color) / 255.0F,
                            FastColor.ARGB32.blue(color) / 255.0F, FastColor.ARGB32.alpha(color) / 255.0F);
                    RenderSystem.enableBlend();

                    int atlasWidth = (int) (sprite.getWidth() / (sprite.getU1() - sprite.getU0()));
                    int atlasHeight = (int) (sprite.getHeight() / (sprite.getV1() - sprite.getV0()));
                    blit(poseStack, x, y, width, height, sprite.getU0() * atlasWidth, sprite.getV0() * atlasHeight, sprite.getWidth(), sprite.getHeight(),
                            atlasWidth, atlasHeight);
                    RenderSystem.setShaderColor(1, 1, 1, 1);

                }
            }
            if (isHovered(mouseX,mouseY)) {
                screen.renderTooltip(poseStack, Arrays.asList(fluidTank.getFluid().getDisplayName().getVisualOrderText(), Component.literal(fluidTank.getFluid().getAmount() + " mB").getVisualOrderText()), mouseX, mouseY);
            }
        }
    }

    @Override
    public void updateNarration(NarrationElementOutput p_169152_) {

    }

    protected boolean isHovered(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    public void setPos(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
