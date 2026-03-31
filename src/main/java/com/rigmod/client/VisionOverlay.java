package com.rigmod.client;

import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class VisionOverlay {
    public static final IGuiOverlay HUD_VISION = (gui, guiGraphics, partialTick, width, height) -> {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        ItemStack helmet = mc.player.getItemBySlot(EquipmentSlot.HEAD);
        
        // Ensure they are wearing YOUR custom helmet
        if (helmet.getItem() instanceof Custom3DArmorItem) {
            int mode = helmet.getOrCreateTag().getInt("VisionMode");

            if (mode == 1) {
                guiGraphics.fill(0, 0, width, height, 0x3300FF00); 
            } else if (mode == 2) {
                guiGraphics.fill(0, 0, width, height, 0x22E0FFFF); 
            } else if (mode == 3) {
                guiGraphics.fill(0, 0, width, height, 0x55000022); 
            }
        }
    };
}