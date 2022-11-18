package com.brewinandchewin.core;

import javax.annotation.Nonnull;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.brewinandchewin.client.BCClientSetup;
import com.brewinandchewin.common.BCCommonSetup;
import com.brewinandchewin.common.crafting.KegRecipe;
import com.brewinandchewin.common.effect.TipsyEffect;
import com.brewinandchewin.core.registry.BCBlockEntityTypes;
import com.brewinandchewin.core.registry.BCBlocks;
import com.brewinandchewin.core.registry.BCContainerTypes;
import com.brewinandchewin.core.registry.BCEffects;
import com.brewinandchewin.core.registry.BCItems;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.PotionEvent.PotionRemoveEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(BrewinAndChewin.MODID)
@Mod.EventBusSubscriber(modid = BrewinAndChewin.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class BrewinAndChewin
{
	public static final String MODID = "brewinandchewin";
	public static final CreativeModeTab CREATIVE_TAB = new CreativeModeTab(BrewinAndChewin.MODID)	{
		@Nonnull
		@Override
		public ItemStack makeIcon() {
			return new ItemStack(BCBlocks.KEG.get());
		}
	};
		
	public static final Logger LOGGER = LogManager.getLogger();
	public static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
	
	public BrewinAndChewin() {
		final IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

		modEventBus.addListener(BCCommonSetup::init);
		modEventBus.addListener(BCClientSetup::init);
		modEventBus.addGenericListener(RecipeSerializer.class, this::registerRecipeSerializers);

		//ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Configuration.COMMON_CONFIG);
		//ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Configuration.CLIENT_CONFIG);

		BCItems.ITEMS.register(modEventBus);
		BCBlocks.BLOCKS.register(modEventBus);
		BCBlockEntityTypes.TILES.register(modEventBus);
		BCContainerTypes.CONTAINER_TYPES.register(modEventBus);
		BCEffects.EFFECTS.register(modEventBus);
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@Mod.EventBusSubscriber
	public static class Events {

		@SubscribeEvent
		public static void preventTipsyCure(final PotionRemoveEvent event) {
			if (event.getPotion() instanceof TipsyEffect) {
				MobEffectInstance effect = event.getPotionEffect();
				if (effect.getAmplifier() > 0) {
					event.setCanceled(true);
					event.getEntityLiving().forceAddEffect(new MobEffectInstance(BCEffects.TIPSY.get(), effect.getDuration(), effect.getAmplifier() - 1), null);
				} else if (effect.getAmplifier() < 1) {
					event.setCanceled(false);
				}
			}
		}
		
		@SubscribeEvent
		public static void extendNaturalHealing(final LivingHealEvent event) {
			LivingEntity entity = event.getEntityLiving();
			if (entity.hasEffect(BCEffects.SWEET_HEART.get())) {
				event.setAmount(event.getAmount() + 
				(0.5F + (0.5F * entity.getEffect(BCEffects.SWEET_HEART.get()).getAmplifier())));
			}
		}
	}
		

	private void registerRecipeSerializers(RegistryEvent.Register<RecipeSerializer<?>> event) {

		event.getRegistry().register(KegRecipe.SERIALIZER);
	}
}
