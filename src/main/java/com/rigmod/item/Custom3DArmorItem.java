package com.rigmod.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.rigmod.RigMod;
import com.rigmod.client.StandardLevel1ChestModel;
import com.rigmod.client.StandardLevel1HelmetModel;
import com.rigmod.client.StandardLevel1LeggingsModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Custom3DArmorItem extends ArmorItem {

    private final String customTexture;
    private Multimap<Attribute, AttributeModifier> customModifiers;

    // Unique IDs so our armor stats don't conflict with other items
    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("d2b3dbe3-0834-4373-90b7-eba5785e061b");
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");

    public Custom3DArmorItem(ArmorMaterial material, Type type, Properties properties, String customTexture) {
        super(material, type, properties);
        this.customTexture = customTexture;
    }

    // --- ATTRIBUTES (CHESTPLATE & LEGGINGS) ---
    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == this.getType().getSlot()) {
            if (this.customModifiers == null) {
                ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                builder.putAll(super.getDefaultAttributeModifiers(slot));

                if (this.getType() == Type.CHESTPLATE) {
                    builder.put(Attributes.MAX_HEALTH, new AttributeModifier(
                            HEALTH_MODIFIER_UUID, "Chestplate health boost", 8.0D, AttributeModifier.Operation.ADDITION));
                }

                // ✅ NEW: Leggings Speed Boost (100% Invisible)
                if (this.getType() == Type.LEGGINGS) {
                    // 0.20D is exactly a 20% speed boost, which equals the Speed I potion effect!
                    builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(
                            SPEED_MODIFIER_UUID, "Leggings speed boost", 0.20D, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }

                this.customModifiers = builder.build();
            }
            return this.customModifiers;
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    // --- TEXTURE ---
    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (this.customTexture != null && !this.customTexture.isEmpty()) {
            return RigMod.MODID + ":textures/models/armor/" + this.customTexture;
        }
        return super.getArmorTexture(stack, entity, slot, type);
    }

    // --- MAIN LOGIC ---
    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.isClientSide()) return;

        // Clamp health if chestplate is removed
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof Custom3DArmorItem chestItem && chestItem.getType() == Type.CHESTPLATE)) {
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }

        // --- HELMET LOGIC ---
        if (this.getType() == Type.HELMET) {
            CompoundTag tag = stack.getOrCreateTag();
            int mode = tag.getInt("VisionMode");

            switch (mode) {
                case 1:
                case 2:
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
                    break;
                case 3:
                    player.removeEffect(MobEffects.NIGHT_VISION);
                    AABB box = player.getBoundingBox().inflate(40.0D);
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player);
                    for (LivingEntity target : entities) {
                        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false, false));
                    }
                    break;
                default:
                    player.removeEffect(MobEffects.NIGHT_VISION);
                    break;
            }
        }

        // --- CHESTPLATE LOGIC ---
        if (this.getType() == Type.CHESTPLATE) {
            // Regen half a heart every 2 seconds
            if (player.tickCount % 40 == 0) {
                if (player.getHealth() < player.getMaxHealth()) {
                    player.heal(1.0F);
                }
            }
        }
        
        // (Leggings tick logic removed because Attributes handle it perfectly now)
    }

    // --- MODEL ---
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(
                    LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {

                if (armorSlot == EquipmentSlot.HEAD) {
                    StandardLevel1HelmetModel<?> customModel = new StandardLevel1HelmetModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(StandardLevel1HelmetModel.LAYER_LOCATION));
                    customModel.young = _default.young;
                    customModel.crouching = _default.crouching;
                    customModel.riding = _default.riding;
                    customModel.rightArmPose = _default.rightArmPose;
                    customModel.leftArmPose = _default.leftArmPose;
                    return customModel;
                }

                if (armorSlot == EquipmentSlot.CHEST) {
                    StandardLevel1ChestModel<?> customModel = new StandardLevel1ChestModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(StandardLevel1ChestModel.LAYER_LOCATION));
                    customModel.young = _default.young;
                    customModel.crouching = _default.crouching;
                    customModel.riding = _default.riding;
                    customModel.rightArmPose = _default.rightArmPose;
                    customModel.leftArmPose = _default.leftArmPose;
                    return customModel;
                }

                if (armorSlot == EquipmentSlot.LEGS) {
                    StandardLevel1LeggingsModel<?> customModel = new StandardLevel1LeggingsModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(StandardLevel1LeggingsModel.LAYER_LOCATION));
                    customModel.young = _default.young;
                    customModel.crouching = _default.crouching;
                    customModel.riding = _default.riding;
                    customModel.rightArmPose = _default.rightArmPose;
                    customModel.leftArmPose = _default.leftArmPose;
                    return customModel;
                }

                return _default;
            }
        });
    }
}