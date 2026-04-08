package com.rigmod;

import com.mojang.logging.LogUtils;
import com.rigmod.client.Level2HelmetModel; 
import com.rigmod.client.Level2ChestplateModel; 
import com.rigmod.client.StandardLevel1ChestModel;
import com.rigmod.client.StandardLevel1HelmetModel;
import com.rigmod.client.StandardLevel1LeggingsModel;
import com.rigmod.client.VisionOverlay;
import com.rigmod.client.RadarOverlay; 
import com.rigmod.client.KeyBindings;
import com.rigmod.client.RigWorkbenchModel; // NEW: Workbench Model
import com.rigmod.client.RigWorkbenchRenderer; // NEW: Workbench Renderer
import com.rigmod.item.Custom3DArmorItem;
import com.rigmod.item.ModItems;
import com.rigmod.item.ModCreativeTabs;
import com.rigmod.network.ModMessages;
import com.rigmod.network.packet.CycleVisionModePacket;
import com.rigmod.network.packet.CycleRadarModePacket;
import com.rigmod.block.ModBlocks;
import com.rigmod.blockentity.ModBlockEntities; // NEW: Block Entities

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent; 
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RenderArmEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraft.client.model.HumanoidModel;
import org.slf4j.Logger;

@Mod(RigMod.MODID)
public class RigMod
{
    public static final String MODID = "rigmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RigMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        ModItems.register(modEventBus);
        ModBlocks.register(modEventBus);
        ModBlockEntities.register(modEventBus); // NEW: Registers the Workbench Brains
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        
        MinecraftForge.EVENT_BUS.register(this);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMessages.register(); 
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(StandardLevel1HelmetModel.LAYER_LOCATION, StandardLevel1HelmetModel::createBodyLayer);
            event.registerLayerDefinition(StandardLevel1ChestModel.LAYER_LOCATION, StandardLevel1ChestModel::createBodyLayer);
            event.registerLayerDefinition(StandardLevel1LeggingsModel.LAYER_LOCATION, StandardLevel1LeggingsModel::createBodyLayer);
            event.registerLayerDefinition(Level2HelmetModel.LAYER_LOCATION, Level2HelmetModel::createBodyLayer);
            event.registerLayerDefinition(Level2ChestplateModel.LAYER_LOCATION, Level2ChestplateModel::createBodyLayer);
            
            // NEW: Registers the 3D Workbench Model Layer
            event.registerLayerDefinition(RigWorkbenchModel.LAYER_LOCATION, RigWorkbenchModel::createBodyLayer);
        }

        // NEW: Links your Custom Renderer to your Block Entity
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
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            while (KeyBindings.CYCLE_VISION_KEY.consumeClick()) {
                ModMessages.sendToServer(new CycleVisionModePacket());
            }
            while (KeyBindings.CYCLE_RADAR_KEY.consumeClick()) {
                ModMessages.sendToServer(new CycleRadarModePacket());
            }
        }

        @SubscribeEvent
        public static void onRenderArm(RenderArmEvent event) {
            Player player = event.getPlayer();
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);

            if (chest.getItem() instanceof Custom3DArmorItem armorItem) {
                Minecraft mc = Minecraft.getInstance();

                String textureName = armorItem.getArmorTexture(chest, player, EquipmentSlot.CHEST, null);
                if (textureName == null) return; 
                
                ResourceLocation texture = new ResourceLocation(textureName);
                VertexConsumer vertexConsumer = event.getMultiBufferSource().getBuffer(RenderType.armorCutoutNoCull(texture));

                event.getPoseStack().pushPose();

                HumanoidModel<?> model;
                if (armorItem.getArmorLevel() == 2) {
                    model = new Level2ChestplateModel<>(mc.getEntityModels().bakeLayer(Level2ChestplateModel.LAYER_LOCATION));
                } else {
                    model = new StandardLevel1ChestModel<>(mc.getEntityModels().bakeLayer(StandardLevel1ChestModel.LAYER_LOCATION));
                }

                if (event.getArm() == HumanoidArm.RIGHT) {
                    model.rightArm.xRot = 0.0F;
                    model.rightArm.yRot = 0.0F;
                    model.rightArm.zRot = 0.0F;
                    model.rightArm.render(event.getPoseStack(), vertexConsumer, event.getPackedLight(), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                } 
                else if (event.getArm() == HumanoidArm.LEFT) {
                    model.leftArm.xRot = 0.0F;
                    model.leftArm.yRot = 0.0F;
                    model.leftArm.zRot = 0.0F;
                    model.leftArm.render(event.getPoseStack(), vertexConsumer, event.getPackedLight(), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
                }

                event.getPoseStack().popPose();
            }
        }
    }
}