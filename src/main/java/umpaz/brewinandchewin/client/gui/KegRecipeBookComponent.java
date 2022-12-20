package umpaz.brewinandchewin.client.gui;

import net.minecraft.client.gui.screens.recipebook.RecipeBookComponent;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.Recipe;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.crafting.KegRecipe;
import umpaz.brewinandchewin.common.utility.BCTextUtils;

import javax.annotation.Nonnull;
import java.util.List;

public class KegRecipeBookComponent extends RecipeBookComponent
{
    protected static final ResourceLocation RECIPE_BOOK_BUTTONS = new ResourceLocation(BrewinAndChewin.MODID, "textures/gui/recipe_book_buttons.png");

    @Override
    protected void initFilterButtonTextures() {
        this.filterButton.initTextureValues(0, 0, 28, 18, RECIPE_BOOK_BUTTONS);
    }

    public void hide() {
        this.setVisible(false);
    }

    @Override
    @Nonnull
    protected Component getRecipeFilterName() {
        return BCTextUtils.getTranslation("container.recipe_book.fermentable");
    }

    @Override
    public void setupGhostRecipe(Recipe<?> recipe, List<Slot> slots) {
        NonNullList<Ingredient> ingredientsList = NonNullList.create();
        ingredientsList.addAll(recipe.getIngredients());
        ingredientsList.remove(ingredientsList.size() - 1);
        ItemStack resultStack = recipe.getResultItem();
        this.ghostRecipe.setRecipe(recipe);
        if (slots.get(5).getItem().isEmpty()) {
            this.ghostRecipe.addIngredient(Ingredient.of(resultStack), (slots.get(5)).x, (slots.get(5)).y);
        }

        if (recipe instanceof KegRecipe fermentingRecipe) {
            ItemStack containerStack = fermentingRecipe.getOutputContainer();
            ItemStack fluidItemStack = fermentingRecipe.getFluidItem();
            if (!fluidItemStack.isEmpty()) {
                this.ghostRecipe.addIngredient(Ingredient.of(fluidItemStack), (slots.get(4)).x, (slots.get(4)).y);

            }
            if (!containerStack.isEmpty()) {
                this.ghostRecipe.addIngredient(Ingredient.of(containerStack), (slots.get(6)).x, (slots.get(6)).y);
            }
        }

        this.placeRecipe(this.menu.getGridWidth(), this.menu.getGridHeight(), this.menu.getResultSlotIndex(), recipe, ingredientsList.iterator(), 0);
    }

}