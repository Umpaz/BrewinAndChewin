package com.brewinandchewin.data.recipe;

import java.util.function.Consumer;

import com.brewinandchewin.core.registry.BCItems;
import com.brewinandchewin.core.tag.BCTags;
import com.brewinandchewin.data.builder.BCKegRecipeBuilder;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.Items;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ForgeTags;

public class BCFermentingRecipes
{
	
	public static final int FERMENTING_TIME = 12000;		// 5 seconds
	
	public static void register(Consumer<FinishedRecipe> consumer) {
		fermentBrews(consumer);
		fermentFoods(consumer);
	}

	private static void fermentBrews(Consumer<FinishedRecipe> consumer) {
		BCKegRecipeBuilder.kegRecipe(BCItems.BEER.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), Items.WATER_BUCKET, 3)
			.addIngredient(Items.WHEAT)
			.addIngredient(Items.WHEAT)
			.addIngredient(Items.WHEAT)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.VODKA.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), Items.WATER_BUCKET, 3)
			.addIngredient(Items.POTATO)
			.addIngredient(Items.POTATO)
			.addIngredient(Items.POTATO)
			.addIngredient(Items.WHEAT)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.MEAD.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), Items.HONEY_BOTTLE, 3)
			.addIngredient(Items.WHEAT)
			.addIngredient(Items.WHEAT)
			.addIngredient(Items.SWEET_BERRIES)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.RICE_WINE.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), Items.WATER_BUCKET, 3)
			.addIngredient(ModItems.RICE.get())
			.addIngredient(ModItems.RICE.get())
			.addIngredient(ModItems.RICE.get())
			.addIngredient(ModItems.RICE.get())
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.EGG_GROG.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), Items.MILK_BUCKET, 3)
			.addIngredient(Items.EGG)
			.addIngredient(Items.EGG)
			.addIngredient(ModItems.CABBAGE_LEAF.get())
			.addIngredient(Items.SUGAR)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.STRONGROOT_ALE.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), BCItems.BEER.get(), 3)
			.addIngredient(Items.BEETROOT)
			.addIngredient(Items.POTATO)
			.addIngredient(Items.BROWN_MUSHROOM)
			.addIngredient(Items.CARROT)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.SACCHARINE_RUM.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), BCItems.MEAD.get(), 4)
			.addIngredient(Items.SWEET_BERRIES)
			.addIngredient(Items.SUGAR_CANE)
			.addIngredient(Items.SUGAR_CANE)
			.addIngredient(Items.MELON_SLICE)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.PALE_JANE.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), BCItems.RICE_WINE.get(), 4)
			.addIngredient(Items.HONEY_BOTTLE)
			.addIngredient(ModItems.TREE_BARK.get())
			.addIngredient(Items.LILY_OF_THE_VALLEY)
			.addIngredient(Items.SUGAR)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.DREAD_NOG.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), BCItems.EGG_GROG.get(), 1)
			.addIngredient(Items.EGG)
			.addIngredient(Items.EGG)
			.addIngredient(Items.TURTLE_EGG)
			.addIngredient(Items.FERMENTED_SPIDER_EYE)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.SALTY_FOLLY.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), BCItems.VODKA.get(), 2)
			.addIngredient(Items.SEA_PICKLE)
			.addIngredient(Items.DRIED_KELP)
			.addIngredient(Items.DRIED_KELP)
			.addIngredient(Items.SEAGRASS)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.STEEL_TOE_STOUT.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), BCItems.STRONGROOT_ALE.get(), 1)
			.addIngredient(Items.CRIMSON_FUNGUS)
			.addIngredient(Items.IRON_INGOT)
			.addIngredient(Items.NETHER_WART)
			.addIngredient(Items.WHEAT)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.GLITTERING_GRENADINE.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), Items.WATER_BUCKET, 2)
			.addIngredient(Items.GLOW_BERRIES)
			.addIngredient(Items.GLOW_INK_SAC)
			.addIngredient(Items.GLOWSTONE_DUST)
			.addIngredient(Items.GLOW_BERRIES)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.BLOODY_MARY.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), BCItems.VODKA.get(), 4)
			.addIngredient(ModItems.TOMATO.get())
			.addIngredient(ModItems.TOMATO.get())
			.addIngredient(ModItems.CABBAGE_LEAF.get())
			.addIngredient(Items.SWEET_BERRIES)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.RED_RUM.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), BCItems.BLOODY_MARY.get(), 5)
			.addIngredient(Items.CRIMSON_FUNGUS)
			.addIngredient(Items.NETHER_WART)
			.addIngredient(Items.FERMENTED_SPIDER_EYE)
			.addIngredient(Items.SHROOMLIGHT)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.WITHERING_DROSS.get(), 1, FERMENTING_TIME, 0.6F, BCItems.TANKARD.get(), BCItems.SALTY_FOLLY.get(), 5)
			.addIngredient(Items.WITHER_ROSE)
			.addIngredient(Items.INK_SAC)
			.addIngredient(Items.NETHER_WART)
			.addIngredient(Items.BONE)
			.build(consumer);
	}

	private static void fermentFoods(Consumer<FinishedRecipe> consumer) {
		BCKegRecipeBuilder.kegRecipe(BCItems.KIMCHI.get(), 2, FERMENTING_TIME, 0.6F, 3)
			.addIngredient(ModItems.CABBAGE_LEAF.get())
			.addIngredient(ForgeTags.VEGETABLES)
			.addIngredient(Items.KELP)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.JERKY.get(), 3, FERMENTING_TIME, 0.6F, 3)
			.addIngredient(BCTags.RAW_MEATS)
			.addIngredient(BCTags.RAW_MEATS)
			.addIngredient(BCTags.RAW_MEATS)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.PICKLED_PICKLES.get(), 2, FERMENTING_TIME, 0.6F, Items.HONEY_BOTTLE, 2)
			.addIngredient(Items.SEA_PICKLE)
			.addIngredient(Items.SEA_PICKLE)
			.addIngredient(Items.GLOWSTONE_DUST)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.KIPPERS.get(), 3, FERMENTING_TIME, 0.6F, 3)
			.addIngredient(ForgeTags.RAW_FISHES)
			.addIngredient(ForgeTags.RAW_FISHES)
			.addIngredient(Items.KELP)
			.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.COCOA_FUDGE.get(), 1, FERMENTING_TIME, 0.6F, Items.MILK_BUCKET, 5)
			.addIngredient(Items.SUGAR)
			.addIngredient(Items.COCOA_BEANS)
			.addIngredient(Items.COCOA_BEANS)
			.build(consumer);
		
		BCKegRecipeBuilder.kegRecipe(BCItems.UNRIPE_FLAXEN_CHEESE_WHEEL.get(), 1, FERMENTING_TIME, 0.6F, Items.HONEYCOMB, Items.MILK_BUCKET, 4)
		.addIngredient(Items.BROWN_MUSHROOM)
		.addIngredient(Items.BROWN_MUSHROOM)
		.addIngredient(Items.SUGAR)
		.build(consumer);
		BCKegRecipeBuilder.kegRecipe(BCItems.UNRIPE_SCARLET_CHEESE_WHEEL.get(), 1, FERMENTING_TIME, 0.6F, Items.HONEYCOMB, Items.MILK_BUCKET, 4)
		.addIngredient(Items.CRIMSON_FUNGUS)
		.addIngredient(Items.CRIMSON_FUNGUS)
		.addIngredient(Items.SUGAR)
		.build(consumer);
		
	}
}