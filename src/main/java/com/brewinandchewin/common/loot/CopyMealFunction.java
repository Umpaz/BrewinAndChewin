package com.brewinandchewin.common.loot;

import javax.annotation.Nullable;

import com.brewinandchewin.common.block.entity.KegBlockEntity;
import com.brewinandchewin.core.BrewinAndChewin;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;

public class CopyMealFunction extends LootItemConditionalFunction
{
	public static final ResourceLocation ID = new ResourceLocation(BrewinAndChewin.MODID, "copy_meal");

	private CopyMealFunction(LootItemCondition[] conditions) {
		super(conditions);
	}

	public static LootItemConditionalFunction.Builder<?> builder() {
		return simpleBuilder(CopyMealFunction::new);
	}

	@Override
	protected ItemStack run(ItemStack stack, LootContext context) {
		BlockEntity tile = context.getParamOrNull(LootContextParams.BLOCK_ENTITY);
		if (tile instanceof KegBlockEntity) {
			CompoundTag tag = ((KegBlockEntity) tile).writeMeal(new CompoundTag());
			if (!tag.isEmpty()) {
				stack.addTagElement("BlockEntityTag", tag);
			}
		}
		return stack;
	}

	@Override
	@Nullable
	public LootItemFunctionType getType() {
		return null;
	}

	public static class Serializer extends LootItemConditionalFunction.Serializer<CopyMealFunction>
	{
		@Override
		public CopyMealFunction deserialize(JsonObject json, JsonDeserializationContext context, LootItemCondition[] conditions) {
			return new CopyMealFunction(conditions);
		}
	}
}