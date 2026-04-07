package com.rigmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.rigmod.RigMod;
import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import java.util.List;

public class RadarOverlay {

    // Loads your radar background image
    private static final ResourceLocation RADAR_TEXTURE = new ResourceLocation(RigMod.MODID, "textures/gui/radar_bg.png");

    public static final IGuiOverlay HUD_RADAR = (gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);

        // Check if wearing Level 2 Helmet
        if (helmet.getItem() instanceof Custom3DArmorItem armor && armor.getArmorLevel() == 2) {
            
            int radarMode = helmet.getOrCreateTag().getInt("RadarMode");

            // If Radar is ON (Mode 1, 2, or 3)
            if (radarMode > 0) {
                // Radar UI Dimensions
                int radarSize = 64; 
                int padding = 10;
                
                // Calculate position for Bottom Right corner
                int startX = screenWidth - radarSize - padding;
                int startY = screenHeight - radarSize - padding;

                // 1. Draw the Background Image
                RenderSystem.enableBlend();
                guiGraphics.blit(RADAR_TEXTURE, startX, startY, 0, 0, radarSize, radarSize, radarSize, radarSize);

                // 2. Radar Math Center
                float centerX = startX + (radarSize / 2f);
                float centerY = startY + (radarSize / 2f);
                float maxRadius = radarSize / 2f;
                
                // ✅ CHANGED: Scan range increased to 200 blocks!
                float scanRange = 200.0f; 

                // 3. Scan for entities
                AABB box = player.getBoundingBox().inflate(scanRange);
                List<LivingEntity> entities = player.level().getEntitiesOfClass(LivingEntity.class, box, e -> e != player);

                for (LivingEntity target : entities) {
                    
                    boolean isPlayer = target instanceof Player;
                    if (radarMode == 1 && isPlayer) continue; 
                    if (radarMode == 2 && !isPlayer) continue; 
                    
                    // Calculate distance
                    double dx = target.getX() - player.getX();
                    double dz = target.getZ() - player.getZ();
                    double distance = Math.sqrt(dx * dx + dz * dz);

                    if (distance <= scanRange) {
                        // Math.PI perfectly aligns the math angle with Minecraft's camera Yaw
                        double angle = Math.atan2(dz, dx) - Math.toRadians(player.getYRot()) - Math.PI;

                        // Calculate the X and Y coordinates on the 2D radar screen
                        float blipDistance = (float) (distance / scanRange) * maxRadius;
                        float blipX = centerX + (float) (blipDistance * Math.cos(angle));
                        float blipY = centerY + (float) (blipDistance * Math.sin(angle));

                        // 4. Draw the Blip
                        int color = isPlayer ? 0xFFFF0000 : 0xFFFF6600; 
                        guiGraphics.fill((int)blipX - 1, (int)blipY - 1, (int)blipX + 2, (int)blipY + 2, color);
                    }
                }
            }
        }
    };
}