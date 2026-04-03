package com.rigmod.item;

import com.rigmod.RigMod;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import java.util.function.Supplier;

public enum ModArmorMaterials implements ArmorMaterial {
    // We removed the confusing number arrays from this line
    STANDARD_LEVEL_1("standard_level_1", 15, 9, SoundEvents.ARMOR_EQUIP_IRON, 0.0F, 0.0F, () -> Ingredient.of(ModItems.TITANIUM_INGOT.get()));

    private final String name;
    private final int durabilityMultiplier;
    private final int enchantability;
    private final SoundEvent equipSound;
    private final float toughness;
    private final float knockbackResistance;
    private final Supplier<Ingredient> repairIngredient;

    ModArmorMaterials(String name, int durabilityMultiplier, int enchantability, SoundEvent equipSound, float toughness, float knockbackResistance, Supplier<Ingredient> repairIngredient) {
        this.name = name;
        this.durabilityMultiplier = durabilityMultiplier;
        this.enchantability = enchantability;
        this.equipSound = equipSound;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;
        this.repairIngredient = repairIngredient;
    }

    // FIXED: Explicitly define the base durability for every armor piece
    @Override
    public int getDurabilityForType(ArmorItem.Type type) {
        int baseDurability = 0;
        switch (type) {
            case HELMET: baseDurability = 11; break;
            case CHESTPLATE: baseDurability = 16; break;
            case LEGGINGS: baseDurability = 15; break;
            case BOOTS: baseDurability = 13; break;
        }
        return baseDurability * this.durabilityMultiplier;
    }

    // FIXED: Explicitly define the defense points for every armor piece
    @Override
    public int getDefenseForType(ArmorItem.Type type) {
        switch (type) {
            case HELMET: return 2;
            case CHESTPLATE: return 6; // Your RIG suit now correctly gets 6 armor points!
            case LEGGINGS: return 5;
            case BOOTS: return 2;
            default: return 0;
        }
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