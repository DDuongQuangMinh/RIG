package com.rigmod.item;

import com.rigmod.RigMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
    // LEVEL 1: Iron-ish stats
    STANDARD_LEVEL_1("standard_level_1", 15, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, 
            new int[]{2, 5, 6, 2}, () -> Ingredient.of(ModItems.TITANIUM_INGOT.get())),

    // LEVEL 2: Exact Netherite stats (Durability 37, Toughness 3.0, Knockback Res 0.1)
    LEVEL_2_ENGINEERING("level_2_helmet", 37, 15, SoundEvents.ARMOR_EQUIP_NETHERITE, 3.0F, 0.1F, 
            new int[]{3, 6, 8, 3}, () -> Ingredient.of(Items.NETHERITE_INGOT));

    private final String name;
    private final int durabilityMultiplier;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final int[] protectionAmounts; // Added to store defense per piece
    private final Supplier<Ingredient> repairIngredient;

    ModArmorMaterials(String name, int durabilityMultiplier, int enchantability, SoundEvent equipSound, 
                      float toughness, float knockbackResistance, int[] protectionAmounts, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.protectionAmounts = protectionAmounts;
        this.repairIngredient = repairIngredient;
    }

    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        // Standard Minecraft base durability values
        int baseDurability = switch (type) {
            case HELMET -> 11;
            case CHESTPLATE -> 16;
            case LEGGINGS -> 15;
            case BOOTS -> 13;
        };
        return baseDurability * this.durabilityMultiplier;
    }

    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        // Pulls the defense from the array we defined in the enum constants above
        return this.protectionAmounts[type.getSlot().getIndex()];
    }

    @Override
    public int getEnchantmentValue() {
        return this.enchantability;
    }

    @Override
    public SoundEvent getEquipSound() {
        return this.equipSound; 
    }

    @Override
    public Ingredient getRepairIngredient() {
        return this.repairIngredient.get();
    }

    @Override
    public String getName() {
        return RigMod.MODID + ":" + this.name; 
    }

    @Override
    public float getToughness() {
        return this.toughness;
    }

    @Override
    public float getKnockbackResistance() {
        return this.knockbackResistance;
    }
}