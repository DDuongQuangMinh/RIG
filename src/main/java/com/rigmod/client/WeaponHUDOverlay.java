package com.rigmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rigmod.RigMod;
import com.rigmod.item.PlasmaCutterItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RigMod.MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class WeaponHUDOverlay {

    // The path to your 2D Gun Silhouette PNG
    private static final ResourceLocation WEAPON_ICON = new ResourceLocation(RigMod.MODID, "textures/gui/plasma_cutter_icon.png");

    @SubscribeEvent
    public static void onRenderHUD(RenderGuiOverlayEvent.Post event) {
        if (event.getOverlay() != VanillaGuiOverlay.HOTBAR.type()) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack stack = player.getMainHandItem();
        if (!(stack.getItem() instanceof PlasmaCutterItem)) return;

        GuiGraphics guiGraphics = event.getGuiGraphics();
        int width = event.getWindow().getGuiScaledWidth();
        int height = event.getWindow().getGuiScaledHeight();

        // 1. Gather Data
        int ammo = stack.getOrCreateTag().getInt("Ammo");
        int reserveAmmo = player.getInventory().countItem(com.rigmod.item.ModItems.PLASMA_ENERGY.get());
        boolean isVertical = stack.getOrCreateTag().getBoolean("IsVertical");

        // Format numbers
        String currentAmmoStr = String.format("%02d", ammo);
        String reserveAmmoStr = String.valueOf(reserveAmmo);
        String fireMode = isVertical ? "[VER]" : "[HOR]";

        // Colors
        int lowColor = 0xFFFF5555; // Red
        int normalColor = 0xFFFFFFFF; // White
        int reserveColor = 0xFFAAAAAA; // Gray
        
        // Turn text red ONLY when ammo is 0
        int currentColor = (ammo == 0) ? lowColor : normalColor;

        // Positioning anchors
        int rightMargin = width - 30;
        int bottomMargin = height - 40;

        // --- 1. RESERVE AMMO ---
        int reserveWidth = mc.font.width(reserveAmmoStr);
        int reserveX = rightMargin - reserveWidth;
        guiGraphics.drawString(mc.font, reserveAmmoStr, reserveX, bottomMargin, reserveColor, true);

        // --- 2. DIVIDER LINE ---
        int dividerX = reserveX - 8;
        guiGraphics.fill(dividerX, bottomMargin - 2, dividerX + 1, bottomMargin + 8, 0xAAFFFFFF);

        // --- 3. CURRENT AMMO ---
        int currentAmmoWidthUnscaled = mc.font.width(currentAmmoStr);
        int currentAmmoX = dividerX - 8 - (currentAmmoWidthUnscaled * 2);

        guiGraphics.pose().pushPose();
        guiGraphics.pose().translate(currentAmmoX, bottomMargin - 8, 0);
        guiGraphics.pose().scale(2.0f, 2.0f, 1.0f);
        guiGraphics.drawString(mc.font, currentAmmoStr, 0, 0, currentColor, true);
        guiGraphics.pose().popPose();

        // --- 4. WEAPON NAME & FIRE MODE ---
        String weaponName = "PLASMA CUTTER " + fireMode;
        int nameWidth = mc.font.width(weaponName);
        int nameX = rightMargin - nameWidth;
        int nameY = bottomMargin - 20;
        guiGraphics.drawString(mc.font, weaponName, nameX, nameY, 0xFF00E5FF, true);

        // --- 5. 2D GUN SILHOUETTE (MOVED TO THE LEFT) ---
        RenderSystem.enableBlend(); 
        RenderSystem.defaultBlendFunc();

        int iconWidth = 32;
        int iconHeight = 16;
        
        // Find the furthest left point of our text block
        int leftmostTextX = Math.min(currentAmmoX, nameX);
        
        // Push the icon to the left of the text block!
        int iconX = leftmostTextX - iconWidth - 8; 
        int iconY = bottomMargin - 14; // Centered beautifully next to the text block
        
        // 🔥 TINT THE IMAGE RED IF AMMO IS 0
        if (ammo == 0) {
            // Applies a red tint to the PNG (R=1.0, G=0.33, B=0.33)
            RenderSystem.setShaderColor(1.0F, 0.33F, 0.33F, 1.0F); 
        } else {
            // Normal untinted image
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); 
        }

        // Draw the image!
        guiGraphics.blit(WEAPON_ICON, iconX, iconY, 0, 0, iconWidth, iconHeight, iconWidth, iconHeight);
        
        // ALWAYS reset the color back to normal, otherwise your crosshair/inventory turns red!
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F); 
        RenderSystem.disableBlend();
    }
}