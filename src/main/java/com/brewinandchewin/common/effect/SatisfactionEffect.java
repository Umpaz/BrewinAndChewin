package com.brewinandchewin.common.effect;

import com.brewinandchewin.core.registry.BCEffects;

import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;

public class SatisfactionEffect extends MobEffect {

	public SatisfactionEffect() {
		super(MobEffectCategory.BENEFICIAL, 0); //12161815
	}
	
	@Override
	public void applyEffectTick(LivingEntity entity, int amplifier) {
		if (entity.hasEffect(BCEffects.SATISFACTION.get()) && entity instanceof Player player) {
			FoodData foodData = player.getFoodData();
	         if (foodData.getFoodLevel() < 20) {
	        	 foodData.setFoodLevel(foodData.getFoodLevel() + 1);
	         }
		}
	}

	@Override
	public boolean isDurationEffectTick(int duration, int amplifier) {
		int k = 400 >> amplifier;
	         if (k > 0) {
	        	 return duration % k == 0;
	         } else {
	        	 return true;
	         }
	}
}