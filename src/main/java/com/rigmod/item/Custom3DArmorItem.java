package com.rigmod.item;

import com.rigmod.RigMod;
import com.rigmod.client.StandardLevel1ChestModel;
import com.rigmod.client.StandardLevel1HelmetModel;
import com.rigmod.client.StandardLevel1LeggingsModel; // 1. ADDED THIS IMPORT
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Consumer;

public class Custom3DArmorItem extends ArmorItem {

    private final String customTexture; 

    public Custom3DArmorItem(ArmorMaterial material, Type type, Properties properties, String customTexture) {
        super(material, type, properties);
        this.customTexture = customTexture;
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (this.customTexture != null && !this.customTexture.isEmpty()) {
            return RigMod.MODID + ":textures/models/armor/" + this.customTexture;
        }
        return super.getArmorTexture(stack, entity, slot, type);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide() && this.getType() == Type.HELMET) {
            CompoundTag playerNBT = player.getPersistentData();
            int currentMode = stack.getOrCreateTag().getInt("VisionMode");
            int prevMode = playerNBT.getInt("RigMod_PrevVisionMode");

            if (currentMode != prevMode) {
                if (currentMode == 0 || currentMode == 3) {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                }
            }

            switch (currentMode) {
                case 1:
                case 2:
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
                    break;
                case 3:
                    player.removeEffect(MobEffects.NIGHT_VISION);
                    AABB boundingBox = player.getBoundingBox().inflate(40.0D);
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox, entity -> entity != player);
                    for (LivingEntity target : entities) {
                        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false, false));
                    }
                    break;
            }

            playerNBT.putInt("RigMod_PrevVisionMode", currentMode);
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                
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

                // 2. --- NEW: ADDED LEGGINGS LOGIC HERE ---
                if (armorSlot == EquipmentSlot.LEGS) {
                    StandardLevel1LeggingsModel<?> customModel = new StandardLevel1LeggingsModel<>(
                            Minecraft.getInstance().getEntityModels().bakeLayer(StandardLevel1LeggingsModel.LAYER_LOCATION));
                    // We copy over the vanilla animation states so your leggings bend when you crouch/ride!
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