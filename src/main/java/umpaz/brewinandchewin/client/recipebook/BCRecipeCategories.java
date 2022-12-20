package umpaz.brewinandchewin.client.recipebook;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.RecipeBookCategories;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.client.event.RegisterRecipeBookCategoriesEvent;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.crafting.KegRecipe;
import umpaz.brewinandchewin.common.registry.BCRecipeTypes;
import vectorwing.farmersdelight.common.registry.ModItems;

import java.util.function.Supplier;

public class BCRecipeCategories
{
    public static final Supplier<RecipeBookCategories> FERMENTING_SEARCH = Suppliers.memoize(() -> RecipeBookCategories.create("FERMENTING_SEARCH", new ItemStack(Items.COMPASS)));
    public static final Supplier<RecipeBookCategories> FERMENTING_DRINKS = Suppliers.memoize(() -> RecipeBookCategories.create("FERMENTING_DRINKS", new ItemStack(ModItems.APPLE_CIDER.get())));

    public static void init(RegisterRecipeBookCategoriesEvent event) {
        event.registerBookCategories(BrewinAndChewin.RECIPE_TYPE_FERMENTING, ImmutableList.of(FERMENTING_SEARCH.get(), FERMENTING_DRINKS.get()));
        event.registerAggregateCategory(FERMENTING_SEARCH.get(), ImmutableList.of(FERMENTING_DRINKS.get()));
        event.registerRecipeCategoryFinder(BCRecipeTypes.FERMENTING.get(), recipe ->
        {
            if (recipe instanceof KegRecipe cookingRecipe) {
                KegRecipeBookTab tab = cookingRecipe.getRecipeBookTab();
                if (tab != null) {
                    return switch (tab) {
                        case DRINKS -> FERMENTING_DRINKS.get();
                    };
                }
            }

            // If no tab is specified in recipe, this fallback organizes them instead

            return FERMENTING_DRINKS.get();
        });
    }
}
