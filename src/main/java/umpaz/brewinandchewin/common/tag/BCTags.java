package umpaz.brewinandchewin.common.tag;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import umpaz.brewinandchewin.BrewinAndChewin;

/**
 * References to tags under the Farmer's Delight namespace.
 * These tags are used for mod mechanics.
 */
public class BCTags
{
	// Blocks that are efficiently mined with a Knife.
	//public static final TagKey<Block> MINEABLE_WITH_KNIFE = modBlockTag("mineable/knife");

	// Hot Blocks
	public static final TagKey<Block> FREEZE_SOURCES = modBlockTag("freeze_sources");

	private static TagKey<Item> modItemTag(String path) {
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(BrewinAndChewin.MODID + ":" + path));
	}

	private static TagKey<Block> modBlockTag(String path) {
		return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(BrewinAndChewin.MODID + ":" + path));
	}
}
