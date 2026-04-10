package com.rigmod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rigmod.RigMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class EngineeringLevel3HelmetModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "engineering_level_3_helmet"), "main");

    private LivingEntity currentEntity;

    public EngineeringLevel3HelmetModel(ModelPart root) {
        super(root);
        
        // Hide standard body parts to prevent floating white boxes
        this.body.visible = false;
        this.rightArm.visible = false;
        this.leftArm.visible = false;
        this.rightLeg.visible = false;
        this.leftLeg.visible = false;
        this.hat.visible = false;
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Empty vanilla parts (Same logic as Level 2)
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

        // ==========================================
        // 100% PURE NEW BLOCKBENCH GEOMETRY
        // ==========================================
        PartDefinition level3 = head.addOrReplaceChild("level3", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -32.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition front = level3.addOrReplaceChild("front", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        PartDefinition front_armor = front.addOrReplaceChild("front_armor", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        front_armor.addOrReplaceChild("row1", CubeListBuilder.create()
                .texOffs(32, 0).addBox(3.2F, -1.0F, 0.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(30, 30).addBox(-4.2F, -1.0F, 0.0F, 1.0F, 2.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(0, 16).addBox(-4.0F, -1.0F, 0.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, -25.0F, -5.0F));

        front_armor.addOrReplaceChild("row2", CubeListBuilder.create()
                .texOffs(0, 26).addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 5).addBox(3.2F, -1.0F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(32, 9).addBox(-4.2F, -1.0F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, -27.0F, -5.0F));

        front_armor.addOrReplaceChild("row3", CubeListBuilder.create()
                .texOffs(18, 26).addBox(-4.0F, -1.0F, 0.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 35).addBox(3.2F, -1.0F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F))
                .texOffs(18, 35).addBox(-4.2F, -1.0F, 0.0F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, -29.0F, -5.0F));

        front_armor.addOrReplaceChild("row4", CubeListBuilder.create()
                .texOffs(0, 28).addBox(-4.0F, -1.0F, -2.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 13).addBox(3.2F, -1.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(26, 35).addBox(-4.2F, -1.0F, -2.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, -31.0F, -3.0F));

        PartDefinition back = level3.addOrReplaceChild("back", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        back.addOrReplaceChild("upper", CubeListBuilder.create()
                .texOffs(18, 16).addBox(-4.0F, -29.0F, 3.2F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(28, 19).addBox(3.2F, -29.0F, 0.2F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 30).addBox(-4.2F, -29.0F, 0.2F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 0.0F, 0.0F));

        back.addOrReplaceChild("lower", CubeListBuilder.create()
                .texOffs(18, 28).addBox(-4.0F, -31.0F, 3.2F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 30).addBox(3.2F, -31.0F, 0.2F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(20, 30).addBox(-4.2F, -31.0F, 0.2F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 0.0F, 0.0F));

        level3.addOrReplaceChild("top", CubeListBuilder.create()
                .texOffs(0, 19).addBox(-2.5F, -32.3F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F))
                .texOffs(14, 19).addBox(1.5F, -32.3F, -3.0F, 1.0F, 1.0F, 6.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.currentEntity = entity;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float r, float g, float b, float a) {
        
        // Hide the armor if the player is in first-person (Same logic as Level 2)
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && this.currentEntity == Minecraft.getInstance().player) {
            return; 
        }

        poseStack.pushPose();
        
        // Matrix Scaling to hide the player's skin without breaking UV mapping
        poseStack.scale(1.02F, 1.02F, 1.02F);
        
        // Only render the head
        this.head.render(poseStack, buffer, packedLight, packedOverlay, r, g, b, a);
        
        poseStack.popPose();
    }
}