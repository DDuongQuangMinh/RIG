package com.rigmod.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import com.rigmod.RigMod;
import com.rigmod.client.Level2HelmetModel;
import com.rigmod.client.Level2ChestplateModel; 
import com.rigmod.client.model.StandardLevel1ChestModel;
import com.rigmod.client.model.EngineeringLevel3HelmetModel;
import com.rigmod.client.model.EngineeringLevel3ChestplateModel;
import com.rigmod.client.model.EngineeringLevel3LeggingsModel;
import com.rigmod.client.StandardLevel1HelmetModel;
import com.rigmod.client.StandardLevel1LeggingsModel;
import com.rigmod.client.model.StandardLevel1BootsModel;
import com.rigmod.client.model.EngineeringLevel2BootsModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

public class Custom3DArmorItem extends ArmorItem {

    private final String customTexture;
    private final int armorLevel; 
    private Multimap<Attribute, AttributeModifier> customModifiers;

    public static final UUID HEALTH_MODIFIER_UUID = UUID.fromString("d2b3dbe3-0834-4373-90b7-eba5785e061b");
    public static final UUID SPEED_MODIFIER_UUID = UUID.fromString("a1b2c3d4-e5f6-4a5b-8c9d-0e1f2a3b4c5d");
    public static final UUID KNOCKBACK_MODIFIER_UUID = UUID.fromString("3b9a9a4a-7a58-4521-8289-5369a1b79e78");
    public static final UUID NODE_ARMOR_UUID = UUID.fromString("1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d");
    public static final UUID NODE_SPEED_UUID = UUID.fromString("f0e1d2c3-b4a5-9687-7654-3210fedcba98");

    public Custom3DArmorItem(ArmorMaterial material, Type type, Properties properties, String customTexture, int armorLevel) {
        super(material, type, properties);
        this.customTexture = customTexture;
        this.armorLevel = armorLevel;
    }

    public int getArmorLevel() {
        return this.armorLevel;
    }

    @Override
    public boolean isFireResistant() {
        return this.armorLevel >= 2 || super.isFireResistant();
    }

    @Override
    public boolean isDamageable(ItemStack stack) {
        return this.armorLevel < 2 && super.isDamageable(stack);
    }

    @Override
    public void setDamage(ItemStack stack, int damage) {
        if (this.armorLevel >= 2) return;
        super.setDamage(stack, damage);
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot slot) {
        if (slot == this.getType().getSlot()) {
            if (this.customModifiers == null) {
                ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
                if (this.getType() == Type.HELMET && this.armorLevel >= 2) {
                    builder.put(Attributes.ARMOR, new AttributeModifier(UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB150"), "Armor modifier", 4.5D, AttributeModifier.Operation.ADDITION));
                    builder.put(Attributes.ARMOR_TOUGHNESS, new AttributeModifier(UUID.fromString("2AD3F246-FEE1-4E67-B886-69FD380BB151"), "Armor toughness", 4.5D, AttributeModifier.Operation.ADDITION));
                } else {
                    builder.putAll(super.getDefaultAttributeModifiers(slot));
                }
                if (this.getType() == Type.LEGGINGS) {
                    builder.put(Attributes.MOVEMENT_SPEED, new AttributeModifier(SPEED_MODIFIER_UUID, "Leggings speed boost", 0.20D, AttributeModifier.Operation.MULTIPLY_TOTAL));
                }
                this.customModifiers = builder.build();
            }
            return this.customModifiers;
        }
        return super.getDefaultAttributeModifiers(slot);
    }

    @Override
    public String getArmorTexture(ItemStack stack, Entity entity, EquipmentSlot slot, String type) {
        if (this.customTexture != null && !this.customTexture.isEmpty()) return RigMod.MODID + ":textures/models/armor/" + this.customTexture;
        return super.getArmorTexture(stack, entity, slot, type);
    }

    @Override
    public void onArmorTick(ItemStack stack, Level level, Player player) {
        if (level.isClientSide()) return;

        if (this.getType() == Type.HELMET) {
            CompoundTag tag = stack.getOrCreateTag();
            int visionMode = tag.getInt("VisionMode");
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            int power = 0;
            if (chest.getItem() instanceof Custom3DArmorItem chestItem && chestItem.getType() == Type.CHESTPLATE) {
                power = chest.getOrCreateTag().getInt("RigPower");
            }
            boolean isVisionActive = false;
            if (this.armorLevel == 3 && (visionMode == 0 || visionMode == 1)) isVisionActive = true;
            if (this.armorLevel < 3 && visionMode > 0) isVisionActive = true;
            if (power > 0 && isVisionActive) {
                if (player.tickCount % 400 == 0) {
                    power = Math.max(0, power - 5); 
                    chest.getOrCreateTag().putInt("RigPower", power);
                }
            }

            if (power <= 0) player.removeEffect(MobEffects.NIGHT_VISION);
            else {
                if (this.armorLevel == 1) {
                    if (visionMode == 1 || visionMode == 2) {
                        MobEffectInstance currentNV = player.getEffect(MobEffects.NIGHT_VISION);
                        if (currentNV == null || currentNV.getDuration() <= 200) player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
                    } else if (visionMode == 3) {
                        player.removeEffect(MobEffects.NIGHT_VISION);
                        AABB box = player.getBoundingBox().inflate(40.0D);
                        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player);
                        for (LivingEntity target : entities) {
                            MobEffectInstance currentGlowing = target.getEffect(MobEffects.GLOWING);
                            if (currentGlowing == null || currentGlowing.getDuration() <= 5) target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false, false));
                        }
                    } else player.removeEffect(MobEffects.NIGHT_VISION);
                } 
                else if (this.armorLevel == 2) {
                    player.removeEffect(MobEffects.NIGHT_VISION);
                    if (visionMode > 0) {
                        AABB box = player.getBoundingBox().inflate(40.0D);
                        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player);
                        for (LivingEntity target : entities) {
                            MobEffectInstance currentGlowing = target.getEffect(MobEffects.GLOWING);
                            if (currentGlowing == null || currentGlowing.getDuration() <= 5) target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false, false));
                        }
                    }
                    int radarMode = tag.getInt("RadarMode");
                    if (radarMode > 0 && player.tickCount % 20 == 0) {
                        AABB radarBox = player.getBoundingBox().inflate(200.0D); 
                        int entityCount = 0; int playerCount = 0;
                        if (radarMode == 1 || radarMode == 3) entityCount = level.getEntitiesOfClass(LivingEntity.class, radarBox, e -> e != player && !(e instanceof Player)).size();
                        if (radarMode == 2 || radarMode == 3) playerCount = level.getEntitiesOfClass(Player.class, radarBox, e -> e != player).size();
                        String radarMsg = "§a[RADAR] Scanning " + (radarMode == 1 ? "Mobs" : radarMode == 2 ? "Players" : "All") + " (" + (entityCount + playerCount) + " detected)";
                        player.displayClientMessage(net.minecraft.network.chat.Component.literal(radarMsg), true);
                    }
                }
                else if (this.armorLevel == 3) {
                    if (visionMode == 0 || visionMode == 1) {
                        MobEffectInstance currentNV = player.getEffect(MobEffects.NIGHT_VISION);
                        if (currentNV == null || currentNV.getDuration() <= 200) player.addEffect(new MobEffectInstance(MobEffects.NIGHT_VISION, 220, 0, false, false, false));
                        Scoreboard scoreboard = level.getScoreboard();
                        PlayerTeam redTeam = scoreboard.getPlayerTeam("RigThermalRed");
                        if (redTeam == null) {
                            redTeam = scoreboard.addPlayerTeam("RigThermalRed");
                            redTeam.setColor(net.minecraft.ChatFormatting.RED);
                        }
                        AABB box = player.getBoundingBox().inflate(40.0D);
                        List<LivingEntity> entities = level.getEntitiesOfClass(LivingEntity.class, box, e -> e != player);
                        for (LivingEntity target : entities) {
                            MobEffectInstance currentGlowing = target.getEffect(MobEffects.GLOWING);
                            if (currentGlowing == null || currentGlowing.getDuration() <= 5) target.addEffect(new MobEffectInstance(MobEffects.GLOWING, 10, 0, false, false, false));
                            if (visionMode == 1) scoreboard.addPlayerToTeam(target.getScoreboardName(), redTeam);
                            else if (scoreboard.getPlayersTeam(target.getScoreboardName()) != null && scoreboard.getPlayersTeam(target.getScoreboardName()).getName().equals("RigThermalRed")) scoreboard.removePlayerFromTeam(target.getScoreboardName(), redTeam);
                        }
                    } else player.removeEffect(MobEffects.NIGHT_VISION);

                    int radarMode = tag.getInt("RadarMode");
                    if (radarMode > 0 && player.tickCount % 20 == 0) {
                        AABB radarBox = player.getBoundingBox().inflate(200.0D); 
                        int entityCount = 0; int playerCount = 0;
                        if (radarMode == 1 || radarMode == 3) entityCount = level.getEntitiesOfClass(LivingEntity.class, radarBox, e -> e != player && !(e instanceof Player)).size();
                        if (radarMode == 2 || radarMode == 3) playerCount = level.getEntitiesOfClass(Player.class, radarBox, e -> e != player).size();
                        String radarMsg = "§a[RADAR] Scanning " + (radarMode == 1 ? "Mobs" : radarMode == 2 ? "Players" : "All") + " (" + (entityCount + playerCount) + " detected)";
                        player.displayClientMessage(net.minecraft.network.chat.Component.literal(radarMsg), true);
                    }
                }
            }
        }

        // ==========================================
        // CHESTPLATE LOGIC & UNIVERSAL NODE STATS
        // ==========================================
        if (this.getType() == Type.CHESTPLATE) {
            CompoundTag tag = stack.getOrCreateTag();
            int power = tag.getInt("RigPower");

            if (tag.contains("AttributeModifiers")) tag.remove("AttributeModifiers");

            boolean hasArm1 = tag.getBoolean("RigNode_3");
            boolean hasArm2 = tag.getBoolean("RigNode_12");
            boolean hasSpd = tag.getBoolean("RigNode_6");
            boolean hasCap1 = tag.getBoolean("RigNode_5");
            boolean hasCap2 = tag.getBoolean("RigNode_11");

            int maxPower = 100 + (hasCap1 ? 50 : 0) + (hasCap2 ? 50 : 0);
            if (power > maxPower) {
                power = maxPower;
                tag.putInt("RigPower", power);
            }

            AttributeInstance healthAttr = player.getAttribute(Attributes.MAX_HEALTH);
            AttributeInstance kbAttr = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
            AttributeInstance armorAttr = player.getAttribute(Attributes.ARMOR);
            AttributeInstance speedAttr = player.getAttribute(Attributes.MOVEMENT_SPEED);

            // 🔥 FORCE REMOVE HEARTS TO CLEAN UP THE HUD
            if (healthAttr != null && healthAttr.getModifier(HEALTH_MODIFIER_UUID) != null) {
                healthAttr.removeModifier(HEALTH_MODIFIER_UUID);
                if (player.getHealth() > player.getMaxHealth()) player.setHealth(player.getMaxHealth());
            }

            if (power > 0) {
                /* HEARTS TEMPORARILY DISABLED AS REQUESTED
                if (healthAttr != null) {
                    double targetHealth = (this.armorLevel >= 2) ? 20.0D : 8.0D;
                    if (hasHP1) targetHealth += 10.0D; 
                    if (hasHP2) targetHealth += 10.0D; 
                    AttributeModifier currentMod = healthAttr.getModifier(HEALTH_MODIFIER_UUID);
                    if (currentMod == null || currentMod.getAmount() != targetHealth) {
                        healthAttr.removeModifier(HEALTH_MODIFIER_UUID);
                        healthAttr.addTransientModifier(new AttributeModifier(HEALTH_MODIFIER_UUID, "Chestplate health boost", targetHealth, AttributeModifier.Operation.ADDITION));
                    }
                }
                */
                
                if (kbAttr != null && this.armorLevel >= 2) {
                    if (kbAttr.getModifier(KNOCKBACK_MODIFIER_UUID) == null) kbAttr.addTransientModifier(new AttributeModifier(KNOCKBACK_MODIFIER_UUID, "Chestplate knockback resistance", 1.0D, AttributeModifier.Operation.ADDITION));
                }

                // APPLY NODE ARMOR
                if (armorAttr != null) {
                    double targetArmor = (hasArm1 ? 4.0D : 0.0D) + (hasArm2 ? 4.0D : 0.0D);
                    AttributeModifier currentArmMod = armorAttr.getModifier(NODE_ARMOR_UUID);
                    if (targetArmor > 0) {
                        if (currentArmMod == null || currentArmMod.getAmount() != targetArmor) {
                            armorAttr.removeModifier(NODE_ARMOR_UUID);
                            armorAttr.addTransientModifier(new AttributeModifier(NODE_ARMOR_UUID, "Node Armor", targetArmor, AttributeModifier.Operation.ADDITION));
                        }
                    } else if (currentArmMod != null) armorAttr.removeModifier(NODE_ARMOR_UUID);
                }

                // APPLY NODE SPEED
                if (speedAttr != null) {
                    double targetSpeed = hasSpd ? 0.15D : 0.0D; 
                    AttributeModifier currentSpdMod = speedAttr.getModifier(NODE_SPEED_UUID);
                    if (targetSpeed > 0) {
                        if (currentSpdMod == null || currentSpdMod.getAmount() != targetSpeed) {
                            speedAttr.removeModifier(NODE_SPEED_UUID);
                            speedAttr.addTransientModifier(new AttributeModifier(NODE_SPEED_UUID, "Node Speed", targetSpeed, AttributeModifier.Operation.MULTIPLY_TOTAL));
                        }
                    } else if (currentSpdMod != null) speedAttr.removeModifier(NODE_SPEED_UUID);
                }

            } 
            else {
                if (kbAttr != null && kbAttr.getModifier(KNOCKBACK_MODIFIER_UUID) != null) kbAttr.removeModifier(KNOCKBACK_MODIFIER_UUID);
                if (armorAttr != null && armorAttr.getModifier(NODE_ARMOR_UUID) != null) armorAttr.removeModifier(NODE_ARMOR_UUID);
                if (speedAttr != null && speedAttr.getModifier(NODE_SPEED_UUID) != null) speedAttr.removeModifier(NODE_SPEED_UUID);
            }

            if (this.armorLevel == 3) {
                if (power >= 2 && player.getHealth() < player.getMaxHealth()) {
                    player.setHealth(player.getMaxHealth()); 
                    tag.putInt("RigPower", power - 2);
                }
            } 
            else if (this.armorLevel == 2) {
                if (player.tickCount % 40 == 0 && player.getHealth() < player.getMaxHealth()) player.heal(1.0F); 
            }
        }
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            @Override
            public @NotNull HumanoidModel<?> getHumanoidArmorModel(LivingEntity entityLiving, ItemStack itemStack, EquipmentSlot armorSlot, HumanoidModel<?> _default) {
                if (armorSlot == EquipmentSlot.HEAD) {
                    if (Custom3DArmorItem.this.armorLevel == 3) {
                        EngineeringLevel3HelmetModel<?> customModel3 = new EngineeringLevel3HelmetModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(EngineeringLevel3HelmetModel.LAYER_LOCATION));
                        customModel3.young = _default.young; customModel3.crouching = _default.crouching; customModel3.riding = _default.riding; customModel3.rightArmPose = _default.rightArmPose; customModel3.leftArmPose = _default.leftArmPose;
                        return customModel3;
                    } else if (Custom3DArmorItem.this.armorLevel == 2) {
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
                    if (Custom3DArmorItem.this.armorLevel == 3) {
                        EngineeringLevel3ChestplateModel<?> customModel3 = new EngineeringLevel3ChestplateModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(EngineeringLevel3ChestplateModel.LAYER_LOCATION));
                        customModel3.young = _default.young; customModel3.crouching = _default.crouching; customModel3.riding = _default.riding; customModel3.rightArmPose = _default.rightArmPose; customModel3.leftArmPose = _default.leftArmPose;
                        return customModel3;
                    } else if (Custom3DArmorItem.this.armorLevel == 2) {
                        Level2ChestplateModel<?> customModel2 = new Level2ChestplateModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(Level2ChestplateModel.LAYER_LOCATION));
                        customModel2.young = _default.young; customModel2.crouching = _default.crouching; customModel2.riding = _default.riding; customModel2.rightArmPose = _default.rightArmPose; customModel2.leftArmPose = _default.leftArmPose;
                        return customModel2;
                    } else {
                        StandardLevel1ChestModel<?> customModel1 = new StandardLevel1ChestModel<>(StandardLevel1ChestModel.createBodyLayer().bakeRoot());
                        customModel1.young = _default.young; customModel1.crouching = _default.crouching; customModel1.riding = _default.riding; customModel1.rightArmPose = _default.rightArmPose; customModel1.leftArmPose = _default.leftArmPose;
                        return customModel1;
                    }
                }

                if (armorSlot == EquipmentSlot.LEGS) {
                    if (Custom3DArmorItem.this.armorLevel == 3) {
                        EngineeringLevel3LeggingsModel<?> customModel3 = new EngineeringLevel3LeggingsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(EngineeringLevel3LeggingsModel.LAYER_LOCATION));
                        customModel3.young = _default.young; customModel3.crouching = _default.crouching; customModel3.riding = _default.riding; customModel3.rightArmPose = _default.rightArmPose; customModel3.leftArmPose = _default.leftArmPose;
                        return customModel3;
                    } else if (Custom3DArmorItem.this.armorLevel == 2) {
                        com.rigmod.client.model.Level2LeggingsModel<?> customModel2 = new com.rigmod.client.model.Level2LeggingsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(com.rigmod.client.model.Level2LeggingsModel.LAYER_LOCATION));
                        customModel2.young = _default.young; customModel2.crouching = _default.crouching; customModel2.riding = _default.riding; customModel2.rightArmPose = _default.rightArmPose; customModel2.leftArmPose = _default.leftArmPose;
                        return customModel2;
                    } else {
                        StandardLevel1LeggingsModel<?> customModel1 = new StandardLevel1LeggingsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(StandardLevel1LeggingsModel.LAYER_LOCATION));
                        customModel1.young = _default.young; customModel1.crouching = _default.crouching; customModel1.riding = _default.riding; customModel1.rightArmPose = _default.rightArmPose; customModel1.leftArmPose = _default.leftArmPose;
                        return customModel1;
                    }
                }

                if (armorSlot == EquipmentSlot.FEET) {
                    if (Custom3DArmorItem.this.armorLevel == 2) {
                        EngineeringLevel2BootsModel<?> customModel2 = new EngineeringLevel2BootsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(EngineeringLevel2BootsModel.LAYER_LOCATION));
                        customModel2.young = _default.young; customModel2.crouching = _default.crouching; customModel2.riding = _default.riding; customModel2.rightArmPose = _default.rightArmPose; customModel2.leftArmPose = _default.leftArmPose;
                        return customModel2;
                    } else if (Custom3DArmorItem.this.armorLevel == 1) {
                        StandardLevel1BootsModel<?> customModel1 = new StandardLevel1BootsModel<>(Minecraft.getInstance().getEntityModels().bakeLayer(StandardLevel1BootsModel.LAYER_LOCATION));
                        customModel1.young = _default.young; customModel1.crouching = _default.crouching; customModel1.riding = _default.riding; customModel1.rightArmPose = _default.rightArmPose; customModel1.leftArmPose = _default.leftArmPose;
                        return customModel1;
                    }
                }
                return _default;
            }
        });
    }
}