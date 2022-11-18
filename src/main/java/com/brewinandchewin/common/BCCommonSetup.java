package com.brewinandchewin.common;

import com.brewinandchewin.common.loot.CopyMealFunction;

import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class BCCommonSetup {

	public static void init(final FMLCommonSetupEvent event) {
		LootItemFunctions.register(CopyMealFunction.ID.toString(), new CopyMealFunction.Serializer());
	}
}
