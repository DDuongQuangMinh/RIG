package com.rigmod.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import com.rigmod.client.model.PlasmaCutterModel;
import com.rigmod.item.PlasmaCutterItem;
import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import software.bernie.geckolib.cache.object.BakedGeoModel;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.renderer.GeoItemRenderer;

public class PlasmaCutterRenderer extends GeoItemRenderer<PlasmaCutterItem> {

    private ItemDisplayContext currentContext = ItemDisplayContext.NONE;
    private ItemStack currentStack; 
    
    private float aimProgress = 0.0f; 

    public PlasmaCutterRenderer() {
        super(new PlasmaCutterModel());
    }

    @Override
    public void renderByItem(ItemStack stack, ItemDisplayContext displayContext, PoseStack poseStack, MultiBufferSource bufferSource, int packedLight, int packedOverlay) {
        this.currentContext = displayContext;
        this.currentStack = stack; 
        super.renderByItem(stack, displayContext, poseStack, bufferSource, packedLight, packedOverlay);
    }

    // THE INVENTORY ISOLATION FIX
    @Override
    public long getInstanceId(PlasmaCutterItem animatable) {
        long baseId = super.getInstanceId(animatable);
        
        // If the gun is in the inventory (GUI), on the wall (FIXED), or on the floor (GROUND)
        if (this.currentContext == ItemDisplayContext.GUI || 
            this.currentContext == ItemDisplayContext.FIXED || 
            this.currentContext == ItemDisplayContext.GROUND) {
            
            // We mix in the exact memory address (identityHashCode) of the item. 
            // This gives every single gun in your pockets its own isolated animation brain 
            // so they NEVER fight each other or freeze the gun in your hands!
            return baseId ^ (this.currentContext.ordinal() * 1048576L) ^ System.identityHashCode(this.currentStack);
        }
        
        return baseId; // First and Third person hands keep the normal ID!
    }

    @Override
    public void preRender(PoseStack poseStack, PlasmaCutterItem animatable, BakedGeoModel model, MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isRebind, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        boolean isFirstPerson = currentContext == ItemDisplayContext.FIRST_PERSON_RIGHT_HAND || currentContext == ItemDisplayContext.FIRST_PERSON_LEFT_HAND;

        int armorLevel = 0; 
        Player player = Minecraft.getInstance().player;
        if (player != null && player.getItemBySlot(EquipmentSlot.CHEST).getItem() instanceof Custom3DArmorItem armor) {
            armorLevel = armor.getArmorLevel();
        }

        int finalArmorLevel = armorLevel; 
        
        String[] oldArms = {"leftArm", "rightArm", "armorLeftArm", "armorRightArm"};
        for (String boneName : oldArms) {
            model.getBone(boneName).ifPresent(bone -> bone.setHidden(true));
        }

        model.getBone("leftArm_naked").ifPresent(b -> b.setHidden(!isFirstPerson || finalArmorLevel != 0));
        model.getBone("rightArm_naked").ifPresent(b -> b.setHidden(!isFirstPerson || finalArmorLevel != 0));

        model.getBone("leftArm_level1").ifPresent(b -> b.setHidden(!isFirstPerson || finalArmorLevel != 1));
        model.getBone("rightArm_level1").ifPresent(b -> b.setHidden(!isFirstPerson || finalArmorLevel != 1));

        model.getBone("leftArm_level2").ifPresent(b -> b.setHidden(!isFirstPerson || finalArmorLevel != 2));
        model.getBone("rightArm_level2").ifPresent(b -> b.setHidden(!isFirstPerson || finalArmorLevel != 2));

        model.getBone("leftArm_level3").ifPresent(b -> b.setHidden(!isFirstPerson || finalArmorLevel != 3));
        model.getBone("rightArm_level3").ifPresent(b -> b.setHidden(!isFirstPerson || finalArmorLevel != 3));

        switch (this.currentContext) {
            case FIRST_PERSON_RIGHT_HAND:
            case FIRST_PERSON_LEFT_HAND:
                poseStack.scale(1.1f, 1.1f, 1.1f); 

                boolean isAiming = Minecraft.getInstance().options.keyUse.isDown();
                float targetAim = isAiming ? 1.0f : 0.0f;
                this.aimProgress += (targetAim - this.aimProgress) * 0.15f; 

                float hipX = 0.3f,  hipY = -1.6f, hipZ = -0.4f;
                float aimX = 0.0f,  aimY = -1.6f, aimZ = -0.6f; 

                float currentX = hipX + (aimX - hipX) * this.aimProgress;
                float currentY = hipY + (aimY - hipY) * this.aimProgress;
                float currentZ = hipZ + (aimZ - hipZ) * this.aimProgress;

                poseStack.translate(currentX, currentY, currentZ); 
                poseStack.mulPose(Axis.YP.rotationDegrees(0.0f)); 
                break;
                
            case THIRD_PERSON_RIGHT_HAND:
            case THIRD_PERSON_LEFT_HAND:
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.translate(0.5f, -1.2f, 0.4f);
                poseStack.mulPose(Axis.YP.rotationDegrees(0.0f)); 
                break;
                
            case GUI:
                poseStack.scale(0.8f, 0.8f, 0.8f);
                poseStack.translate(0.1f, -1.0f, 2.0f);
                poseStack.mulPose(Axis.YP.rotationDegrees(90.0f)); 
                poseStack.mulPose(Axis.ZP.rotationDegrees(45.0f)); 
                break;

            case FIXED: // ITEM FRAME
                poseStack.scale(0.8f, 0.8f, 0.8f);
                poseStack.translate(0.6f, -1.4f, 0.6f);
                poseStack.mulPose(Axis.YP.rotationDegrees(-90.0f)); 
                poseStack.mulPose(Axis.ZP.rotationDegrees(15.0f));
                poseStack.mulPose(Axis.XP.rotationDegrees(-15.0f));
                break;
                
            case GROUND: // DROPPED ON THE FLOOR
                poseStack.scale(0.5f, 0.5f, 0.5f);
                poseStack.translate(0.0f, 0.2f, 0.0f);
                break;

            default:
                poseStack.scale(0.5f, 0.5f, 0.5f);
                break;
        }

        super.preRender(poseStack, animatable, model, bufferSource, buffer, isRebind, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public void renderRecursively(PoseStack poseStack, PlasmaCutterItem animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, com.mojang.blaze3d.vertex.VertexConsumer buffer, boolean isRebind, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {

        if (bone.getName().equals("leftArm_naked") || bone.getName().equals("rightArm_naked")) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                ResourceLocation playerSkin = player.getSkinTextureLocation();
                RenderType skinRenderType = RenderType.entityTranslucent(playerSkin);
                com.mojang.blaze3d.vertex.VertexConsumer skinBuffer = bufferSource.getBuffer(skinRenderType);

                super.renderRecursively(poseStack, animatable, bone, skinRenderType, bufferSource, skinBuffer, isRebind, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
                return; 
            }
        }

        com.mojang.blaze3d.vertex.VertexConsumer freshBuffer = bufferSource.getBuffer(renderType);
        super.renderRecursively(poseStack, animatable, bone, renderType, bufferSource, freshBuffer, isRebind, partialTick, packedLight, packedOverlay, red, green, blue, alpha);
    }

    @Override
    public RenderType getRenderType(PlasmaCutterItem animatable, ResourceLocation texture, MultiBufferSource bufferSource, float partialTick) {
        return RenderType.entityTranslucent(texture);
    }
}