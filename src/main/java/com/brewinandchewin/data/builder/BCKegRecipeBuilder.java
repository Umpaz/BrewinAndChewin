package com.brewinandchewin.data.builder;

import java.util.List;
import java.util.function.Consumer;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import com.brewinandchewin.common.crafting.KegRecipe;
import com.brewinandchewin.core.BrewinAndChewin;
import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import mezz.jei.api.MethodsReturnNonnullByDefault;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.ForgeRegistries;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BCKegRecipeBuilder
{
	private final List<Ingredient> ingredients = Lists.newArrayList();
	private final Item result;
	private final int count;
	private final int cookingTime;
	private final float experience;
	private final Item container;
	private final Item liquid;
	private final int temperature;

	private BCKegRecipeBuilder(ItemLike resultIn, int count, int cookingTime, float experience, @Nullable ItemLike container, @Nullable ItemLike liquid, int temperature) {
		this.result = resultIn.asItem();
		this.count = count;
		this.liquid = liquid != null ? liquid.asItem() : null;
		this.cookingTime = cookingTime;
		this.experience = experience;
		this.container = container != null ? container.asItem() : null;
		this.temperature = temperature;
	}

	public static BCKegRecipeBuilder kegRecipe(ItemLike mainResult, int count, int cookingTime, float experience, ItemLike liquid, int temperature) {
		return new BCKegRecipeBuilder(mainResult, count, cookingTime, experience, null, liquid, temperature);
	}

	public static BCKegRecipeBuilder kegRecipe(ItemLike mainResult, int count, int cookingTime, float experience, int temperature) {
		return new BCKegRecipeBuilder(mainResult, count, cookingTime, experience, null, null, temperature);
	}

	public static BCKegRecipeBuilder kegRecipe(ItemLike mainResult, int count, int cookingTime, float experience, ItemLike container, ItemLike liquid, int temperature) {
		return new BCKegRecipeBuilder(mainResult, count, cookingTime, experience, container, liquid, temperature);
	}

	public BCKegRecipeBuilder addIngredient(TagKey<Item> tagIn) {
		return this.addIngredient(Ingredient.of(tagIn));
	}

	public BCKegRecipeBuilder addIngredient(ItemLike itemIn) {
		return this.addIngredient(itemIn, 1);
	}

	public BCKegRecipeBuilder addIngredient(ItemLike itemIn, int quantity) {
		for (int i = 0; i < quantity; ++i) {
			this.addIngredient(Ingredient.of(itemIn));
		}
		return this;
	}

	public BCKegRecipeBuilder addIngredient(Ingredient ingredientIn) {
		return this.addIngredient(ingredientIn, 1);
	}

	public BCKegRecipeBuilder addIngredient(Ingredient ingredientIn, int quantity) {
		for (int i = 0; i < quantity; ++i) {
			this.ingredients.add(ingredientIn);
		}
		return this;
	}

	public void build(Consumer<FinishedRecipe> consumerIn) {
		ResourceLocation location = ForgeRegistries.ITEMS.getKey(this.result);
		this.build(consumerIn, BrewinAndChewin.MODID + ":fermenting/" + location.getPath());
	}

	public void build(Consumer<FinishedRecipe> consumerIn, String save) {
		ResourceLocation resourcelocation = ForgeRegistries.ITEMS.getKey(this.result);
		if ((new ResourceLocation(save)).equals(resourcelocation)) {
			throw new IllegalStateException("Fermenting Recipe " + save + " should remove its 'save' argument");
		} else {
			this.build(consumerIn, new ResourceLocation(save));
		}
	}

	public void build(Consumer<FinishedRecipe> consumerIn, ResourceLocation id) {
		consumerIn.accept(new BCKegRecipeBuilder.Result(id, this.result, this.count, this.ingredients, this.cookingTime, this.experience, this.container, this.liquid, this.temperature));
	}

	public static class Result implements FinishedRecipe
	{
		private final ResourceLocation id;
		private final List<Ingredient> ingredients;
		private final Item result;
		private final int count;
		private final int cookingTime;
		private final float experience;
		private final Item container;
		private final Item liquid;
		private final int temperature;
		
		public Result(ResourceLocation idIn, Item resultIn, int countIn, List<Ingredient> ingredientsIn, int cookingTimeIn, float experienceIn, @Nullable Item containerIn, @Nullable Item liquidIn, int temperatureIn) {
			this.id = idIn;
			this.ingredients = ingredientsIn;
			this.result = resultIn;
			this.count = countIn;
			this.cookingTime = cookingTimeIn;
			this.experience = experienceIn;
			this.container = containerIn;
			this.liquid = liquidIn;
			this.temperature = temperatureIn;
		}

		@Override
		public void serializeRecipeData(JsonObject json) {
			JsonArray arrayIngredients = new JsonArray();

			for (Ingredient ingredient : this.ingredients) {
				arrayIngredients.add(ingredient.toJson());
			}
			json.add("ingredients", arrayIngredients);

			JsonObject objectResult = new JsonObject();
			objectResult.addProperty("item", ForgeRegistries.ITEMS.getKey(this.result).toString());
			if (this.count > 1) {
				objectResult.addProperty("count", this.count);
			}
			json.add("result", objectResult);

			if (this.container != null) {
				JsonObject objectContainer = new JsonObject();
				objectContainer.addProperty("item", ForgeRegistries.ITEMS.getKey(this.container).toString());
				json.add("container", objectContainer);
			}
			if (this.liquid != null) {
				JsonObject objectLiquid = new JsonObject();
				objectLiquid.addProperty("item", ForgeRegistries.ITEMS.getKey(this.liquid).toString());
				json.add("liquid", objectLiquid);
			}
			if (this.experience > 0) {
				json.addProperty("experience", this.experience);
			}
			json.addProperty("cookingtime", this.cookingTime);
			json.addProperty("temperature", this.temperature);
		}

		@Override
		public ResourceLocation getId() {
			return this.id;
		}

		@Override
		public RecipeSerializer<?> getType() {
			return KegRecipe.SERIALIZER;
		}

		@Nullable
		@Override
		public JsonObject serializeAdvancement() {
			return null;
		}

		@Nullable
		@Override
		public ResourceLocation getAdvancementId() {
			return null;
		}
	}
}