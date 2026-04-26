package com.rigmod.client;

import com.rigmod.RigMod;
import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.ForgeGui;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RigMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class PowerOverlay {

    @SubscribeEvent
    public static void onRenderHUD(RenderGuiOverlayEvent.Post event) {

        // Render immediately after the vanilla food bar finishes
        if (event.getOverlay() != VanillaGuiOverlay.FOOD_LEVEL.type()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (!(chestplate.getItem() instanceof Custom3DArmorItem armor && armor.getArmorLevel() >= 2)) {
            return;
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();

        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        // =========================
        // POWER DATA
        // =========================
        int power = chestplate.getOrCreateTag().getInt("RigPower");
        boolean hasCap1 = chestplate.getOrCreateTag().getBoolean("RigNode_5");
        boolean hasCap2 = chestplate.getOrCreateTag().getBoolean("RigNode_11");
        int maxPower = 100 + (hasCap1 ? 50 : 0) + (hasCap2 ? 50 : 0);

        float powerPct = Math.max(0f, Math.min(1f, (float) power / maxPower));

        // =========================
        // MEKANISM-STYLE COLORS
        // =========================
        int powerColor = 0xFF00E5FF; // Bright Cyan Energy

        // Warning colors based on power level
        if (powerPct <= 0.20f) {
            // Flash between Red and Dark Red when critical
            powerColor = (player.tickCount % 20 < 10) ? 0xFFFF3333 : 0xFFAA0000;
        } else if (powerPct <= 0.50f) {
            // Solid Orange when half empty
            powerColor = 0xFFFFAA00;
        }

        // =========================
        // LAYOUT
        // =========================
        int barWidth = 81;   // Exactly the width of the 10 food shanks
        int barHeight = 4;   // Slim height

        ForgeGui gui = (ForgeGui) minecraft.gui;

        int x = (width / 2) + 10;
        
        // 🔥 THE FIX: By adding +2 here, the 8-pixel tall casing slides perfectly into 
        // the 9-pixel tall space of the armor row, aligning it perfectly!
        int y = height - gui.rightHeight + 2;

        // =========================
        // RENDER THE SLIM BAR
        // =========================
        
        // 1. Draw the Mekanism Outer Industrial Casing (Light Gray)
        guiGraphics.fill(x - 2, y - 2, x + barWidth + 2, y + barHeight + 2, 0xFF4A4A4A);
        
        // 2. Draw the Inner Shadow Bevel (Almost Black)
        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF111111);

        // 3. Draw the Empty Core Background (Dark Gray)
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFF222222);

        // 4. Draw the Solid Energy Fill
        int fillWidth = (int) (powerPct * barWidth);
        if (fillWidth > 0) {
            guiGraphics.fill(x, y, x + fillWidth, y + barHeight, powerColor);
        }

        // 5. Draw the Internal Gauge Overlay
        // Draws semi-transparent black ticks every 4 pixels across the slim bar
        for (int i = 3; i < barWidth; i += 4) {
            guiGraphics.fill(x + i, y, x + i + 1, y + barHeight, 0x88000000);
        }

        // Reserve 10 pixels of vertical HUD space so air bubbles draw cleanly above it
        gui.rightHeight += 10;
    }
}