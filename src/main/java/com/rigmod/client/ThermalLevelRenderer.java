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

    @SubscribeEvent
    public static void onFogColor(ViewportEvent.ComputeFogColor event) {
        if (isWearingThermal()) {
            // Dark Purple/Blue background
            event.setRed(0.1F);
            event.setGreen(0.0F);
            event.setBlue(0.3F); 
        }
    }

    @SubscribeEvent
    public static void onRenderEntity(RenderLivingEvent.Pre<? extends LivingEntity, ?> event) {
        if (isWearingThermal()) {
            LivingEntity entity = event.getEntity();
            float health = entity.getHealth() / entity.getMaxHealth();

            // IRONBOW PALETTE CALCULATION
            // This mimics the real-life heat map colors (Yellow = Hot, Purple = Cold)
            float r, g, b;

            if (health > 0.8f) { // Hottest: Yellow/White
                r = 1.0f; g = 1.0f; b = 0.5f;
            } else if (health > 0.5f) { // Hot: Orange/Red
                r = 1.0f; g = 0.4f; b = 0.0f;
            } else if (health > 0.2f) { // Warm: Magenta/Pink
                r = 0.8f; g = 0.0f; b = 0.8f;
            } else { // Coldest: Blue/Purple
                r = 0.3f; g = 0.0f; b = 1.0f;
            }

            RenderSystem.setShaderColor(r, g, b, 1.0F);
        }
    }

    @SubscribeEvent
    public static void onRenderEntityPost(RenderLivingEvent.Post<? extends LivingEntity, ?> event) {
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    private static boolean isWearingThermal() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return false;
        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        return helmet.getItem() instanceof Custom3DArmorItem && helmet.getOrCreateTag().getInt("VisionMode") == 3;
    }
}