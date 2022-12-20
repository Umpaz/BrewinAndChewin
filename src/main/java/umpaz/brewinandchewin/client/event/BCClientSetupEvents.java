package umpaz.brewinandchewin.client.event;

import net.minecraft.client.Minecraft;
import net.minecraft.client.color.block.BlockColors;
import net.minecraft.client.renderer.BiomeColors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.FoliageColor;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.recipebook.BCRecipeCategories;
import umpaz.brewinandchewin.common.registry.BCBlocks;

@Mod.EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class BCClientSetupEvents {
	public static final ResourceLocation EMPTY_CONTAINER_SLOT_MUG = new ResourceLocation(BrewinAndChewin.MODID, "item/empty_container_slot_mug");

	@SubscribeEvent
	public static void onRegisterRecipeBookCategories(RegisterRecipeBookCategoriesEvent event) {
		BCRecipeCategories.init(event);
	}

	@SubscribeEvent
	public static void onStitchEvent(TextureStitchEvent.Pre event) {
		event.addSprite(EMPTY_CONTAINER_SLOT_MUG);
	}

}
