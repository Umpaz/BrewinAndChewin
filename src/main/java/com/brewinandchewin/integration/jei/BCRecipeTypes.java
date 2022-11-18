package com.brewinandchewin.integration.jei;

import com.brewinandchewin.common.crafting.KegRecipe;
import com.brewinandchewin.core.BrewinAndChewin;

import mezz.jei.api.recipe.RecipeType;

public final class BCRecipeTypes
{
	public static final RecipeType<KegRecipe> FERMENTING = RecipeType.create(BrewinAndChewin.MODID, "fermenting", KegRecipe.class);
}
