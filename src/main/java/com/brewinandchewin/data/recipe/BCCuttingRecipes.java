package com.brewinandchewin.data.recipe;

import java.util.function.Consumer;

import com.brewinandchewin.core.registry.BCItems;
import com.brewinandchewin.data.builder.BCCuttingBoardRecipeBuilder;

import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.crafting.Ingredient;
import vectorwing.farmersdelight.common.tag.ForgeTags;

public class BCCuttingRecipes {

	public static void register(Consumer<FinishedRecipe> consumer) {
		BCCuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(BCItems.FLAXEN_CHEESE_WHEEL.get()), Ingredient.of(ForgeTags.TOOLS_KNIVES), BCItems.FLAXEN_CHEESE_WEDGE.get(), 4)
		.build(consumer);
		BCCuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(BCItems.SCARLET_CHEESE_WHEEL.get()), Ingredient.of(ForgeTags.TOOLS_KNIVES), BCItems.SCARLET_CHEESE_WEDGE.get(), 4)
		.build(consumer);
		BCCuttingBoardRecipeBuilder.cuttingRecipe(Ingredient.of(BCItems.PIZZA.get()), Ingredient.of(ForgeTags.TOOLS_KNIVES), BCItems.PIZZA_SLICE.get(), 4)
		.build(consumer);
	}
}