package com.brewinandchewin.common.effect;

import java.util.Random;

import com.brewinandchewin.core.registry.BCEffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class TipsyEffect extends MobEffect {

	public TipsyEffect() {
		super(MobEffectCategory.NEUTRAL, 0); //12161815
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		MobEffectInstance effect = entity.getEffect(BCEffects.TIPSY.get());
		if (effect.getAmplifier() > 1) {
			entity.addEffect(new MobEffectInstance(MobEffects.CONFUSION, effect.getDuration(), 0));
		}
		if (effect.getAmplifier() > 8) {
			entity.addEffect(new MobEffectInstance(MobEffects.POISON, effect.getDuration(), 0));
		}
			
		if (effect.getAmplifier() > 3) {
			Random rand = entity.getCommandSenderWorld().getRandom();
			float amount = rand.nextFloat() * (0.06F + (0.01F * effect.getAmplifier()));
			float x = 0.0F;
			float z = 0.0F;
			if (rand.nextBoolean())
				amount *= -1;
			if (rand.nextBoolean())
				x = amount;
			else
				z = amount;
			entity.setDeltaMovement(entity.getDeltaMovement().add(x, 0.0F, z ));					
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		return true;
	}
}