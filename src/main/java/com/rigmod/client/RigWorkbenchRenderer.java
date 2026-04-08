package com.rigmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rigmod.RigMod;
import com.rigmod.blockentity.RigWorkbenchBlockEntity;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity; // NEW: Required to satisfy the Blockbench model

public class RigWorkbenchRenderer implements BlockEntityRenderer<RigWorkbenchBlockEntity> {
    
    // FIX: Changed to <Entity> to satisfy the strict EntityModel requirements
    private final RigWorkbenchModel<Entity> model; 
    
    private static final ResourceLocation TEXTURE = new ResourceLocation(RigMod.MODID, "textures/block/rig_workbench.png");

    public RigWorkbenchRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new RigWorkbenchModel<>(context.bakeLayer(RigWorkbenchModel.LAYER_LOCATION));
    }

    @Override
    public void render(RigWorkbenchBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        poseStack.pushPose();
        
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.mulPose(Axis.XP.rotationDegrees(180));

        float progress = Mth.lerp(partialTick, entity.prevAnimationProgress, entity.animationProgress);
        model.animate(progress);

        // FIX: Grab the light level from the air block right above the workbench so it is never trapped in shadow!
        int actualLight = net.minecraft.client.renderer.LevelRenderer.getLightColor(entity.getLevel(), entity.getBlockPos().above());

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutout(TEXTURE));
        
        // FIX: Pass 'actualLight' here instead of 'packedLight'
        model.renderToBuffer(poseStack, vertexConsumer, actualLight, packedOverlay, 1.0F, 1.0F, 1.0F, 1.0F);
        
        poseStack.popPose();
    }
}