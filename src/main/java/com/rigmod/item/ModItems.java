package com.rigmod.item;

import com.rigmod.RigMod;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {
    public static final DeferredRegister<Item> ITEMS = 
            DeferredRegister.create(ForgeRegistries.ITEMS, RigMod.MODID);

    public static final RegistryObject<Item> TITANIUM_INGOT = ITEMS.register("titanium_ingot",
            () -> new Item(new Item.Properties()));

    // --- LEVEL 1 ARMOR (Tier 1) ---
    
    // Helmet
    public static final RegistryObject<Item> STANDARD_LEVEL_1_HELMET = ITEMS.register("standard_level_1_helmet",
            () -> new Custom3DArmorItem(ModArmorMaterials.STANDARD_LEVEL_1, ArmorItem.Type.HELMET, new Item.Properties(), null, 1));

    // BRONZE Chestplate
    public static final RegistryObject<Item> STANDARD_LEVEL_1_CHEST_BRONZE = ITEMS.register("standard_level_1_chest_bronze",
            () -> new Custom3DArmorItem(ModArmorMaterials.STANDARD_LEVEL_1, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "standard_level_1_chest_bronze.png", 1));

    // WHITE Chestplate
    public static final RegistryObject<Item> STANDARD_LEVEL_1_CHEST_WHITE = ITEMS.register("standard_level_1_chest_white",
            () -> new Custom3DArmorItem(ModArmorMaterials.STANDARD_LEVEL_1, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "standard_level_1_chest_white.png", 1));

    // Leggings
    public static final RegistryObject<Item> STANDARD_LEVEL_1_LEGGINGS = ITEMS.register("standard_level_1_legging",
            () -> new Custom3DArmorItem(ModArmorMaterials.STANDARD_LEVEL_1, ArmorItem.Type.LEGGINGS, new Item.Properties(), "standard_level_1_legging.png", 1));

    // --- LEVEL 2 ARMOR (Tier 2) ---
    
    // FIXED: Engineering Level 2 Helmet now uses LEVEL_2_ENGINEERING and is fire resistant!
    public static final RegistryObject<Item> ENGINEERING_LEVEL_2_HELMET = ITEMS.register("engineering_level_2_helmet", 
            () -> new Custom3DArmorItem(ModArmorMaterials.LEVEL_2_ENGINEERING, ArmorItem.Type.HELMET, new Item.Properties().fireResistant(), "engineering_level_2_helmet.png", 2));

    // NEW: Level 2 Chestplate
    public static final RegistryObject<Item> ENGINEERING_LEVEL_2_CHESTPLATE = ITEMS.register("engineering_level_2_chestplate", 
            () -> new Custom3DArmorItem(ModArmorMaterials.LEVEL_2_ENGINEERING, ArmorItem.Type.CHESTPLATE, new Item.Properties().fireResistant(), "engineering_level_2_chestplate.png", 2));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}