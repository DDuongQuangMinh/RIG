package com.rigmod;

import com.mojang.logging.LogUtils;
import com.rigmod.client.Level2HelmetModel;
import com.rigmod.client.Level2ChestplateModel;
import com.rigmod.client.model.EngineeringLevel3HelmetModel;
import com.rigmod.client.model.EngineeringLevel3ChestplateModel;
import com.rigmod.client.model.StandardLevel1ChestModel;
import com.rigmod.client.StandardLevel1HelmetModel;
import com.rigmod.client.StandardLevel1LeggingsModel;
import com.rigmod.client.VisionOverlay;
import com.rigmod.client.RadarOverlay;
import com.rigmod.client.KeyBindings;
import com.rigmod.client.RigWorkbenchModel;
import com.rigmod.client.RigWorkbenchRenderer;
import com.rigmod.client.RigWorkbenchScreen;
import com.rigmod.item.Custom3DArmorItem;
import com.rigmod.item.ModItems;
import com.rigmod.item.ModCreativeTabs;
import com.rigmod.network.ModMessages;
import com.rigmod.network.packet.CycleVisionModePacket;
import com.rigmod.network.packet.CycleRadarModePacket;
import com.rigmod.block.ModBlocks;
import com.rigmod.blockentity.ModBlockEntities;
import com.rigmod.menu.ModMenuTypes;
import com.rigmod.event.ArmorFlightHandler; 

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.client.event.RenderPlayerEvent; 
import net.minecraftforge.client.event.ViewportEvent; 
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent; 
import net.minecraftforge.event.entity.living.LivingEquipmentChangeEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import org.slf4j.Logger;

@Mod(RigMod.MODID)
public class RigMod {

    public static final String MODID = "rigmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RigMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus);
        ModMenuTypes.register(modEventBus);
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(ModMessages::register);
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {}

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class CommonForgeEvents {
        
        @SubscribeEvent
        public static void onPlayerHurt(LivingHurtEvent event) {
            if (event.getEntity() instanceof Player player) {
                ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
                
                if (chest.getItem() instanceof Custom3DArmorItem armor && armor.getArmorLevel() >= 2) {
                    int power = chest.getOrCreateTag().getInt("RigPower");
                    
                    if (event.getSource().is(net.minecraft.tags.DamageTypeTags.IS_FALL)) {
                        if (power > 0) {
                            event.setCanceled(true);
                            return; 
                        }
                    }

                    if (power > 0) {
                        event.setCanceled(true); 
                        
                        int hits = chest.getOrCreateTag().getInt("AbsorbedHits") + 1;
                        if (hits >= 20) {
                            chest.getOrCreateTag().putInt("RigPower", power - 1); 
                            chest.getOrCreateTag().putInt("AbsorbedHits", 0); 
                        } else {
                            chest.getOrCreateTag().putInt("AbsorbedHits", hits);
                        }
                        
                        if (!player.level().isClientSide()) {
                            player.level().playSound(null, player.blockPosition(), net.minecraft.sounds.SoundEvents.RESPAWN_ANCHOR_DEPLETE.get(), net.minecraft.sounds.SoundSource.PLAYERS, 1.0F, 2.0F);
                        }
                    }
                }
            }
        }

        @SubscribeEvent
        public static void onEquipmentChange(LivingEquipmentChangeEvent event) {
            if (event.getEntity() instanceof Player player) {
                if (event.getSlot() == EquipmentSlot.CHEST) {
                    ItemStack newItem = event.getTo();
                    
                    if (!(newItem.getItem() instanceof Custom3DArmorItem)) {
                        AttributeInstance health = player.getAttribute(Attributes.MAX_HEALTH);
                        if (health != null) health.removeModifier(Custom3DArmorItem.HEALTH_MODIFIER_UUID);
                        
                        AttributeInstance kb = player.getAttribute(Attributes.KNOCKBACK_RESISTANCE);
                        if (kb != null) kb.removeModifier(Custom3DArmorItem.KNOCKBACK_MODIFIER_UUID);
                        
                        if (player.getHealth() > player.getMaxHealth()) {
                            player.setHealth(player.getMaxHealth());
                        }
                    }
                }
            }
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            event.enqueueWork(() -> {
                net.minecraft.client.gui.screens.MenuScreens.register(
                        ModMenuTypes.RIG_WORKBENCH_MENU.get(),
                        RigWorkbenchScreen::new
                );
            });
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(StandardLevel1HelmetModel.LAYER_LOCATION, StandardLevel1HelmetModel::createBodyLayer);
            event.registerLayerDefinition(StandardLevel1LeggingsModel.LAYER_LOCATION, StandardLevel1LeggingsModel::createBodyLayer);
            event.registerLayerDefinition(Level2HelmetModel.LAYER_LOCATION, Level2HelmetModel::createBodyLayer);
            event.registerLayerDefinition(Level2ChestplateModel.LAYER_LOCATION, Level2ChestplateModel::createBodyLayer);
            event.registerLayerDefinition(EngineeringLevel3HelmetModel.LAYER_LOCATION, EngineeringLevel3HelmetModel::createBodyLayer);
            event.registerLayerDefinition(EngineeringLevel3ChestplateModel.LAYER_LOCATION, EngineeringLevel3ChestplateModel::createBodyLayer);
            event.registerLayerDefinition(RigWorkbenchModel.LAYER_LOCATION, RigWorkbenchModel::createBodyLayer);
            event.registerLayerDefinition(StandardLevel1ChestModel.LAYER_LOCATION, StandardLevel1ChestModel::createBodyLayer);
            event.registerLayerDefinition(com.rigmod.client.model.Level2LeggingsModel.LAYER_LOCATION, com.rigmod.client.model.Level2LeggingsModel::createBodyLayer);
            event.registerLayerDefinition(com.rigmod.client.model.StandardLevel1BootsModel.LAYER_LOCATION, com.rigmod.client.model.StandardLevel1BootsModel::createBodyLayer);
            event.registerLayerDefinition(com.rigmod.client.model.EngineeringLevel3LeggingsModel.LAYER_LOCATION, com.rigmod.client.model.EngineeringLevel3LeggingsModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerRenderers(EntityRenderersEvent.RegisterRenderers event) {
            event.registerBlockEntityRenderer(ModBlockEntities.RIG_WORKBENCH_BE.get(), RigWorkbenchRenderer::new);
        }

        @SubscribeEvent
        public static void registerOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("vision_overlay", VisionOverlay.HUD_VISION);
            event.registerAboveAll("radar_overlay", RadarOverlay.HUD_RADAR);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.CYCLE_VISION_KEY);
            event.register(KeyBindings.CYCLE_RADAR_KEY);
            event.register(KeyBindings.TOGGLE_STABLE_KEY); 
            event.register(KeyBindings.ROTATE_LEFT_KEY); 
            event.register(KeyBindings.ROTATE_RIGHT_KEY); 
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {

        @SubscribeEvent
        public static void onCameraSetup(ViewportEvent.ComputeCameraAngles event) {
            if (ArmorFlightHandler.currentRoll != 0.0F && event.getCamera().getEntity() instanceof Player) {
                event.setRoll(event.getRoll() + ArmorFlightHandler.currentRoll);
            }
        }

        // 🔥 FIX: Completely deleted the forward pitch lean!
        @SubscribeEvent
        public static void onRenderPlayerPre(RenderPlayerEvent.Pre event) {
            Player player = event.getEntity();
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);

            if (chest.getItem() instanceof Custom3DArmorItem armor && armor.getArmorLevel() >= 2) {
                if (!player.onGround() && chest.getOrCreateTag().getInt("RigPower") > 0) {
                    
                    player.yBodyRot = player.yHeadRot;
                    player.yBodyRotO = player.yHeadRotO;
                    
                    event.getPoseStack().pushPose();
                    event.getPoseStack().translate(0.0D, 1.0D, 0.0D); 

                    // Only apply Z-Axis Roll
                    if (ArmorFlightHandler.currentRoll != 0.0F) {
                        event.getPoseStack().mulPose(new org.joml.Quaternionf().fromAxisAngleDeg(new org.joml.Vector3f(0.0f, 0.0f, 1.0f), ArmorFlightHandler.currentRoll));
                    }

                    event.getPoseStack().translate(0.0D, -1.0D, 0.0D);
                }
            }
        }

        @SubscribeEvent
        public static void onRenderPlayerPost(RenderPlayerEvent.Post event) {
            Player player = event.getEntity();
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (chest.getItem() instanceof Custom3DArmorItem armor && armor.getArmorLevel() >= 2) {
                if (!player.onGround() && chest.getOrCreateTag().getInt("RigPower") > 0) {
                    event.getPoseStack().popPose();
                }
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            while (KeyBindings.CYCLE_VISION_KEY.consumeClick()) {
                ModMessages.sendToServer(new CycleVisionModePacket());
            }
            while (KeyBindings.CYCLE_RADAR_KEY.consumeClick()) {
                ModMessages.sendToServer(new CycleRadarModePacket());
            }
            
            while (KeyBindings.TOGGLE_STABLE_KEY.consumeClick()) {
                ArmorFlightHandler.isStableMode = !ArmorFlightHandler.isStableMode;
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    String state = ArmorFlightHandler.isStableMode ? "§aON" : "§cOFF";
                    mc.player.displayClientMessage(net.minecraft.network.chat.Component.literal("§b[SUIT] Flight Stabilizer: " + state), true);
                }
            }

            Minecraft mc = Minecraft.getInstance();
            if (mc.player != null) {
                if (ArmorFlightHandler.isFlying) {
                    float rollSpeed = 3.5F; 
                    if (KeyBindings.ROTATE_LEFT_KEY.isDown()) {
                        ArmorFlightHandler.currentRoll += rollSpeed; 
                    }
                    if (KeyBindings.ROTATE_RIGHT_KEY.isDown()) {
                        ArmorFlightHandler.currentRoll -= rollSpeed; 
                    }
                } else {
                    ArmorFlightHandler.currentRoll *= 0.8F; 
                    if (Math.abs(ArmorFlightHandler.currentRoll) < 0.1F) {
                        ArmorFlightHandler.currentRoll = 0.0F;
                    }
                }
            }
        }

        @SuppressWarnings("unchecked")
        @SubscribeEvent
        public static void onRenderArm(RenderArmEvent event) {
            Player player = event.getPlayer();
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);

            if (!(chest.getItem() instanceof Custom3DArmorItem armorItem)) return;

            event.setCanceled(true);

            Minecraft mc = Minecraft.getInstance();

            EntityRenderer<?> renderer = mc.getEntityRenderDispatcher().getRenderer(player);

            if (!(renderer instanceof net.minecraft.client.renderer.entity.player.PlayerRenderer playerRenderer)) return;

            HumanoidModel<LivingEntity> playerModel =
                    (HumanoidModel<LivingEntity>) (Object) playerRenderer.getModel();

            String textureName = armorItem.getArmorTexture(chest, player, EquipmentSlot.CHEST, null);
            if (textureName == null) return;

            ResourceLocation texture = ResourceLocation.parse(textureName);
            VertexConsumer buffer = event.getMultiBufferSource()
                    .getBuffer(RenderType.armorCutoutNoCull(texture));

            event.getPoseStack().pushPose();

            float partialTick = mc.getFrameTime();

            HumanoidModel<LivingEntity> model;
            
            if (armorItem.getArmorLevel() == 3) {
                model = (HumanoidModel<LivingEntity>) (Object) new EngineeringLevel3ChestplateModel<>(
                        mc.getEntityModels().bakeLayer(EngineeringLevel3ChestplateModel.LAYER_LOCATION)
                );
            } else if (armorItem.getArmorLevel() == 2) {
                model = (HumanoidModel<LivingEntity>) (Object) new Level2ChestplateModel<>(
                        mc.getEntityModels().bakeLayer(Level2ChestplateModel.LAYER_LOCATION)
                );
            } else {
                model = (HumanoidModel<LivingEntity>) (Object) new StandardLevel1ChestModel<>(
                        mc.getEntityModels().bakeLayer(StandardLevel1ChestModel.LAYER_LOCATION)
                );
            }

            playerModel.copyPropertiesTo(model);
            model.setupAnim(player, 0, 0, partialTick, player.getYRot(), player.getXRot());

            if (event.getArm() == HumanoidArm.RIGHT) {
                model.rightArm.copyFrom(playerModel.rightArm);
                model.rightArm.render(event.getPoseStack(), buffer,
                        event.getPackedLight(), OverlayTexture.NO_OVERLAY,
                        1, 1, 1, 1);
            } else {
                model.leftArm.copyFrom(playerModel.leftArm);
                model.leftArm.render(event.getPoseStack(), buffer,
                        event.getPackedLight(), OverlayTexture.NO_OVERLAY,
                        1, 1, 1, 1);
            }

            event.getPoseStack().popPose();
        }
    }
}