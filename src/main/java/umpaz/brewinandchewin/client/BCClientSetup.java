package umpaz.brewinandchewin.client;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import umpaz.brewinandchewin.client.gui.KegScreen;
import umpaz.brewinandchewin.common.registry.BCMenuTypes;

public class BCClientSetup
{
	public static void init(final FMLClientSetupEvent event) {
		event.enqueueWork(() -> MenuScreens.register(BCMenuTypes.KEG.get(), KegScreen::new));
	}
}
