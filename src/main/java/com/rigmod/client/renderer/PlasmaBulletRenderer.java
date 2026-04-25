package com.rigmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rigmod.entity.PlasmaBulletEntity;
import com.rigmod.item.ModItems; 
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.util.Mth;


public class PlasmaBulletRenderer extends EntityRenderer<PlasmaBulletEntity> {

    public PlasmaBulletRenderer(EntityRendererProvider.Context context) {
        super(context);
    }

    @Override
    public void render(PlasmaBulletEntity entity, float entityYaw, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight) {
        poseStack.pushPose();

        // 1. Calculate trajectory exactly like a vanilla arrow (Flight path is now on the +X axis)
        poseStack.mulPose(Axis.YP.rotationDegrees(Mth.lerp(partialTick, entity.yRotO, entity.getYRot()) - 90.0F));
        poseStack.mulPose(Axis.ZP.rotationDegrees(Mth.lerp(partialTick, entity.xRotO, entity.getXRot())));

        // 2. 🔥 THE FIX: Spin the bullet vertically FIRST, while the X-axis still matches the flight path!
        if (entity.isVertical()) {
            poseStack.mulPose(Axis.XP.rotationDegrees(90.0F));
        }

        // 3. Re-align our Blockbench model (which faces +Z) to point forward (+X)
        poseStack.mulPose(Axis.YP.rotationDegrees(-90.0F));

        // 4. Center the Blockbench model so it rotates perfectly on its own axis instead of swinging!
        // (Note: If you already moved the model to 0,0,0 inside Blockbench itself, you can delete this line)
        poseStack.translate(-7.0f / 16.0f, -14.125f / 16.0f, -11.5f / 16.0f);

        Minecraft.getInstance().getItemRenderer().renderStatic(
                new ItemStack(ModItems.PLASMA_BULLET_MODEL.get()),
                ItemDisplayContext.NONE,
                packedLight,
                OverlayTexture.NO_OVERLAY,
                poseStack,
                bufferSource,
                entity.level(),
                entity.getId()
        );

        poseStack.popPose();
        super.render(entity, entityYaw, partialTick, poseStack, bufferSource, packedLight);
    }

    @Override
    public ResourceLocation getTextureLocation(PlasmaBulletEntity entity) {
        return null; 
    }
}