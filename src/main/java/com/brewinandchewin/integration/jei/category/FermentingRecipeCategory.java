package com.brewinandchewin.integration.jei.category;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.ParametersAreNonnullByDefault;

import com.brewinandchewin.common.crafting.KegRecipe;
import com.brewinandchewin.common.crafting.KegRecipe.Range;
import com.brewinandchewin.core.BrewinAndChewin;
import com.brewinandchewin.core.registry.BCItems;
import com.brewinandchewin.core.utility.BCTextUtils;
import com.brewinandchewin.integration.jei.BCRecipeTypes;
import com.mojang.blaze3d.vertex.PoseStack;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class FermentingRecipeCategory implements IRecipeCategory<KegRecipe>
{
	public static final ResourceLocation UID = new ResourceLocation(BrewinAndChewin.MODID, "fermenting");
	protected final IDrawableAnimated arrow;
	private final Component title;
	private final IDrawable background;
	private final IDrawable icon;
	private final IDrawable frigid;
	private final IDrawable cold;
	private final IDrawable warm;
	private final IDrawable hot;
	
	public FermentingRecipeCategory(IGuiHelper helper) {
		title = BCTextUtils.getTranslation("jei.fermenting");
		ResourceLocation backgroundImage = new ResourceLocation(BrewinAndChewin.MODID, "textures/gui/jei/keg.png");
		background = helper.createDrawable(backgroundImage, 29, 16, 117, 57);
		icon = helper.createDrawableIngredient(VanillaTypes.ITEM, new ItemStack(BCItems.KEG.get()));
		arrow = helper.drawableBuilder(backgroundImage, 176, 28, 33, 9)
				.buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
		frigid = helper.createDrawable(backgroundImage, 176, 0, 6, 3);
		cold = helper.createDrawable(backgroundImage, 182, 0, 7, 3);
		warm = helper.createDrawable(backgroundImage, 195, 0, 7, 3);
		hot = helper.createDrawable(backgroundImage, 202, 0, 7, 3);
	}

	@Override
	public ResourceLocation getUid() {
		return this.getRecipeType().getUid();
	}

	@Override
	public Class<? extends KegRecipe> getRecipeClass() {
		return this.getRecipeType().getRecipeClass();
	}

	@Override
	public RecipeType<KegRecipe> getRecipeType() {
		return BCRecipeTypes.FERMENTING;
	}

	@Override
	public Component getTitle() {
		return this.title;
	}

	@Override
	public IDrawable getBackground() {
		return this.background;
	}

	@Override
	public IDrawable getIcon() {
		return this.icon;
	}

	@Override
	public void setIngredients(KegRecipe kegRecipe, IIngredients ingredients) {
		List<Ingredient> inputAndContainer = new ArrayList<>(kegRecipe.getIngredients());
		inputAndContainer.add(Ingredient.of(kegRecipe.getOutputContainer()));
		inputAndContainer.add(kegRecipe.getLiquid());
		
		ingredients.setInputIngredients(inputAndContainer);
		ingredients.setOutput(VanillaTypes.ITEM, kegRecipe.getResultItem());
	}

	@Override
	public void setRecipe(IRecipeLayout recipeLayout, KegRecipe recipe, IIngredients ingredients) {
		final int LIQUID_INPUT = 4;
		final int MEAL_DISPLAY = 5;
		final int CONTAINER_INPUT = 6;
		final int OUTPUT = 7;
		IGuiItemStackGroup itemStacks = recipeLayout.getItemStacks();
		NonNullList<Ingredient> recipeIngredients = recipe.getIngredients();
		
		int borderSlotSize = 18;
		for (int row = 0; row < 2; ++row) {
			for (int column = 0; column < 2; ++column) {
				int inputIndex = row * 2 + column;
				if (inputIndex < recipeIngredients.size()) {
					itemStacks.init(inputIndex, true, column * borderSlotSize + 3, row * borderSlotSize + 11);
					itemStacks.set(inputIndex, Arrays.asList(recipeIngredients.get(inputIndex).getItems()));
				}
			}
		}

		itemStacks.init(MEAL_DISPLAY, false, 92, 6);
		itemStacks.set(MEAL_DISPLAY, recipe.getResultItem());

		if (!recipe.getOutputContainer().isEmpty()) {
			itemStacks.init(CONTAINER_INPUT, false, 60, 38);
			itemStacks.set(CONTAINER_INPUT, recipe.getOutputContainer());
		}
		if (!recipe.getLiquid().isEmpty()) {
			itemStacks.init(LIQUID_INPUT, true, 55, 1);
			itemStacks.set(LIQUID_INPUT, Arrays.asList(recipe.getLiquid().getItems())); 
		}
		

		itemStacks.init(OUTPUT, false, 92, 38);
		itemStacks.set(OUTPUT, recipe.getResultItem());
	}

	@Override
	public void draw(KegRecipe recipe, IRecipeSlotsView recipeSlotsView, PoseStack matrixStack, double mouseX, double mouseY) {
		arrow.draw(matrixStack, 48, 28);
		if (recipe.getTemperatureJei() == 1) {
			frigid.draw(matrixStack, 48, 23);
			cold.draw(matrixStack, 54, 23);
		}
		if (recipe.getTemperatureJei() == 2) {
			cold.draw(matrixStack, 54, 23);
		}
		if (recipe.getTemperatureJei() == 4) {
			warm.draw(matrixStack, 67, 23);
		}
		if (recipe.getTemperatureJei() == 5) {
			hot.draw(matrixStack, 74, 23);
			warm.draw(matrixStack, 67, 23);
		}
	}
}