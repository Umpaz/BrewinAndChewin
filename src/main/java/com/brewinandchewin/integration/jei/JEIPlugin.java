package com.brewinandchewin.integration.jei;

import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.ParametersAreNonnullByDefault;

import com.brewinandchewin.client.gui.KegScreen;
import com.brewinandchewin.common.block.entity.container.KegContainer;
import com.brewinandchewin.common.crafting.KegRecipe;
import com.brewinandchewin.core.BrewinAndChewin;
import com.brewinandchewin.core.registry.BCItems;
import com.brewinandchewin.integration.jei.category.FermentingRecipeCategory;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IGuiHandlerRegistration;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.IRecipeTransferRegistration;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;

@JeiPlugin
@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
@SuppressWarnings("unused")
public class JEIPlugin implements IModPlugin
{
	private static final ResourceLocation ID = new ResourceLocation(BrewinAndChewin.MODID, "jei_plugin");
	private static final Minecraft MC = Minecraft.getInstance();

	private static List<Recipe<?>> findRecipesByType(RecipeType<?> type) {
		return MC.level
				.getRecipeManager()
				.getRecipes()
				.stream()
				.filter(r -> r.getType() == type)
				.collect(Collectors.toList());
	}

	@Override
	public void registerCategories(IRecipeCategoryRegistration registry) {
		registry.addRecipeCategories(new FermentingRecipeCategory(registry.getJeiHelpers().getGuiHelper()));
	}

	@Override
	public void registerRecipes(IRecipeRegistration registration) {
		registration.addRecipes(findRecipesByType(KegRecipe.TYPE), FermentingRecipeCategory.UID);
	}

	@Override
	public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
		registration.addRecipeCatalyst(new ItemStack(BCItems.KEG.get()), BCRecipeTypes.FERMENTING);
	}

	@Override
	public void registerGuiHandlers(IGuiHandlerRegistration registration) {
		registration.addRecipeClickArea(KegScreen.class, 78, 45, 31, 7, BCRecipeTypes.FERMENTING);
	}

	@Override
	public void registerRecipeTransferHandlers(IRecipeTransferRegistration registration) {
		//registration.addRecipeTransferHandler(KegContainer.class, BCRecipeTypes.FERMENTING, 0, 5, 7, 36);
	}

	@Override
	public ResourceLocation getPluginUid() {
		return ID;
	}
}
