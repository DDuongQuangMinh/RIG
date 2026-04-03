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

    // Helmet (Passes null because it doesn't need a texture override)
    public static final RegistryObject<Item> STANDARD_LEVEL_1_HELMET = ITEMS.register("standard_level_1_helmet",
            () -> new Custom3DArmorItem(ModArmorMaterials.STANDARD_LEVEL_1, ArmorItem.Type.HELMET, new Item.Properties(), null));

    // BRONZE Chestplate
    public static final RegistryObject<Item> STANDARD_LEVEL_1_CHEST_BRONZE = ITEMS.register("standard_level_1_chest_bronze",
            () -> new Custom3DArmorItem(ModArmorMaterials.STANDARD_LEVEL_1, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "standard_level_1_chest_bronze.png"));

    // WHITE Chestplate
    public static final RegistryObject<Item> STANDARD_LEVEL_1_CHEST_WHITE = ITEMS.register("standard_level_1_chest_white",
            () -> new Custom3DArmorItem(ModArmorMaterials.STANDARD_LEVEL_1, ArmorItem.Type.CHESTPLATE, new Item.Properties(), "standard_level_1_chest_white.png"));

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}