package com.rigmod.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.rigmod.RigMod;
import com.rigmod.client.Level2HelmetModel;
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
    private final int armorLevel; 
    private Multimap<Attribute, AttributeModifier> customModifiers;

    private static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("d2b3dbe3-0834-4373-90b7-eba5785e061b");
    private static final UUID SPEED_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");

    public Custom3DArmorItem(ArmorMaterial material, Type type, Properties properties, String customTexture, int armorLevel) {
        super(material, type, properties);
        this.customTexture = customTexture;
        this.armorLevel = armorLevel;
    }

    public int getArmorLevel() {
        return this.armorLevel;
    }

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
                if (this.getType() == Type.LEGGINGS) {
                    builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(
                            SPEED_MODIFIER_UUID, "Leggings speed boost", 0.20D, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                this.customModifiers = builder.build();
            }
            return this.customModifiers;
        }
        return super.getDefaultAttributeModifiers(slot);
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
        if (level.isClientSide()) return;

        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chest.getItem() instanceof Custom3DArmorItem chestItem && chestItem.getType() == Type.CHESTPLATE)) {
            if (player.getHealth() > player.getMaxHealth()) {
                player.setHealth(player.getMaxHealth());
            }
        }

        // --- HELMET LOGIC ---
        if (this.getType() == Type.HELMET) {
            CompoundTag tag = stack.getOrCreateTag();
            int visionMode = tag.getInt("VisionMode");

            // LEVEL 1 HELMET 
            if (this.armorLevel == 1) {
                if (visionMode == 1 || visionMode == 2) {
                    MobEffectInstance currentNV = player.getEffect(MobEffects.NIGHT_VISION);
                    if (currentNV == null || currentNV.getDuration() <= 200) {
                        player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
                    }
                } else if (visionMode == 3) {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                    AABB box = player.getBoundingBox().inflate(40.0D);
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player);
                    for (LivingEntity target : entities) {
                        MobEffectInstance currentGlowing = target.getEffect(MobEffects.GLOWING);
                        if (currentGlowing == null || currentGlowing.getDuration() <= 5) {
                            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false, false));
                        }
                    }
                } else {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                }
            } 
            
            // LEVEL 2 HELMET
            else if (this.armorLevel == 2) {
                // 1. Permanently banish Night Vision
                player.removeEffect(MobEffects.NIGHT_VISION);

                // 2. Thermal Vision ON
                if (visionMode > 0) {
                    AABB box = player.getBoundingBox().inflate(40.0D);
                    List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player);
                    for (LivingEntity target : entities) {
                        MobEffectInstance currentGlowing = target.getEffect(MobEffects.GLOWING);
                        if (currentGlowing == null || currentGlowing.getDuration() <= 5) {
                            target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false, false));
                        }
                    }
                }

                // 3. RESTORED: Action Bar Radar Messages!
                int radarMode = tag.getInt("RadarMode");
                if (radarMode > 0 && player.tickCount % 20 == 0) {
                    // Match the visual scan range
                    AABB radarBox = player.getBoundingBox().inflate(200.0D); 
                    int entityCount = 0;
                    int playerCount = 0;

                    if (radarMode == 1 || radarMode == 3) {
                        List<LivingEntity> mobs = level.getEntitiesOfClass(LivingEntity.class, radarBox, e -> e != player && !(e instanceof Player));
                        entityCount = mobs.size();
                    }
                    if (radarMode == 2 || radarMode == 3) {
                        List<Player> players = level.getEntitiesOfClass(Player.class, radarBox, e -> e != player);
                        playerCount = players.size();
                    }

                    // §a colors the text Minecraft Green to match your HUD
                    String radarMsg = "§a[RADAR] ";
                    if (radarMode == 1) radarMsg += "Scanning Mobs (" + entityCount + " detected)";
                    else if (radarMode == 2) radarMsg += "Scanning Players (" + playerCount + " detected)";
                    else if (radarMode == 3) radarMsg += "Scanning All (Mobs: " + entityCount + " | Players: " + playerCount + ")";

                    player.displayClientMessage(net.minecraft.network.chat.Component.literal(radarMsg), true);
                }
            }
        }

        // --- CHESTPLATE LOGIC ---
        if (this.getType() == Type.CHESTPLATE) {
            if (player.tickCount % 40 == 0) {
                if (player.getHealth() < player.getMaxHealth()) {
                    player.heal(1.0F);
                }
            }
        }
    }

    // --- MODEL ---
    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                if (armorSlot == EquipmentSlot.HEAD) {
                    if (Custom3DArmorItem.this.armorLevel == 2) {
                        Level2HelmetModel<?> customModel2 = new Level2HelmetModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(Level2HelmetModel.LAYER_LOCATION));
                        customModel2.young = _default.young; customModel2.crouching = _default.crouching; customModel2.riding = _default.riding; customModel2.rightArmPose = _default.rightArmPose; customModel2.leftArmPose = _default.leftArmPose;
                        return customModel2;
                    } else {
                        StandardLevel1HelmetModel<?> customModel1 = new StandardLevel1HelmetModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(StandardLevel1HelmetModel.LAYER_LOCATION));
                        customModel1.young = _default.young; customModel1.crouching = _default.crouching; customModel1.riding = _default.riding; customModel1.rightArmPose = _default.rightArmPose; customModel1.leftArmPose = _default.leftArmPose;
                        return customModel1;
                    }
                }
                if (armorSlot == EquipmentSlot.CHEST) {
                    StandardLevel1ChestModel<?> customModel = new StandardLevel1ChestModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(StandardLevel1ChestModel.LAYER_LOCATION));
                    customModel.young = _default.young; customModel.crouching = _default.crouching; customModel.riding = _default.riding; customModel.rightArmPose = _default.rightArmPose; customModel.leftArmPose = _default.leftArmPose;
                    return customModel;
                }
                if (armorSlot == EquipmentSlot.LEGS) {
                    StandardLevel1LeggingsModel<?> customModel = new StandardLevel1LeggingsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(StandardLevel1LeggingsModel.LAYER_LOCATION));
                    customModel.young = _default.young; customModel.crouching = _default.crouching; customModel.riding = _default.riding; customModel.rightArmPose = _default.rightArmPose; customModel.leftArmPose = _default.leftArmPose;
                    return customModel;
                }
                return _default;
            }
        });
    }
}