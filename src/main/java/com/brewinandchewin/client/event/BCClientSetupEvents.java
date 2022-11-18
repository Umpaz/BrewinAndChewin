package com.brewinandchewin.client.event;

import com.brewinandchewin.client.renderer.ItemMatRenderer;
import com.brewinandchewin.core.BrewinAndChewin;

import com.brewinandchewin.core.registry.BCBlockEntityTypes;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import vectorwing.farmersdelight.client.renderer.CanvasSignRenderer;
import vectorwing.farmersdelight.client.renderer.CuttingBoardRenderer;
import vectorwing.farmersdelight.client.renderer.SkilletRenderer;
import vectorwing.farmersdelight.client.renderer.StoveRenderer;
import vectorwing.farmersdelight.common.registry.ModBlockEntityTypes;

@Mod.EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BCClientSetupEvents
{
	public static final ResourceLocation EMPTY_CONTAINER_SLOT_MUG = new ResourceLocation(BrewinAndChewin.MODID, "item/empty_container_slot_mug");

	
	@SubscribeEvent
	public static void onStitchEvent(TextureStitchEvent.Pre event) {
		event.addSprite(EMPTY_CONTAINER_SLOT_MUG);
	}

	@SubscribeEvent
	public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
		event.registerBlockEntityRenderer(BCBlockEntityTypes.ITEM_MAT.get(), ItemMatRenderer::new);
	}
}
