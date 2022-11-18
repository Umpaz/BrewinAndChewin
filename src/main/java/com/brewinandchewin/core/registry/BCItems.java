package com.brewinandchewin.core.registry;

import com.brewinandchewin.common.item.BoozeItem;
import com.brewinandchewin.common.item.DreadNogItem;
import com.brewinandchewin.core.BrewinAndChewin;
import com.brewinandchewin.core.utility.BCFoods;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BCItems
{
	public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, BrewinAndChewin.MODID);

	//Items
	
	public static final RegistryObject<Item> KEG = ITEMS.register("keg",
			() -> new BlockItem(BCBlocks.KEG.get(), new Item.Properties().stacksTo(1).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> ITEM_MAT = ITEMS.register("item_mat",
			() -> new BlockItem(BCBlocks.ITEM_MAT.get(), new Item.Properties().tab(BrewinAndChewin.CREATIVE_TAB)));

	public static final RegistryObject<Item> TANKARD = ITEMS.register("tankard",
			() -> new Item(new Item.Properties().tab(BrewinAndChewin.CREATIVE_TAB)));
	
	public static final RegistryObject<Item> BEER = ITEMS.register("beer",
			() -> new BoozeItem(1, 8, new Item.Properties().stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> VODKA = ITEMS.register("vodka",
			() -> new BoozeItem(1, 12, new Item.Properties().stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> MEAD = ITEMS.register("mead",
			() -> new BoozeItem(1, 8, new Item.Properties().food(BCFoods.MEAD).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> RICE_WINE = ITEMS.register("rice_wine",
			() -> new BoozeItem(1, 5, new Item.Properties().food(BCFoods.RICE_WINE).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> EGG_GROG = ITEMS.register("egg_grog",
			() -> new BoozeItem(1, 0, new Item.Properties().food(BCFoods.EGG_GROG).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> STRONGROOT_ALE = ITEMS.register("strongroot_ale",
			() -> new BoozeItem(2, 12, new Item.Properties().food(BCFoods.STRONGROOT_ALE).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> SACCHARINE_RUM = ITEMS.register("saccharine_rum",
			() -> new BoozeItem(2, 8, new Item.Properties().food(BCFoods.SACCHARINE_RUM).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> PALE_JANE = ITEMS.register("pale_jane",
			() -> new BoozeItem(1, 5, new Item.Properties().food(BCFoods.PALE_JANE).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	
	public static final RegistryObject<Item> DREAD_NOG = ITEMS.register("dread_nog",
			() -> new DreadNogItem(3, 5, new Item.Properties().stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	
	public static final RegistryObject<Item> SALTY_FOLLY = ITEMS.register("salty_folly",
			() -> new BoozeItem(2, 10, new Item.Properties().food(BCFoods.SALTY_FOLLY).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> STEEL_TOE_STOUT = ITEMS.register("steel_toe_stout",
			() -> new BoozeItem(3, 10, new Item.Properties().food(BCFoods.STEEL_TOE_STOUT).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> GLITTERING_GRENADINE = ITEMS.register("glittering_grenadine",
			() -> new BoozeItem(1, 5, new Item.Properties().food(BCFoods.GLITTERING_GRENADINE).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> BLOODY_MARY = ITEMS.register("bloody_mary",
			() -> new BoozeItem(1, 12, new Item.Properties().food(BCFoods.BLOODY_MARY).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> RED_RUM = ITEMS.register("red_rum",
			() -> new BoozeItem(1, 18, new Item.Properties().food(BCFoods.RED_RUM).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> WITHERING_DROSS = ITEMS.register("withering_dross",
			() -> new BoozeItem(3, 20, new Item.Properties().food(BCFoods.WITHERING_DROSS).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> KOMBUCHA = ITEMS.register("kombucha",
			() -> new BoozeItem(1, 5, new Item.Properties().food(BCFoods.KOMBUHCA).stacksTo(16).craftRemainder(BCItems.TANKARD.get()).tab(BrewinAndChewin.CREATIVE_TAB)));
	
	
	public static final RegistryObject<Item> KIMCHI = ITEMS.register("kimchi",
			() -> new Item(new Item.Properties().food(BCFoods.KIMCHI).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> JERKY = ITEMS.register("jerky",
			() -> new Item(new Item.Properties().food(BCFoods.JERKY).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> PICKLED_PICKLES = ITEMS.register("pickled_pickles",
			() -> new Item(new Item.Properties().food(BCFoods.PICKLED_PICKLES).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> KIPPERS = ITEMS.register("kippers",
			() -> new Item(new Item.Properties().food(BCFoods.KIPPERS).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> COCOA_FUDGE = ITEMS.register("cocoa_fudge",
			() -> new Item(new Item.Properties().food(BCFoods.COCOA_FUDGE).tab(BrewinAndChewin.CREATIVE_TAB)));
	

	public static final RegistryObject<Item> FLAXEN_CHEESE_WHEEL = ITEMS.register("flaxen_cheese_wheel",
			() -> new BlockItem(BCBlocks.FLAXEN_CHEESE_WHEEL.get(), new Item.Properties().stacksTo(16).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> UNRIPE_FLAXEN_CHEESE_WHEEL = ITEMS.register("unripe_flaxen_cheese_wheel",
			() -> new BlockItem(BCBlocks.UNRIPE_FLAXEN_CHEESE_WHEEL.get(), new Item.Properties().stacksTo(16).tab(BrewinAndChewin.CREATIVE_TAB)));
	
	public static final RegistryObject<Item> SCARLET_CHEESE_WHEEL = ITEMS.register("scarlet_cheese_wheel",
			() -> new BlockItem(BCBlocks.SCARLET_CHEESE_WHEEL.get(), new Item.Properties().stacksTo(16).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> UNRIPE_SCARLET_CHEESE_WHEEL = ITEMS.register("unripe_scarlet_cheese_wheel",
			() -> new BlockItem(BCBlocks.UNRIPE_SCARLET_CHEESE_WHEEL.get(), new Item.Properties().stacksTo(16).tab(BrewinAndChewin.CREATIVE_TAB)));
	
	public static final RegistryObject<Item> FLAXEN_CHEESE_WEDGE = ITEMS.register("flaxen_cheese_wedge",
			() -> new Item(new Item.Properties().food(BCFoods.FLAXEN_CHEESE).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> SCARLET_CHEESE_WEDGE = ITEMS.register("scarlet_cheese_wedge",
			() -> new Item(new Item.Properties().food(BCFoods.SCARLET_CHEESE).tab(BrewinAndChewin.CREATIVE_TAB)));
	
	public static final RegistryObject<Item> PIZZA = ITEMS.register("pizza",
			() -> new BlockItem(BCBlocks.PIZZA.get(), new Item.Properties().stacksTo(1).tab(BrewinAndChewin.CREATIVE_TAB)));
	public static final RegistryObject<Item> PIZZA_SLICE = ITEMS.register("pizza_slice",
			() -> new Item(new Item.Properties().food(BCFoods.PIZZA_SLICE).tab(BrewinAndChewin.CREATIVE_TAB)));
	
	
	//public static final RegistryObject<Item> FIERY_FONDUE_POT = ITEMS.register("fiery_fondue_pot",
			//() -> new BlockItem(BCBlocks.FIERY_FONDUE_POT.get(), new Item.Properties().stacksTo(1).tab(BrewinAndChewin.CREATIVE_TAB)));
	//public static final RegistryObject<Item> FIERY_FONDUE = ITEMS.register("fiery_fondue",
			//() -> new ConsumableItem(new Item.Properties().food(BCFoods.FIERY_FONDUE).craftRemainder(Items.BOWL).tab(BrewinAndChewin.CREATIVE_TAB)));
	
	public static final RegistryObject<Item> HAM_AND_CHEESE_SANDWICH = ITEMS.register("ham_and_cheese_sandwich",
			() -> new Item(new Item.Properties().food(BCFoods.HAM_AND_CHEESE_SANDWICH).tab(BrewinAndChewin.CREATIVE_TAB)));
}
