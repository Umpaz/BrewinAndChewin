package com.brewinandchewin.common.item;

import java.util.List;

import javax.annotation.Nullable;

import com.brewinandchewin.core.registry.BCEffects;
import com.brewinandchewin.core.registry.BCItems;
import com.brewinandchewin.core.utility.BCTextUtils;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vectorwing.farmersdelight.common.item.DrinkableItem;

public class BoozeItem extends DrinkableItem {
	protected final int potency;
	protected final int duration;

	public BoozeItem(int potency, int duration, Properties properties) {
		super(properties);
		this.potency = potency;
		this.duration = duration;
	}

	@Override
	public void affectConsumer(ItemStack stack, Level level, LivingEntity consumer) {
		if (consumer.hasEffect(BCEffects.TIPSY.get())) {
			MobEffectInstance effect = consumer.getEffect(BCEffects.TIPSY.get());
			if (effect.getAmplifier() == 8) {
				consumer.addEffect(new MobEffectInstance(BCEffects.TIPSY.get(), effect.getDuration() + (duration * 600), effect.getAmplifier() + 1), consumer);
			}
			if (effect.getAmplifier() == 7 && potency > 2) {
				consumer.addEffect(new MobEffectInstance(BCEffects.TIPSY.get(), effect.getDuration() + (duration * 600), effect.getAmplifier() + 2), consumer);
			}
			if (effect.getAmplifier() < 9) {
				consumer.addEffect(new MobEffectInstance(BCEffects.TIPSY.get(), effect.getDuration() + (duration * 600), effect.getAmplifier() + potency), consumer);
			}
			if (effect.getAmplifier() == 9) {
				consumer.addEffect(new MobEffectInstance(BCEffects.TIPSY.get(), effect.getDuration() + (duration * 600), effect.getAmplifier()), consumer);
			}
		} else if (!consumer.hasEffect(BCEffects.TIPSY.get())) {
			consumer.addEffect(new MobEffectInstance(BCEffects.TIPSY.get(), duration * 1200, potency - 1), consumer);
		}
	}
	
	@Override
	@OnlyIn(Dist.CLIENT)
	public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
		if (potency == 1) {
			MutableComponent textTipsy = BCTextUtils.getTranslation("tooltip.tipsy1", duration);
			tooltip.add(textTipsy.withStyle(ChatFormatting.RED));
		}
		if (potency == 2) {
			MutableComponent textTipsy = BCTextUtils.getTranslation("tooltip.tipsy2", duration);
			tooltip.add(textTipsy.withStyle(ChatFormatting.RED));
		}
		if (potency == 3) {
			MutableComponent textTipsy = BCTextUtils.getTranslation("tooltip.tipsy3", duration);
			tooltip.add(textTipsy.withStyle(ChatFormatting.RED));
		}
		BCTextUtils.addFoodEffectTooltip(stack, tooltip, 1.0F);
		if (stack.sameItem(new ItemStack(BCItems.DREAD_NOG.get()))) {
			MutableComponent textEmpty = BCTextUtils.getTranslation("tooltip." + this);
			tooltip.add(textEmpty.withStyle(ChatFormatting.RED));
		}
	}	
}