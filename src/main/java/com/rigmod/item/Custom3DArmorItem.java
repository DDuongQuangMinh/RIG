package com.rigmod.item;

import com.rigmod.client.StandardLevel1HelmetModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
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

    public Custom3DArmorItem(ArmorMaterial material, Type type, Properties properties) {
        super(material, type, properties);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (!level.isClientSide()) {
            CompoundTag playerNBT = player.getPersistentData();
            int currentMode = stack.getOrCreateTag().getInt("VisionMode");
            int prevMode = playerNBT.getInt("RigMod_PrevVisionMode");

            // 1. CLEANUP: Remove Night Vision if switching OUT of NVG modes OR into Thermal
            // This ensures no "green tint" or "brightness" interferes with the blue thermal fog
            if (currentMode != prevMode) {
                if (currentMode == 0 || currentMode == 3) {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                }
            }

            // 2. MODE LOGIC
            switch (currentMode) {
                case 1: // Green NVG
                case 2: // White Phosphor NVG
                    player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
                    break;

                case 3: // THERMAL VISION
                    // Force remove Night Vision every tick to keep the world dark for the Blue Fog
                    player.removeEffect(MobEffects.NIGHT_VISION);

                    // Scan for heat signatures (Living Entities)
                    AABB boundingBox = player.getBoundingBox().inflate(30.0D);
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, boundingBox, entity -> entity != player);
                    
                    for (LivingEntity target : entities) {
                        // Apply short glowing effect to see signatures through walls
                        target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false, false));
                    }
                    break;
            }

            // 3. UPDATE STATE
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
                return _default; 
            }
        });
    }
}