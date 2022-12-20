package umpaz.brewinandchewin.common.registry;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.crafting.KegRecipe;

public class BCRecipeSerializers
{
	public static final DeferredRegister<RecipeSerializer<?>> RECIPE_SERIALIZERS = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, BrewinAndChewin.MODID);

	public static final RegistryObject<RecipeSerializer<?>> FERMENTING = RECIPE_SERIALIZERS.register("fermenting", KegRecipe.Serializer::new);

}
