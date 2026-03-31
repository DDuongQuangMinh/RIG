package com.rigmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rigmod.RigMod;
import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.client.Minecraft;
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

    // 1. MAKE THE WORLD DARK BLUE (Like the image background)
    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (isWearingThermal()) {
            event.setRed(0.0F);
            event.setGreen(0.0F);
            event.setBlue(0.15F); // Deep Navy Blue base
        }
    }

    @SubscribeEvent
    public static void onFogRender(ViewportEvent.RenderFog event) {
        if (isWearingThermal()) {
            // Pull fog closer to hide the "normal" world details
            event.setNearPlaneDistance(0.0F);
            event.setFarPlaneDistance(20.0F);
            event.setCanceled(true); 
        }
    }

    // 2. MAKE ENTITIES GLOW RED/ORANGE/YELLOW (Real-life Heat signatures)
    @SubscribeEvent
    public static void onRenderEntity(RenderLivingEvent.Pre<? extends LivingEntity, ?> event) {
        if (isWearingThermal()) {
            LivingEntity entity = event.getEntity();
            
            // Set the color based on "Body Heat" 
            // In a real device, larger/healthier mobs look hotter
            float healthPercent = entity.getHealth() / entity.getMaxHealth();
            
            // Render the entity with a solid heat color overlay
            // Hottest (100% health) = White/Red, Damaged = Orange/Yellow
            RenderSystem.setShaderColor(1.0F, healthPercent * 0.5F, 0.0F, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onRenderEntityPost(RenderLivingEvent.Post<? extends LivingEntity, ?> event) {
        // Reset the color so other things don't turn red!
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static boolean isWearingThermal() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        return helmet.getItem() instanceof Custom3DArmorItem && helmet.getOrCreateTag().getInt("VisionMode") == 3;
    }
}