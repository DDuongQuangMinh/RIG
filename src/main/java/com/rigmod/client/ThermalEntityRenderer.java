package com.rigmod.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "rigmod", value = Dist.CLIENT)
public class ThermalEntityRenderer {

    @SubscribeEvent
    public static void onRenderLevelStage(RenderLevelStageEvent event) {
        // Render after blocks so the boxes overlay nicely
        if (event.getStage() != RenderLevelStageEvent.Stage.AFTER_TRANSLUCENT_BLOCKS) return;

        Minecraft mc = Minecraft.getInstance();
        Player player = mc.player;
        if (player == null || mc.options.hideGui) return;

        ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
        if (!(helmet.getItem() instanceof Custom3DArmorItem armor) || armor.getArmorLevel() != 3) return;

        // Only draw the red filled entities in Mode 1
        if (helmet.getOrCreateTag().getInt("VisionMode") != 1) return;

        PoseStack poseStack = event.getPoseStack();
        Camera camera = event.getCamera();
        Vec3 cameraPos = camera.getPosition();

        VertexConsumer vertexConsumer = mc.renderBuffers().bufferSource().getBuffer(RenderType.lines());

        // GL States Setup
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        // 🔥 X-RAY MAGIC: Disables depth test so the red fill shows through walls!
        RenderSystem.disableDepthTest(); 

        poseStack.pushPose();
        poseStack.translate(-cameraPos.x, -cameraPos.y, -cameraPos.z);

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (entity instanceof LivingEntity && entity != player) {
                AABB box = entity.getBoundingBox();
                
                // Draw shrinking layered red boxes to create a "solid filled heat core" effect!
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, box, 1.0F, 0.0F, 0.0F, 1.0F); // Outer solid red
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, box.deflate(0.05), 1.0F, 0.0F, 0.0F, 0.6F); // Inner fade
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, box.deflate(0.1), 1.0F, 0.0F, 0.0F, 0.3F); // Inner fade
                LevelRenderer.renderLineBox(poseStack, vertexConsumer, box.deflate(0.15), 1.0F, 0.0F, 0.0F, 0.1F); // Center core
            }
        }

        mc.renderBuffers().bufferSource().endBatch(RenderType.lines());
        poseStack.popPose();
        
        // GL Safety cleanup so we don't break the skybox again!
        RenderSystem.enableDepthTest(); 
        RenderSystem.disableBlend();
    }
}