package com.rigmod.client;

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

public class StandardLevel1HelmetModel<T extends LivingEntity> extends HumanoidModel<T> {
    
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "standard_level_1_helmet"), "main");
    
    private final ModelPart Helmet;
    private final ModelPart front;
    private final ModelPart row1;
    private final ModelPart row2;
    private final ModelPart row3;
    private final ModelPart row4;
    private final ModelPart top;
    private final ModelPart right;
    private final ModelPart left;
    private final ModelPart back;

    private LivingEntity currentEntity;

    public StandardLevel1HelmetModel(ModelPart root) {
        super(root);
        // We fetch your custom helmet from inside the standard Humanoid "head" part
        this.Helmet = root.getChild("head").getChild("Helmet");
        this.front = this.Helmet.getChild("front");
        this.row1 = this.front.getChild("row1");
        this.row2 = this.front.getChild("row2");
        this.row3 = this.front.getChild("row3");
        this.row4 = this.front.getChild("row4");
        this.top = this.Helmet.getChild("top");
        this.right = this.Helmet.getChild("right");
        this.left = this.Helmet.getChild("left");
        this.back = this.Helmet.getChild("back");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. Define standard humanoid parts so the game doesn't crash
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        // 2. Attach YOUR custom model to the head part
        PartDefinition Helmet = head.addOrReplaceChild("Helmet", CubeListBuilder.create(), PartPose.offset(0.0F, -3.0F, 0.0F));

        PartDefinition front = Helmet.addOrReplaceChild("front", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition screen_r1 = front.addOrReplaceChild("screen_r1", CubeListBuilder.create().texOffs(0, 24).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 2.0F, -4.0F, 1.5708F, 0.0F, 0.0F));

        PartDefinition row1 = front.addOrReplaceChild("row1", CubeListBuilder.create().texOffs(32, 18).addBox(-4.0F, -6.0F, -6.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(0, 40).addBox(4.0F, -5.0F, -5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(6, 40).addBox(-5.0F, -5.0F, -5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition row2 = front.addOrReplaceChild("row2", CubeListBuilder.create().texOffs(32, 31).addBox(-4.0F, -3.0F, -6.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(12, 40).addBox(-5.0F, -3.0F, -5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(18, 40).addBox(4.0F, -3.0F, -5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition row3 = front.addOrReplaceChild("row3", CubeListBuilder.create().texOffs(32, 33).addBox(-4.0F, -1.0F, -6.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(24, 40).addBox(4.0F, -1.0F, -5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(30, 40).addBox(-5.0F, -1.0F, -5.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition row4 = front.addOrReplaceChild("row4", CubeListBuilder.create().texOffs(32, 14).addBox(-4.0F, 1.0F, -6.0F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(32, 35).addBox(4.0F, 1.0F, -5.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(38, 35).addBox(-5.0F, 1.0F, -5.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
        .texOffs(36, 40).addBox(-5.0F, 1.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(40, 40).addBox(4.0F, 1.0F, -3.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition top = Helmet.addOrReplaceChild("top", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -5.0F, -4.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(32, 0).addBox(-2.0F, -6.0F, -4.0F, 4.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right = Helmet.addOrReplaceChild("right", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r1 = right.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 8).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.0F, -1.0F, -3.0F, 0.0F, 0.0F, -1.5708F));

        PartDefinition left = Helmet.addOrReplaceChild("left", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r2 = left.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 16).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -1.0F, -3.0F, 0.0F, 0.0F, 1.5708F));

        PartDefinition back = Helmet.addOrReplaceChild("back", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition cube_r3 = back.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(32, 26).addBox(0.0F, -1.0F, 1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-5.0F, -4.0F, 4.0F, 0.0F, 1.5708F, 1.5708F));

        PartDefinition cube_r4 = back.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(32, 21).addBox(0.0F, -1.0F, 1.0F, 4.0F, 1.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(4.0F, -4.0F, 4.0F, 0.0F, 1.5708F, 1.5708F));

        PartDefinition cube_r5 = back.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(32, 9).addBox(-4.0F, -1.0F, 1.0F, 8.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
        .texOffs(0, 32).addBox(-4.0F, 0.0F, -1.0F, 8.0F, 0.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -4.0F, 4.0F, -1.5708F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // Store the entity so we know who is wearing it
        this.currentEntity = entity;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // If the player is in 1st person, don't block their vision with the helmet!
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && this.currentEntity == Minecraft.getInstance().player) {
            return; 
        }
        
        // Render the head (which now contains your Helmet)
        this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}