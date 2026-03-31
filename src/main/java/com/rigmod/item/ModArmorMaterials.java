package com.rigmod.item;

import com.rigmod.RigMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

public enum ModArmorMaterials implements ArmorMaterial {
    // Vanilla Iron Stats:
    // Multiplier: 15, Defense: 2, Enchantability: 9, Toughness: 0.0F, Knockback Resist: 0.0F
    STANDARD_LEVEL_1("standard_level_1", 15, 2, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F);

    private final String name;
    private final int durabilityMultiplier;
    private final int defense;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;

    ModArmorMaterials(String name, int durabilityMultiplier, int defense, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.defense = defense;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
    }

    // FIXED: Changed from getDurabilityForSlot to getDurabilityForType
    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        if (type == ArmorItem.Type.HELMET) {
            return this.durabilityMultiplier * 11;
        }
        return 0; 
    }

    // FIXED: Changed from getDefenseForSlot to getDefenseForType
    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        if (type == ArmorItem.Type.HELMET) {
            return this.defense;
        }
        return 0;
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
        return Ingredient.of(ModItems.TITANIUM_INGOT.get());
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