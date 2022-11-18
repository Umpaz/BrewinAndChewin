package com.brewinandchewin.client;

import com.brewinandchewin.client.gui.KegScreen;
import com.brewinandchewin.core.registry.BCBlocks;
import com.brewinandchewin.core.registry.BCContainerTypes;
import com.mojang.blaze3d.platform.ScreenManager;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.ScreenEffectRenderer;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import vectorwing.farmersdelight.common.registry.ModBlocks;

public class BCClientSetup
{
	public static void init(final FMLClientSetupEvent event) {
	//	ItemBlockRenderTypes.setRenderLayer(BCBlocks.PIZZA.get(), RenderType.translucent());
		ItemBlockRenderTypes.setRenderLayer(BCBlocks.ITEM_MAT.get(), RenderType.cutout());

		MenuScreens.register(BCContainerTypes.KEG.get(), KegScreen::new);
	}
}
