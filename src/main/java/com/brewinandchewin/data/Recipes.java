package com.brewinandchewin.data;

import java.util.function.Consumer;

import javax.annotation.ParametersAreNonnullByDefault;

import com.brewinandchewin.core.BrewinAndChewin;
import com.brewinandchewin.core.registry.BCItems;
import com.brewinandchewin.data.builder.BCCuttingBoardRecipeBuilder;
import com.brewinandchewin.data.recipe.BCCuttingRecipes;
import com.brewinandchewin.data.recipe.BCFermentingRecipes;

import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.ShapelessRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;
import vectorwing.farmersdelight.common.registry.ModItems;
import vectorwing.farmersdelight.common.tag.ForgeTags;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class Recipes extends RecipeProvider
{
	public Recipes(DataGenerator generator) {
		super(generator);
	}

	@Override
	protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer) {
		BCFermentingRecipes.register(consumer);
		BCCuttingRecipes.register(consumer);
		recipesCrafted(consumer);

	}

	private void recipesCrafted(Consumer<FinishedRecipe> consumer) {
		ShapelessRecipeBuilder.shapeless(BCItems.HAM_AND_CHEESE_SANDWICH.get(), 2)
		.requires(Items.BREAD)
		.requires(ModItems.SMOKED_HAM.get())
		.requires(BCItems.FLAXEN_CHEESE_WEDGE.get())
		.requires(Items.BREAD)
		.unlockedBy("has_cheese", InventoryChangeTrigger.TriggerInstance.hasItems(BCItems.FLAXEN_CHEESE_WEDGE.get()))
		.save(consumer);

		ShapedRecipeBuilder.shaped(BCItems.PIZZA.get())
		.pattern("fff")
		.pattern("mtp")
		.pattern("www")
		.define('w', Items.WHEAT)
		.define('m', Items.BROWN_MUSHROOM)
		.define('t', ModItems.TOMATO.get())
		.define('p', ModItems.BEEF_PATTY.get())
		.define('f', BCItems.FLAXEN_CHEESE_WEDGE.get())
		.unlockedBy("has_cheese", InventoryChangeTrigger.TriggerInstance.hasItems(BCItems.FLAXEN_CHEESE_WEDGE.get()))
		.save(consumer);

		ShapedRecipeBuilder.shaped(BCItems.ITEM_MAT.get(), 16)
		.pattern("cc")
		.define('c', ModItems.CANVAS.get())
		.unlockedBy("has_canvas", InventoryChangeTrigger.TriggerInstance.hasItems(ModItems.CANVAS.get()))
		.save(consumer);
		
		ShapedRecipeBuilder.shaped(BCItems.KEG.get())
		.pattern("ipi")
		.pattern("ihi")
		.pattern("ppp")
		.define('i', Items.IRON_INGOT)
		.define('h', Items.HONEYCOMB)
		.define('p', ItemTags.PLANKS)
		.unlockedBy("has_honeycomb", InventoryChangeTrigger.TriggerInstance.hasItems(Items.HONEYCOMB))
		.save(consumer);
		
		ShapedRecipeBuilder.shaped(BCItems.TANKARD.get(), 4)
		.pattern("p p")
		.pattern("i i")
		.pattern("ppp")
		.define('i', Items.IRON_NUGGET)
		.define('p', ItemTags.PLANKS)
		.unlockedBy("has_nugget", InventoryChangeTrigger.TriggerInstance.hasItems(Items.IRON_NUGGET))
		.save(consumer);
	}
	
}