package umpaz.brewinandchewin.data;

import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import umpaz.brewinandchewin.BrewinAndChewin;

@Mod.EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BCDataGenerators {

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        ExistingFileHelper helper = event.getExistingFileHelper();

        /*FRBlockTags blockTags = new FRBlockTags(generator, BrewinAndChewin.MODID, helper);
        generator.addProvider(event.includeServer(), blockTags);
        generator.addProvider(event.includeServer(), new FRItemTags(generator, blockTags, BrewinAndChewin.MODID, helper));
        generator.addProvider(event.includeServer(), new FRRecipes(generator));
        //generator.addProvider(event.includeServer(), new Advancements(generator));
        if (event.includeClient()) {
            generator.addProvider(event.includeClient(), new FRLang(generator));
        }*/
    }
}
