package com.rigmod;

import com.mojang.logging.LogUtils;
import com.rigmod.client.StandardLevel1HelmetModel;
import com.rigmod.client.StandardLevel1LeggingsModel;
import com.rigmod.client.VisionOverlay;
import com.rigmod.client.KeyBindings;
import com.rigmod.client.StandardLevel1ChestModel;
import com.rigmod.item.Custom3DArmorItem;
import com.rigmod.item.ModItems;
import com.rigmod.item.ModCreativeTabs;
import com.rigmod.network.ModMessages;
import com.rigmod.network.packet.CycleVisionModePacket;

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
import org.slf4j.Logger;

@Mod(RigMod.MODID)
public class RigMod
{
    public static final String MODID = "rigmod";
    private static final Logger LOGGER = LogUtils.getLogger();

    public RigMod() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        
        ModItems.register(modEventBus); 
        ModCreativeTabs.register(modEventBus);

        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::addCreative);
        
        MinecraftForge.EVENT_BUS.register(this);
    }

    // MERGED: Network registration goes inside this single commonSetup method
    private void commonSetup(final FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            ModMessages.register(); 
        });
    }

    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        // Left empty intentionally so your item doesn't duplicate into vanilla tabs
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Code to run when the server starts
    }

    // MERGED: All MOD bus client events (Overlays, Layers, Keybinds) live in this one class
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event) {
            // Client setup code
        }

        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(StandardLevel1HelmetModel.LAYER_LOCATION, StandardLevel1HelmetModel::createBodyLayer);
            
            event.registerLayerDefinition(StandardLevel1ChestModel.LAYER_LOCATION, StandardLevel1ChestModel::createBodyLayer);

            event.registerLayerDefinition(StandardLevel1LeggingsModel.LAYER_LOCATION, StandardLevel1LeggingsModel::createBodyLayer);
        }

        @SubscribeEvent
        public static void registerOverlays(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("vision_overlay", VisionOverlay.HUD_VISION);
        }

        @SubscribeEvent
        public static void onKeyRegister(RegisterKeyMappingsEvent event) {
            event.register(KeyBindings.CYCLE_VISION_KEY);
        }
    }

    // This stays separate because it listens to the FORGE bus, not the MOD bus
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientForgeEvents {
        
        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            while (KeyBindings.CYCLE_VISION_KEY.consumeClick()) {
                ModMessages.sendToServer(new CycleVisionModePacket());
            }
        }

        // --- FIXED: FIRST PERSON ARM RENDERER ---
        @SubscribeEvent
        public static void onRenderArm(RenderArmEvent event) {
            Player player = event.getPlayer();
            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);

            // Check if the right arm is on screen AND the player is wearing our RIG suit
            if (event.getArm() == HumanoidArm.RIGHT && chest.getItem() instanceof Custom3DArmorItem armorItem) {
                Minecraft mc = Minecraft.getInstance();

                // 1. Get the correct chestplate texture (Bronze or White)
                String textureName = armorItem.getArmorTexture(chest, player, EquipmentSlot.CHEST, null);
                if (textureName == null) return; 
                
                ResourceLocation texture = new ResourceLocation(textureName);

                // 2. Fetch our custom model
                StandardLevel1ChestModel<?> model = new StandardLevel1ChestModel<>(mc.getEntityModels().bakeLayer(StandardLevel1ChestModel.LAYER_LOCATION));

                // 3. Get the renderer ready
                VertexConsumer vertexConsumer = event.getMultiBufferSource().getBuffer(RenderType.armorCutoutNoCull(texture));

                event.getPoseStack().pushPose();

                // 4. SAFETY RESET: We must force the rotation to 0 so the 3rd-person walking animation 
                // doesn't make your first-person tablet swing wildly off-screen!
                model.rightArm.xRot = 0.0F;
                model.rightArm.yRot = 0.0F;
                model.rightArm.zRot = 0.0F;

                // (Notice I completely deleted the event.getPoseStack().translate(...) line here!
                // The event matrix is already perfectly aligned with the vanilla arm.)

                // 5. Render JUST the right arm part (the tablet)
                model.rightArm.render(event.getPoseStack(), vertexConsumer, event.getPackedLight(), OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

                event.getPoseStack().popPose();
            }
        }
    }
}