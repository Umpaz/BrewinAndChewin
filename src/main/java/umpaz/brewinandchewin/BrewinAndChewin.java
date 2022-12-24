package umpaz.brewinandchewin;

import net.minecraft.world.inventory.RecipeBookType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import umpaz.brewinandchewin.client.BCClientSetup;
import umpaz.brewinandchewin.common.BCCommonSetup;
import umpaz.brewinandchewin.common.registry.*;

import javax.annotation.Nonnull;

@Mod(BrewinAndChewin.MODID)
public class BrewinAndChewin
{
	public static final String MODID = "brewinandchewin";
	public static final Logger LOGGER = LogManager.getLogger();

	public static final RecipeBookType RECIPE_TYPE_FERMENTING = RecipeBookType.create("FERMENTING");

	public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(BrewinAndChewin.MODID)
	{
		@Nonnull
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(BCBlocks.KEG.get());
		}
	};

	public BrewinAndChewin() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(BCCommonSetup::init);
		modEventBus.addListener(BCClientSetup::init);

		//ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, FRConfiguration.COMMON_CONFIG);

		//FREffects.EFFECTS.register(modEventBus);
		BCItems.ITEMS.register(modEventBus);
		BCBlocks.BLOCKS.register(modEventBus);
		BCBlockEntityTypes.TILES.register(modEventBus);
		BCMenuTypes.MENU_TYPES.register(modEventBus);
		BCRecipeSerializers.RECIPE_SERIALIZERS.register(modEventBus);
		BCRecipeTypes.RECIPE_TYPES.register(modEventBus);
		//FRSounds.SOUNDS.register(modEventBus);

		//modEventBus.addListener(this::clientSetup);

		//MinecraftForge.EVENT_BUS.addListener(VillageStructures::addNewVillageBuilding);

		MinecraftForge.EVENT_BUS.register(this);

		//add milk fluid
		ForgeMod.enableMilkFluid();
	}
}
