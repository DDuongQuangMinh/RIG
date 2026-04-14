package com.rigmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorItem; 
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.Random;

public class VisionOverlay {

    private static final Random RANDOM = new Random();
    private static double lastTemp = 20.0;

    public static final IGuiOverlay HUD_VISION = (gui, guiGraphics, partialTick, width, height) -> {

        Minecraft mc = Minecraft.getInstance();
        LocalPlayer player = mc.player;

        if (player == null || mc.options.hideGui) return;

        // 🔥 THE FIX: ALL HELMETS NOW REQUIRE A POWERED CHESTPLATE
        ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chest.getItem() instanceof Custom3DArmorItem chestArmor && chestArmor.getType() == ArmorItem.Type.CHESTPLATE) {
            if (chest.getOrCreateTag().getInt("RigPower") <= 0) {
                return; // ⛔ ABORT: BATTERY DEAD = NORMAL SCREEN ⛔
            }
        } else {
            return; // ⛔ ABORT: NO POWERED SUIT EQUIPPED ⛔
        }

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof Custom3DArmorItem armor)) return;

        int mode = helmet.getOrCreateTag().getInt("VisionMode");

        // --- LEVEL 1 HELMET LOGIC ---
        if (armor.getArmorLevel() == 1) {
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
        } 
        
        // --- LEVEL 2 HELMET LOGIC ---
        else if (armor.getArmorLevel() == 2) {
            if (mode > 0) {
                guiGraphics.fill(0, 0, width, height, 0x770D001F);
                renderDigitalScanner(guiGraphics, width, height, mc.level.getGameTime());
                renderNoiseLight(guiGraphics, width, height, false);
            }
        }
        
        // --- LEVEL 3 HELMET LOGIC ---
        else if (armor.getArmorLevel() == 3) {
            
            // 🔥 REVERTED TO MODE 0: Scanner
            if (mode == 0) {
                RenderSystem.enableBlend();
                guiGraphics.fill(0, 0, width, height, 0x770D001F);
                renderDigitalScanner(guiGraphics, width, height, mc.level.getGameTime());
                renderNoiseLight(guiGraphics, width, height, false);
            }
            
            // 🔥 REVERTED TO MODE 1: Thermal
            else if (mode == 1) {
                RenderSystem.enableBlend();
                guiGraphics.fill(0, 0, width, height, 0x33FF5500); 
                
                renderDigitalScanner(guiGraphics, width, height, mc.level.getGameTime());
                renderNoiseLight(guiGraphics, width, height, false);

                int cx = width / 2;
                int cy = height / 2;

                guiGraphics.fill(cx - 16, cy - 2, cx + 16, cy + 2, 0xFF000000);
                guiGraphics.fill(cx - 2, cy - 16, cx + 2, cy + 16, 0xFF000000);
                guiGraphics.fill(cx - 15, cy - 1, cx + 15, cy + 1, 0xFFFFFFFF);
                guiGraphics.fill(cx - 1, cy - 15, cx + 1, cy + 15, 0xFFFFFFFF);
                guiGraphics.fill(cx - 3, cy - 3, cx + 3, cy + 3, 0x88FFAA00);

                double targetTemp = 22.5;

                if (mc.hitResult != null && mc.hitResult.getType() == HitResult.Type.ENTITY) {
                    targetTemp = 37.0 + (RANDOM.nextDouble() * 2.0); 
                } else {
                    HitResult blockHit = player.pick(100.0D, 0.0F, false);
                    if (blockHit != null && blockHit.getType() == HitResult.Type.BLOCK) {
                        BlockPos pos = ((BlockHitResult) blockHit).getBlockPos();
                        BlockState state = mc.level.getBlockState(pos);

                        if (state.is(Blocks.LAVA)) targetTemp = 145.0 + RANDOM.nextDouble() * 15; 
                        else if (state.is(Blocks.WATER)) targetTemp = 12.0 + RANDOM.nextDouble() * 3;
                        else if (state.is(Blocks.FIRE) || state.is(Blocks.CAMPFIRE) || state.is(Blocks.MAGMA_BLOCK)) targetTemp = 95.0 + RANDOM.nextDouble() * 20;
                        else if (state.is(Blocks.SNOW) || state.is(Blocks.SNOW_BLOCK) || state.is(Blocks.ICE)) targetTemp = -5.0 + RANDOM.nextDouble() * 2;
                        else {
                            float biomeTemp = mc.level.getBiome(pos).value().getBaseTemperature();
                            targetTemp = (biomeTemp * 25.0) + (mc.level.isDay() ? 5.0 : -5.0);
                        }
                    }
                }

                lastTemp += (targetTemp - lastTemp) * 0.1;
                String tempText = String.format("%.1f \u00B0C", lastTemp);

                PoseStack pose = guiGraphics.pose();
                pose.pushPose();
                pose.scale(1.5F, 1.5F, 1.5F); 

                int textX = 10;
                int textY = 10;
                int boxWidth = mc.font.width(tempText) + 8;

                guiGraphics.fill(textX - 4, textY - 4, textX + boxWidth - 4, textY + mc.font.lineHeight + 4, 0xAA000011);
                guiGraphics.drawString(mc.font, tempText, textX, textY, 0xFFFFFFFF, false);

                pose.popPose();
            }
        }
    };

    private static void renderDigitalScanner(GuiGraphics g, int w, int h, long gameTime) {
        int scanPos = (int) ((gameTime * 4) % h);
        g.fill(0, scanPos, w, scanPos + 1, 0x3300FFCC);
        int gridSize = 40;
        for (int i = 0; i < w; i += gridSize) {
            g.fill(i, 0, i + 1, h, 0x1100FFCC);
        }
        for (int j = 0; j < h; j += gridSize) {
            g.fill(0, j, w, j + 1, 0x1100FFCC);
        }
    }

    private static void renderVignetteLight(GuiGraphics g, int w, int h) {
        int edge = 120;
        g.fill(0, 0, w, edge, 0x11000000);
        g.fill(0, h - edge, w, h, 0x11000000);
        g.fill(0, 0, edge, h, 0x11000000);
        g.fill(w - edge, 0, w, h, 0x11000000);
    }

    private static void renderNoiseLight(GuiGraphics g, int w, int h, boolean greenMode) {
        long time = System.currentTimeMillis() / 40;
        int step = 6;
        for (int x = 0; x < w; x += step) {
            for (int y = 0; y < h; y += step) {
                int value = (int)((x * 13 + y * 7 + time) & 255);
                if (value < 250) continue;
                int alpha = 10;
                int r, gCol, b;
                if (greenMode) {
                    r = 20; gCol = 255; b = 20;
                } else {
                    r = 180; gCol = 180; b = 180;
                }
                int color = (alpha << 24) | (r << 16) | (gCol << 8) | b;
                g.fill(x, y, x + 1, y + 1, color);
            }
        }
    }
}