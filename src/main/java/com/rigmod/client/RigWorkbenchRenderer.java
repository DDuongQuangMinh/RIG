package com.rigmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rigmod.RigMod;
import com.rigmod.blockentity.RigWorkbenchBlockEntity;
import com.rigmod.item.ModItems; // NEW: Imported your ModItems!
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;

public class RigWorkbenchRenderer implements BlockEntityRenderer<RigWorkbenchBlockEntity> {
    
    // The texture for your 3D Workbench Model
    private static final ResourceLocation TEXTURE = new ResourceLocation(RigMod.MODID, "textures/block/rig_workbench.png");
    private final RigWorkbenchModel model;

    public RigWorkbenchRenderer(BlockEntityRendererProvider.Context context) {
        this.model = new RigWorkbenchModel(context.bakeLayer(RigWorkbenchModel.LAYER_LOCATION));
    }

    @Override
    public void render(RigWorkbenchBlockEntity entity, float partialTick, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        
        // ==========================================
        // 1. RENDER THE MAIN 3D WORKBENCH ANIMATION
        // ==========================================
        poseStack.pushPose();

        //  GET BLOCK FACING
        var state = entity.getBlockState();
        var facing = state.getValue(com.rigmod.block.RigWorkbenchBlock.FACING);

        // Translate to center
        poseStack.translate(0.5D, 1.5D, 0.5D);

        // 🔥 APPLY ROTATION (THIS FIXES EVERYTHING)
        poseStack.mulPose(Axis.YP.rotationDegrees(-facing.toYRot()));

        // Keep your original flip
        poseStack.scale(-1.0F, -1.0F, 1.0F);

        float progress = net.minecraft.util.Mth.lerp(partialTick, entity.prevAnimationProgress, entity.animationProgress);

        this.model.animate(progress);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);

        poseStack.popPose();

        // ==========================================
        // 2. RENDER THE MULTIPLAYER HOLOGRAM
        // ==========================================
        if (entity.displayMode != -1 && progress > 0.8f) {
            poseStack.pushPose();

            double hoverOffset = Math.sin((entity.getLevel().getGameTime() + partialTick) / 10.0D) * 0.05D;
            poseStack.translate(0.5D, 1.3D + hoverOffset, 0.5D);

            float rotationTime = (entity.getLevel().getGameTime() + partialTick) * 3.0F;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationTime));

            poseStack.scale(0.8F, 0.8F, 0.8F);

            // THE FIX: The hologram now uses your custom items instead of vanilla blocks!
            ItemStack hologramItem = ItemStack.EMPTY;
            if (entity.displayMode == 0) {
                hologramItem = new ItemStack(ModItems.STANDARD_LEVEL_1_HELMET.get()); 
            } else if (entity.displayMode == 1) {
                hologramItem = new ItemStack(ModItems.BATTERY_LEVEL_1.get()); 
            }

            if (!hologramItem.isEmpty()) {
                Minecraft.getInstance().getItemRenderer().renderStatic(
                        hologramItem, 
                        ItemDisplayContext.GROUND, 
                        15728880,
                        OverlayTexture.NO_OVERLAY, 
                        poseStack, 
                        bufferSource, 
                        entity.getLevel(), 
                        0);
            }

            poseStack.popPose();
        }
    }
}