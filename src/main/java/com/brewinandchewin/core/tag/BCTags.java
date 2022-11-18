package com.brewinandchewin.core.tag;

import com.brewinandchewin.core.BrewinAndChewin;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class BCTags {

	public BCTags() {
		super();
	}
	
	// Tea Leaves
	public static final TagKey<Item> RAW_MEATS = modItemTag("raw_meats");
	
	public static final TagKey<Block> HOT_BLOCK = modBlockTag("hot_blocks");
	public static final TagKey<Block> COLD_BLOCK = modBlockTag("cold_blocks");

	private static TagKey<Item> modItemTag(String path) {
		return TagKey.create(Registry.ITEM_REGISTRY, new ResourceLocation(BrewinAndChewin.MODID + ":" + path));
	}
	private static TagKey<Block> modBlockTag(String path) {
		return TagKey.create(Registry.BLOCK_REGISTRY, new ResourceLocation(BrewinAndChewin.MODID + ":" + path));
	}
}