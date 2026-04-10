package com.rigmod;

import com.mojang.logging.LogUtils;
import com.rigmod.client.Level2HelmetModel;
import com.rigmod.client.Level2ChestplateModel;
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

    // ================= CLIENT MOD EVENTS =================
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
            event.registerLayerDefinition(RigWorkbenchModel.LAYER_LOCATION, RigWorkbenchModel::createBodyLayer);

            // ⚠️ REQUIRED FOR YOUR ARM MODEL
            event.registerLayerDefinition(StandardLevel1ChestModel.LAYER_LOCATION, StandardLevel1ChestModel::createBodyLayer);
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
        }
    }

    // ================= CLIENT FORGE EVENTS =================
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

            if (!(chest.getItem() instanceof Custom3DArmorItem armorItem)) return;

            event.setCanceled(true);

            Minecraft mc = Minecraft.getInstance();

            // ✅ FIXED: no generics conflict
            EntityRenderer<?> renderer =
                    mc.getEntityRenderDispatcher().getRenderer(player);

            if (!(renderer instanceof net.minecraft.client.renderer.entity.player.PlayerRenderer playerRenderer)) return;

            // ✅ FIXED: safe generic cast
            @SuppressWarnings("unchecked")
            HumanoidModel<LivingEntity> playerModel =
                    (HumanoidModel<LivingEntity>) (Object) playerRenderer.getModel();

            String textureName = armorItem.getArmorTexture(chest, player, EquipmentSlot.CHEST, null);
            if (textureName == null) return;

            ResourceLocation texture = ResourceLocation.parse(textureName);
            VertexConsumer buffer = event.getMultiBufferSource()
                    .getBuffer(RenderType.armorCutoutNoCull(texture));

            event.getPoseStack().pushPose();

            StandardLevel1ChestModel<LivingEntity> model =
                    new StandardLevel1ChestModel<>(
                            mc.getEntityModels().bakeLayer(StandardLevel1ChestModel.LAYER_LOCATION)
                    );

            playerModel.copyPropertiesTo(model);

            float partialTick = mc.getFrameTime();

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