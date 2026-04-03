package com.rigmod.client;

import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class VisionOverlay {

    public static final IGuiOverlay HUD_VISION = (gui, guiGraphics, partialTick, width, height) -> {

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof Custom3DArmorItem)) return;

        int mode = helmet.getOrCreateTag().getInt("VisionMode");

        if (mode == 1) {

            float flicker = 0.995f + (mc.level.getGameTime() % 2) * 0.005f;

            int baseAlpha = (int)(0x66 * flicker);
            int tint = (baseAlpha << 24) | 0x2A3B0F;

            guiGraphics.fill(0, 0, width, height, 0x22000000);
            guiGraphics.fill(0, 0, width, height, 0x110A1205);
            guiGraphics.fill(0, 0, width, height, tint);

            renderVignetteLight(guiGraphics, width, height);
            renderNoiseLight(guiGraphics, width, height, true);
        }

        else if (mode == 2) {

            float flicker = 0.995f + (mc.level.getGameTime() % 2) * 0.005f;

            int baseAlpha = (int)(0x55 * flicker);
            int tint = (baseAlpha << 24) | 0x3A3832;

            guiGraphics.fill(0, 0, width, height, 0x22000000);
            guiGraphics.fill(0, 0, width, height, 0x110A0907);
            guiGraphics.fill(0, 0, width, height, tint);

            renderVignetteLight(guiGraphics, width, height);
            renderNoiseLight(guiGraphics, width, height, false);
        }

        else if (mode == 3) {
            guiGraphics.fill(0, 0, width, height, 0x8815002B);
        }
    };

    // =========================
    // LIGHT VIGNETTE (NO LOOPS)
    // =========================
    private static void renderVignetteLight(net.minecraft.client.gui.GuiGraphics g, int w, int h) {

        int edge = 120;

        // softer edges using multiple layers (cheap trick)
        g.fill(0, 0, w, edge, 0x11000000);
        g.fill(0, h - edge, w, h, 0x11000000);
        g.fill(0, 0, edge, h, 0x11000000);
        g.fill(w - edge, 0, w, h, 0x11000000);

        // inner softer pass
        int inner = 60;
        g.fill(0, 0, w, inner, 0x08000000);
        g.fill(0, h - inner, w, h, 0x08000000);
        g.fill(0, 0, inner, h, 0x08000000);
        g.fill(w - inner, 0, w, h, 0x08000000);
    }

    // =========================
    // LIGHT NOISE (SPARSE)
    // =========================
    private static void renderNoiseLight(net.minecraft.client.gui.GuiGraphics g, int w, int h, boolean greenMode) {

        long time = System.currentTimeMillis() / 40;

        int step = 6; // HUGE performance gain

        for (int x = 0; x < w; x += step) {
            for (int y = 0; y < h; y += step) {

                int value = (int)((x * 13 + y * 7 + time) & 255);

                if (value < 250) continue; // extremely sparse

                int alpha = 10;

                int r, gCol, b;

                if (greenMode) {
                    r = 20;
                    gCol = 255;
                    b = 20;
                } else {
                    r = 180;
                    gCol = 180;
                    b = 180;
                }

                int color = (alpha << 24) | (r << 16) | (gCol << 8) | b;

                g.fill(x, y, x + 1, y + 1, color);
            }
        }
    }
}