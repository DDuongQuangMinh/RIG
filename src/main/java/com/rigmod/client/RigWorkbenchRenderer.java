package com.rigmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import com.rigmod.RigMod;
import com.rigmod.blockentity.RigWorkbenchBlockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

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
        
        // Translate to the center of the block (Minecraft standard)
        poseStack.translate(0.5D, 1.5D, 0.5D);
        poseStack.scale(-1.0F, -1.0F, 1.0F); // Flip it right-side up!

        float progress = net.minecraft.util.Mth.lerp(partialTick, entity.prevAnimationProgress, entity.animationProgress);
        
        // Tells the model how far to open the arms/screen
        this.model.animate(progress);

        VertexConsumer vertexConsumer = bufferSource.getBuffer(RenderType.entityCutoutNoCull(TEXTURE));
        this.model.renderToBuffer(poseStack, vertexConsumer, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
        poseStack.popPose();

        // ==========================================
        // 2. RENDER THE MULTIPLAYER HOLOGRAM
        // ==========================================
        // Only show the hologram if a player has the UI open AND the arms are mostly folded out!
        if (entity.displayMode != -1 && progress > 0.8f) {
            poseStack.pushPose();
            
            // Move the hologram to float directly above the center of the block
            // The Math.sin logic makes it gently hover up and down!
            double hoverOffset = Math.sin((entity.getLevel().getGameTime() + partialTick) / 10.0D) * 0.05D;
            poseStack.translate(0.5D, 1.3D + hoverOffset, 0.5D);
            
            // Make the hologram spin slowly and smoothly
            float rotationTime = (entity.getLevel().getGameTime() + partialTick) * 3.0F;
            poseStack.mulPose(Axis.YP.rotationDegrees(rotationTime));
            
            // Scale it up
            poseStack.scale(0.8F, 0.8F, 0.8F);

            // Decide what icon to show based on the mode the player is currently in!
            ItemStack hologramItem = ItemStack.EMPTY;
            if (entity.displayMode == 0) {
                hologramItem = new ItemStack(Items.CRAFTING_TABLE); 
            } else if (entity.displayMode == 1) {
                hologramItem = new ItemStack(Items.REDSTONE); 
            }

            // Draw the item glowing at maximum brightness (15728880), bypassing regular lighting!
            if (!hologramItem.isEmpty()) {
                Minecraft.getInstance().getItemRenderer().renderStatic(
                        hologramItem, 
                        ItemDisplayContext.GROUND, 
                        15728880, // Max Light
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