package com.brewinandchewin.common.item;

import com.brewinandchewin.core.registry.BCEffects;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class DreadNogItem extends BoozeItem {

	public DreadNogItem(int potency, int duration, Properties properties) {
		super(potency, duration, properties);
	}
	
	@Override
	public void affectConsumer(ItemStack stack, Level level, LivingEntity consumer) {
		MobEffectInstance badOmenEffect = consumer.getEffect(MobEffects.BAD_OMEN);
		if (!consumer.hasEffect(MobEffects.BAD_OMEN)) {
			consumer.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 12000, 0), consumer);
		}
		else if (badOmenEffect.getAmplifier() < 2) {
			consumer.addEffect(new MobEffectInstance(MobEffects.BAD_OMEN, 12000, badOmenEffect.getAmplifier() + 1), consumer);
		}
		if (consumer.hasEffect(BCEffects.TIPSY.get())) {
			MobEffectInstance tipsyEffect = consumer.getEffect(BCEffects.TIPSY.get());
			consumer.addEffect(new MobEffectInstance(BCEffects.TIPSY.get(), tipsyEffect.getDuration() + (duration * 600), tipsyEffect.getAmplifier() + potency), consumer);
		} else if (!consumer.hasEffect(BCEffects.TIPSY.get())) {
			consumer.addEffect(new MobEffectInstance(BCEffects.TIPSY.get(), duration * 1200, potency - 1), consumer);
		}
	}
}
