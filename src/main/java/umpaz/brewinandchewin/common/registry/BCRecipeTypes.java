package umpaz.brewinandchewin.common.registry;

import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.crafting.KegRecipe;

public class BCRecipeTypes
{
	public static final DeferredRegister<RecipeType<?>> RECIPE_TYPES = DeferredRegister.create(ForgeRegistries.RECIPE_TYPES, BrewinAndChewin.MODID);

	public static final RegistryObject<RecipeType<KegRecipe>> FERMENTING = RECIPE_TYPES.register("fermenting", () -> registerRecipeType("fermenting"));

	public static <T extends Recipe<?>> RecipeType<T> registerRecipeType(final String identifier) {
		return new RecipeType<>()
		{
			public String toString() {
				return BrewinAndChewin.MODID + ":" + identifier;
			}
		};
	}
}
