package umpaz.brewinandchewin.common.registry;

import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.item.FluidItem;

@SuppressWarnings("unused")
public class BCItems
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BrewinAndChewin.MODID);

	// Helper methods
	public static Item.Properties basicItem() {
		return new Item.Properties().tab(BrewinAndChewin.CREATIVE_TAB);
	}

	public static Item.Properties foodItem(FoodProperties food) {
		return new Item.Properties().food(food).tab(BrewinAndChewin.CREATIVE_TAB);
	}

	public static Item.Properties bowlFoodItem(FoodProperties food) {
		return new Item.Properties().food(food).craftRemainder(Items.BOWL).stacksTo(16).tab(BrewinAndChewin.CREATIVE_TAB);
	}

	public static Item.Properties drinkItem() {
		return new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16).tab(BrewinAndChewin.CREATIVE_TAB);
	}

	public static Item.Properties drinkItemNoItem() {
		return new Item.Properties().craftRemainder(Items.GLASS_BOTTLE).stacksTo(16);
	}

	public static final RegistryObject<FluidItem> FLUID_ITEM = ITEMS.register("fluid_item", () -> new FluidItem(basicItem()));

	// Blocks
	public static final RegistryObject<Item> KEG = ITEMS.register("keg",
			() -> new BlockItem(BCBlocks.KEG.get(), basicItem().stacksTo(1)));

}
