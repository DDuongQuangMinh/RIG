package com.rigmod.client;

import com.rigmod.RigMod;
import com.rigmod.item.ModItems;
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
        
        // ✅ THE FIX: Wait for the FOOD bar to finish drawing, not the armor!
        if (event.getOverlay() != VanillaGuiOverlay.FOOD_LEVEL.type()) {
            return;
        }

        Minecraft minecraft = Minecraft.getInstance();
        Player player = minecraft.player;
        if (player == null) return;

        // Check if they are wearing the Level 2 Flight Armor
        ItemStack chestplate = player.getItemBySlot(EquipmentSlot.CHEST);
        if (chestplate.getItem() != ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get()) {
            return; 
        }

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        // Get Power
        int power = chestplate.getOrCreateTag().getInt("RigPower");
        
        // 🔥 NEON CYAN COLOR 
        int neonColor = 0xFF00E5FF; 

        // ==========================================
        // 🔋 THE DYNAMIC HUD POWER BAR (RIGHT SIDE)
        // ==========================================
        int barWidth = 81; // Exactly the width of 10 vanilla food icons
        int barHeight = 5; // Same thickness as the Minecraft EXP bar

        // X: Aligned perfectly with the left edge of the hunger bar
        int x = (width / 2) + 10; 

        // Y: DYNAMIC CALCULATION
        ForgeGui gui = (ForgeGui) minecraft.gui;
        
        // Because we waited for the Food Level to draw, gui.rightHeight already includes the hunger bar!
        // We just draw our bar right above it.
        int y = height - gui.rightHeight - barHeight; 

        // 1. Draw the Black Border
        guiGraphics.fill(x - 1, y - 1, x + barWidth + 1, y + barHeight + 1, 0xFF000000);

        // 2. Draw the Empty Background (Dark Gray)
        guiGraphics.fill(x, y, x + barWidth, y + barHeight, 0xFF333333);

        // 3. Draw the Colored Power Fill
        if (power > 0) {
            int fillWidth = (int) ((power / 100.0f) * barWidth);
            guiGraphics.fill(x, y, x + fillWidth, y + barHeight, neonColor);
        }

        // Push the RIGHT height up so things like air bubbles don't overlap our bar!
        gui.rightHeight += (barHeight + 2);
    }
}