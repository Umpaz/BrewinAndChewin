package com.brewinandchewin.core.registry;

import com.brewinandchewin.common.block.entity.container.KegContainer;
import com.brewinandchewin.core.BrewinAndChewin;

import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class BCContainerTypes
{
	public static final DeferredRegister<MenuType<?>> CONTAINER_TYPES = DeferredRegister.create(ForgeRegistries.CONTAINERS, BrewinAndChewin.MODID);

	public static final RegistryObject<MenuType<KegContainer>> KEG = CONTAINER_TYPES
			.register("keg", () -> IForgeMenuType.create(KegContainer::new));
}
