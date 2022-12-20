package umpaz.brewinandchewin.common.registry;

import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import umpaz.brewinandchewin.BrewinAndChewin;
import umpaz.brewinandchewin.common.block.entity.KegBlockEntity;

public class BCBlockEntityTypes
{
	public static final DeferredRegister<BlockEntityType<?>> TILES = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, BrewinAndChewin.MODID);

	public static final RegistryObject<BlockEntityType<KegBlockEntity>> KEG = TILES.register("keg",
			() -> BlockEntityType.Builder.of(KegBlockEntity::new, BCBlocks.KEG.get()).build(null));

}
