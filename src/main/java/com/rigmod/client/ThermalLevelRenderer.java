package com.rigmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.VertexFormat;
import com.rigmod.RigMod;
import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.ViewportEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RigMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ThermalLevelRenderer {

    // 1. SET THE AMBIENT "COLD" WORLD LOOK (Dark Purple/Blue)
    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (isWearingThermal()) {
            event.setRed(0.1F);
            event.setGreen(0.0F);
            event.setBlue(0.3F);
        }
    }

    // 2. THE FULL BODY HEAT OVERRIDE
    @SubscribeEvent
    public static void onRenderEntityPre(RenderLivingEvent.Pre<? extends LivingEntity, ?> event) {
        if (isWearingThermal()) {
            LivingEntity entity = event.getEntity();
            
            // Calculate Heat based on health (1.0 = Healthy/Hot, 0.0 = Dead/Cold)
            float heat = entity.getHealth() / entity.getMaxHealth();

            // Setup the "Solid Color" render state
            RenderSystem.disableBlend();
            RenderSystem.setShader(GameRenderer::getPositionColorShader);
            
            // COLOR MAPPING (Ironbow Palette)
            // This fills the WHOLE body with these colors
            if (heat > 0.75f) {
                // Hottest: Yellow-White
                RenderSystem.setShaderColor(1.0f, 1.0f, 0.4f, 1.0f);
            } else if (heat > 0.4f) {
                // Hot: Vibrant Orange-Red
                RenderSystem.setShaderColor(1.0f, 0.2f, 0.0f, 1.0f);
            } else {
                // Warm/Cooling: Magenta/Purple
                RenderSystem.setShaderColor(0.6f, 0.0f, 0.8f, 1.0f);
            }

            // This tells Minecraft to ignore the lighting of the world (stay bright in caves)
            RenderSystem.enableDepthTest();
        }
    }

    @SubscribeEvent
    public static void onRenderEntityPost(RenderLivingEvent.Post<? extends LivingEntity, ?> event) {
        if (isWearingThermal()) {
            // CRITICAL: Reset the shader color so the rest of the world isn't ruined!
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.enableBlend();
        }
    }

    private static boolean isWearingThermal() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        return helmet.getItem() instanceof Custom3DArmorItem && helmet.getOrCreateTag().getInt("VisionMode") == 3;
    }
}