package com.brewinandchewin.core.registry;

import com.brewinandchewin.common.block.*;
import com.brewinandchewin.core.BrewinAndChewin;

import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import vectorwing.farmersdelight.common.block.CuttingBoardBlock;

public class BCBlocks
{
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BrewinAndChewin.MODID);

	//Blocks

	// Workstations
	public static final RegistryObject<Block> KEG = BLOCKS.register("keg", KegBlock::new);
	public static final RegistryObject<Block> ITEM_MAT = BLOCKS.register("item_mat", ItemMatBlock::new);

	
	//public static final RegistryObject<Block> FIERY_FONDUE_POT = BLOCKS.register("fiery_fondue_pot", () -> new
	//		FonduePotBlock(Block.Properties.copy(Blocks.CAULDRON)));
	
	public static final RegistryObject<Block> PIZZA = BLOCKS.register("pizza", () -> new 
			PizzaBlock(Block.Properties.copy(Blocks.CAKE)));

	public static final RegistryObject<Block> FLAXEN_CHEESE_WHEEL = BLOCKS.register("flaxen_cheese_wheel", () -> new 
			RipeCheeseWheelBlock(BCItems.FLAXEN_CHEESE_WEDGE, Block.Properties.copy(Blocks.CAKE)));
	
	public static final RegistryObject<Block> SCARLET_CHEESE_WHEEL = BLOCKS.register("scarlet_cheese_wheel", () -> new 
			RipeCheeseWheelBlock(BCItems.SCARLET_CHEESE_WEDGE, Block.Properties.copy(Blocks.CAKE)));
	
	public static final RegistryObject<Block> UNRIPE_FLAXEN_CHEESE_WHEEL = BLOCKS.register("unripe_flaxen_cheese_wheel", () -> new 
			CheeseWheelBlock(BCBlocks.FLAXEN_CHEESE_WHEEL, Block.Properties.copy(Blocks.CAKE)));
	
	public static final RegistryObject<Block> UNRIPE_SCARLET_CHEESE_WHEEL = BLOCKS.register("unripe_scarlet_cheese_wheel", () -> new 
			CheeseWheelBlock(BCBlocks.SCARLET_CHEESE_WHEEL, Block.Properties.copy(Blocks.CAKE)));
	

}
