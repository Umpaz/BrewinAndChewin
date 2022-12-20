package umpaz.brewinandchewin.common;

import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

public class BCCommonSetup
{
	public static void init(final FMLCommonSetupEvent event) {
		event.enqueueWork(() -> {
			registerCompostables();
			registerLootItemFunctions();
		});
	}

	public static void registerCompostables() {
		// 30% chance
		//ComposterBlock.COMPOSTABLES.put(FRItems.GREEN_TEA_LEAVES.get(), 0.3F);
	}

	public static void registerLootItemFunctions() {
		//LootItemFunctions.register(BCCopyMealFunction.ID.toString(), new BCCopyMealFunction.Serializer());
	}
}
