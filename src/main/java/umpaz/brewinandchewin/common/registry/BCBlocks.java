package umpaz.brewinandchewin.common.registry;

import net.minecraft.world.level.block.*;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.block.*;

public class BCBlocks
{
	public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, BrewinAndChewin.MODID);

	// Workstations
	public static final RegistryObject<Block> KEG = BLOCKS.register("keg", KegBlock::new);

}
