package com.rigmod.client;

import com.rigmod.RigMod;
import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent; // FIXED: Correct import path!
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RigMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ThermalRenderingManager {
    
    private static boolean isShaderActive = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        if (helmet.getItem() instanceof Custom3DArmorItem) {
            
            int currentMode = helmet.getOrCreateTag().getInt("VisionMode");

            if (currentMode == 3 && !isShaderActive) {
                ResourceLocation shaderLoc = new ResourceLocation(RigMod.MODID, "shaders/post/thermal.json");
                Minecraft.getInstance().gameRenderer.loadEffect(shaderLoc);
                isShaderActive = true;
            
            } else if (currentMode != 3 && isShaderActive) {
                Minecraft.getInstance().gameRenderer.shutdownEffect();
                isShaderActive = false;
            }
        
        } else if (isShaderActive) {
            Minecraft.getInstance().gameRenderer.shutdownEffect();
            isShaderActive = false;
        }
    }
}