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
        
        if (helmet.getItem() instanceof Custom3DArmorItem) {
            int mode = helmet.getOrCreateTag().getInt("VisionMode");

            if (mode == 1) {
                // MILITARY FOREST GREEN (Matching your image)
                // Alpha 0x66 makes it thicker. 
                // Color 004400 is a deep, rich green that reacts well to light.
                guiGraphics.fill(0, 0, width, height, 0x11000000);
                guiGraphics.fill(0, 0, width, height, 0x66004400); 
                
            } else if (mode == 2) {
                // TACTICAL WHITE PHOSPHOR (Silver/Grey look)
                // Using a desaturated blue-grey to match the "Exploit?" side of your image.
                // This makes the world look cold and grey rather than bright blue.
                guiGraphics.fill(0, 0, width, height, 0x11000000);
                guiGraphics.fill(0, 0, width, height, 0x55333333); 
                
            } else if (mode == 3) {
                // DEEP PURPLE THERMAL BASE
                // This provides the dark purple ambient light seen in your image
                guiGraphics.fill(0, 0, width, height, 0x771A0033); 
            }
        }
    };
}