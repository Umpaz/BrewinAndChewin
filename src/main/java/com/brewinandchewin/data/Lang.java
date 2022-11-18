package com.brewinandchewin.data;

import static com.brewinandchewin.core.registry.BCBlocks.BLOCKS;
import static com.brewinandchewin.core.registry.BCEffects.EFFECTS;
import static com.brewinandchewin.core.registry.BCItems.ITEMS;

import java.util.HashSet;
import java.util.Set;

import com.brewinandchewin.core.BrewinAndChewin;

import net.minecraft.data.DataGenerator;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.WallSignBlock;
import net.minecraft.world.level.block.WallTorchBlock;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.registries.RegistryObject;


public class Lang extends LanguageProvider {
    public Lang(DataGenerator gen) {
        super(gen, BrewinAndChewin.MODID, "en_us");
    }

    @Override
    protected void addTranslations() {
        Set<RegistryObject<Block>> blocks = new HashSet<>(BLOCKS.getEntries());
        Set<RegistryObject<Item>> items = new HashSet<>(ITEMS.getEntries());
        Set<RegistryObject<MobEffect>> effects = new HashSet<>(EFFECTS.getEntries());
        blocks.removeIf(b -> b.get() instanceof WallTorchBlock);
        blocks.removeIf(b -> b.get() instanceof WallSignBlock);
        blocks.forEach(b ->
        {
            String name = b.get().getDescriptionId().replaceFirst("block.brewinandchewin.", "");
            name = toTitleCase(correctBlockItemName(name), "_").replaceAll("Of", "of");
            add(b.get().getDescriptionId(), name);
        });
        items.removeIf(i -> i.get() instanceof BlockItem);
        items.forEach(i ->
        {
            String name = i.get().getDescriptionId().replaceFirst("item.brewinandchewin.", "");
            name = toTitleCase(correctBlockItemName(name), "_").replaceAll("Of", "of");
            add(i.get().getDescriptionId(), name);
        });

        effects.forEach(e -> {
            String name = toTitleCase(e.getId().getPath(), "_");
            add("effect.brewinandchewin." + e.get().getRegistryName().getPath(), name);
        });

        add("itemGroup." + BrewinAndChewin.MODID, "Brewin' and Chewin'");
        add("brewinandchewin.container.keg", "Keg");
        add("brewinandchewin.tooltip.tipsy1", "Tipsy I (%s:00)");
        add("brewinandchewin.tooltip.tipsy2", "Tipsy II (%s:00)");
        add("brewinandchewin.tooltip.tipsy3", "Tipsy III (%s:00)");
        add("brewinandchewin.jei.fermenting", "Fermenting");
        add("brewinandchewin.container.keg.served_in", "Served in: %s");
        add("brewinandchewin.tooltip.keg.empty", "Empty");
        add("brewinandchewin.tooltip.keg.single_serving", "Holds 1 serving of:");
        add("brewinandchewin.tooltip.keg.many_servings", "Holds %s serving of:");
        add("brewinandchewin.tooltip.dread_nog", "Bad Omen (10:00)");
        add("brewinandchewin.block.cheese.use_knife", "You need a Knife to cut this.");

        addTemperature("frigid");
        addTemperature("cold");
        addTemperature("normal");
        addTemperature("warm");
        addTemperature("hot");
    }

    @Override
    public String getName() {
        return "Lang Entries";
    }

    public void addTemperature(String temperature) {
        add("brewinandchewin.container.keg." + temperature, "Temperature: " + toTitleCase(temperature, "_"));

    }
    public static String toTitleCase(String givenString, String regex) {
        String[] stringArray = givenString.split(regex);
        StringBuilder stringBuilder = new StringBuilder();
        for (String string : stringArray) {
            stringBuilder.append(Character.toUpperCase(string.charAt(0))).append(string.substring(1)).append(regex);
        }
        return stringBuilder.toString().trim().replaceAll(regex, " ").substring(0, stringBuilder.length() - 1);
    }

    public String correctBlockItemName(String name) {
        if ((!name.endsWith("_bricks"))) {
            if (name.contains("bricks")) {
                name = name.replaceFirst("bricks", "brick");
            }
        }
        if (name.contains("_fence") || name.contains("_button")) {
            if (name.contains("planks")) {
                name = name.replaceFirst("_planks", "");
            }
        }
        return name;
    }
} 