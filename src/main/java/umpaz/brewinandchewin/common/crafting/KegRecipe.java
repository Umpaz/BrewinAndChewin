package umpaz.brewinandchewin.common.crafting;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.minecraft.core.NonNullList;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.client.recipebook.KegRecipeBookTab;
import umpaz.brewinandchewin.common.registry.BCItems;
import umpaz.brewinandchewin.common.registry.BCRecipeSerializers;
import umpaz.brewinandchewin.common.registry.BCRecipeTypes;

import javax.annotation.Nullable;
import java.util.EnumSet;

@SuppressWarnings("ClassCanBeRecord")
public class KegRecipe implements Recipe<RecipeWrapper>
{
    public static final int INPUT_SLOTS = 4;

    private final ResourceLocation id;
    private final String group;
    private final KegRecipeBookTab tab;
    private final NonNullList<Ingredient> inputItems;
    private final ItemStack output;
    private final ItemStack container;
    private final ItemStack fluidItem;
    private final float experience;
    private final int fermentTime;
    private final String temperature;

    public KegRecipe(ResourceLocation id, String group, @Nullable KegRecipeBookTab tab, NonNullList<Ingredient> inputItems, ItemStack fluidItem, ItemStack output, ItemStack container, float experience, int fermentTime, String temperature) {
        this.id = id;
        this.group = group;
        this.tab = tab;
        this.inputItems = inputItems;
        this.output = output;

        if (!container.isEmpty()) {
            this.container = container;
        } else if (!output.getCraftingRemainingItem().isEmpty()) {
            this.container = output.getCraftingRemainingItem();
        } else {
            this.container = ItemStack.EMPTY;
        }

        if (!fluidItem.isEmpty()) {
            this.fluidItem = fluidItem;
        } else {
            this.fluidItem = ItemStack.EMPTY;
        }

        this.experience = experience;
        this.fermentTime = fermentTime;
        this.temperature = temperature;
    }

    @Override
    public ResourceLocation getId() {
        return this.id;
    }

    @Override
    public String getGroup() {
        return this.group;
    }

    @Nullable
    public KegRecipeBookTab getRecipeBookTab() {
        return this.tab;
    }

    @Override
    public NonNullList<Ingredient> getIngredients() {
        return this.inputItems;
    }

    public ItemStack getFluidItem() {
        return this.fluidItem;
    }

    @Override
    public ItemStack getResultItem() {
        return this.output;
    }

    public ItemStack getOutputContainer() {
        return this.container;
    }

    @Override
    public ItemStack assemble(RecipeWrapper inv) {
        return this.output.copy();
    }

    public float getExperience() {
        return this.experience;
    }

    public int getFermentTime() {
        return this.fermentTime;
    }

    public String getTemperature() {
        return this.temperature;
    }


    @Override
    public boolean matches(RecipeWrapper inv, Level level) {
        NonNullList<Ingredient> ingredientsList = NonNullList.create();
        ingredientsList.addAll(this.inputItems);
        ingredientsList.remove(ingredientsList.size() - 1);
        java.util.List<ItemStack> inputs = new java.util.ArrayList<>();
        int i = 0;

        for (int j = 0; j < INPUT_SLOTS; ++j) {
            ItemStack itemstack = inv.getItem(j);
            if (!itemstack.isEmpty()) {
                ++i;
                inputs.add(itemstack);
            }
        }
        if (this.fluidItem != null) {
            return i == ingredientsList.size() && net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, ingredientsList) != null && inv.getItem(4).sameItem(fluidItem);
        }
        return i == ingredientsList.size() && net.minecraftforge.common.util.RecipeMatcher.findMatches(inputs, ingredientsList) != null;
    }

    @Override
    public boolean canCraftInDimensions(int width, int height) {
        return width * height >= this.inputItems.size();
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return BCRecipeSerializers.FERMENTING.get();
    }

    @Override
    public RecipeType<?> getType() {
        return BCRecipeTypes.FERMENTING.get();
    }

    @Override
    public ItemStack getToastSymbol() {
        return new ItemStack(BCItems.KEG.get());
    }

    public static class Serializer implements RecipeSerializer<KegRecipe>
    {
        public Serializer() {
        }

        @Override
        public KegRecipe fromJson(ResourceLocation recipeId, JsonObject json) {
            final String groupIn = GsonHelper.getAsString(json, "group", "");
            final NonNullList<Ingredient> inputItemsIn = readIngredients(GsonHelper.getAsJsonArray(json, "ingredients"));
            if (inputItemsIn.isEmpty()) {
                throw new JsonParseException("No ingredients for fermenting recipe");
            } else if (inputItemsIn.size() > KegRecipe.INPUT_SLOTS) {
                throw new JsonParseException("Too many ingredients for fermenting recipe! The max is " + KegRecipe.INPUT_SLOTS);
            } else {
                final String tabKeyIn = GsonHelper.getAsString(json, "recipe_book_tab", null);
                final KegRecipeBookTab tabIn = KegRecipeBookTab.findByName(tabKeyIn);
                if (tabKeyIn != null && tabIn == null) {
                    BrewinAndChewin.LOGGER.warn("Optional field 'recipe_book_tab' does not match any valid tab. If defined, must be one of the following: " + EnumSet.allOf(KegRecipeBookTab.class));
                }
                final ItemStack outputIn = CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "result"), true);
                ItemStack fluidItem = GsonHelper.isValidNode(json, "fluiditem") ? CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "fluiditem"), true) : ItemStack.EMPTY;
                ItemStack container = GsonHelper.isValidNode(json, "container") ? CraftingHelper.getItemStack(GsonHelper.getAsJsonObject(json, "container"), true) : ItemStack.EMPTY;
                final float experienceIn = GsonHelper.getAsFloat(json, "experience", 0.0F);
                final int fermentTimeIn = GsonHelper.getAsInt(json, "fermentingtime", 200);
                final String temperatureIn = GsonHelper.getAsString(json, "temperature", "normal");
                inputItemsIn.add(Ingredient.of(fluidItem));
                return new KegRecipe(recipeId, groupIn, tabIn, inputItemsIn, fluidItem, outputIn, container, experienceIn, fermentTimeIn, temperatureIn);
            }
        }

        private static NonNullList<Ingredient> readIngredients(JsonArray ingredientArray) {
            NonNullList<Ingredient> nonnulllist = NonNullList.create();

            for (int i = 0; i < ingredientArray.size(); ++i) {
                Ingredient ingredient = Ingredient.fromJson(ingredientArray.get(i));
                if (!ingredient.isEmpty()) {
                    nonnulllist.add(ingredient);
                }
            }

            return nonnulllist;
        }

        @Nullable
        @Override
        public KegRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer) {
            String groupIn = buffer.readUtf();
            KegRecipeBookTab tabIn = KegRecipeBookTab.findByName(buffer.readUtf());
            int i = buffer.readVarInt();
            NonNullList<Ingredient> inputItemsIn = NonNullList.withSize(i, Ingredient.EMPTY);

            for (int j = 0; j < inputItemsIn.size(); ++j) {
                inputItemsIn.set(j, Ingredient.fromNetwork(buffer));
            }

            ItemStack fluidItem = buffer.readItem();
            ItemStack outputIn = buffer.readItem();
            ItemStack container = buffer.readItem();
            float experienceIn = buffer.readFloat();
            int fermentTimeIn = buffer.readVarInt();
            String temperatureIn = buffer.readUtf();
            return new KegRecipe(recipeId, groupIn, tabIn, inputItemsIn, fluidItem, outputIn, container, experienceIn, fermentTimeIn, temperatureIn);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, KegRecipe recipe) {
            buffer.writeUtf(recipe.group);
            buffer.writeUtf(recipe.tab != null ? recipe.tab.toString() : "");
            buffer.writeVarInt(recipe.inputItems.size());

            for (Ingredient ingredient : recipe.inputItems) {
                ingredient.toNetwork(buffer);
            }

            buffer.writeItem(recipe.fluidItem);
            buffer.writeItem(recipe.output);
            buffer.writeItem(recipe.container);
            buffer.writeFloat(recipe.experience);
            buffer.writeVarInt(recipe.fermentTime);
            buffer.writeUtf(recipe.temperature);
        }
    }
}