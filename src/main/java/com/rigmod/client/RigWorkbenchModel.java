package com.rigmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rigmod.RigMod;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;

public class RigWorkbenchModel<T extends Entity> extends EntityModel<T> {
    
    // Updated Layer Location to match the new class name and your mod ID
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "rig_workbench"), "main");
    
    private final ModelPart workbench;
    private final ModelPart bottom;
    private final ModelPart row1;
    private final ModelPart row2;
    private final ModelPart row3;
    private final ModelPart row4;
    private final ModelPart row5;
    private final ModelPart row6;
    private final ModelPart row7;
    private final ModelPart top;
    private final ModelPart right;
    private final ModelPart left;
    private final ModelPart screen;

    // Renamed constructor to match the class name
    public RigWorkbenchModel(ModelPart root) {
        this.workbench = root.getChild("workbench");
        this.bottom = this.workbench.getChild("bottom");
        this.row1 = this.bottom.getChild("row1");
        this.row2 = this.bottom.getChild("row2");
        this.row3 = this.bottom.getChild("row3");
        this.row4 = this.bottom.getChild("row4");
        this.row5 = this.bottom.getChild("row5");
        this.row6 = this.bottom.getChild("row6");
        this.row7 = this.bottom.getChild("row7");
        this.top = this.workbench.getChild("top");
        this.right = this.top.getChild("right");
        this.left = this.top.getChild("left");
        this.screen = this.workbench.getChild("screen");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        PartDefinition workbench = partdefinition.addOrReplaceChild("workbench", CubeListBuilder.create(), PartPose.offset(0.0F, 16.0F, 0.0F));

        PartDefinition bottom = workbench.addOrReplaceChild("bottom", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition row1 = bottom.addOrReplaceChild("row1", CubeListBuilder.create().texOffs(0, 0).addBox(-7.0F, -6.0F, -7.0F, 14.0F, 4.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition row2 = bottom.addOrReplaceChild("row2", CubeListBuilder.create().texOffs(56, 0).addBox(-6.0F, -2.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition row3 = bottom.addOrReplaceChild("row3", CubeListBuilder.create().texOffs(0, 34).addBox(-7.0F, 0.0F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition row4 = bottom.addOrReplaceChild("row4", CubeListBuilder.create().texOffs(56, 14).addBox(-6.0F, 1.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition row5 = bottom.addOrReplaceChild("row5", CubeListBuilder.create().texOffs(0, 49).addBox(-7.0F, 3.0F, -7.0F, 14.0F, 1.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition row6 = bottom.addOrReplaceChild("row6", CubeListBuilder.create().texOffs(56, 28).addBox(-6.0F, 4.0F, -6.0F, 12.0F, 2.0F, 12.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition row7 = bottom.addOrReplaceChild("row7", CubeListBuilder.create().texOffs(0, 18).addBox(-7.0F, 6.0F, -7.0F, 14.0F, 2.0F, 14.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition top = workbench.addOrReplaceChild("top", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition right = top.addOrReplaceChild("right", CubeListBuilder.create().texOffs(52, 92).addBox(-2.0F, -15.0F, -8.0F, 2.0F, 11.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(32, 64).addBox(-2.0F, -4.0F, -8.0F, 4.0F, 4.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(92, 42).addBox(-2.0F, -15.0F, 0.0F, 4.0F, 15.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(86, 92).addBox(0.0F, -6.0F, -8.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(0, 94).addBox(0.0F, -8.0F, -8.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(96, 76).addBox(0.0F, -10.0F, -8.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(86, 101).addBox(0.0F, -12.0F, -8.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(104, 0).addBox(-0.1522F, -14.2346F, -8.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(28, 101).addBox(-3.5142F, -14.9858F, 4.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(104, 18).addBox(-3.5142F, -14.9858F, 2.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(104, 22).addBox(-3.5142F, -14.9858F, 0.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(96, 85).addBox(-3.5142F, -12.9858F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(96, 87).addBox(-3.5142F, -12.9858F, 2.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(96, 89).addBox(-3.5142F, -12.9858F, 4.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(104, 26).addBox(-1.3929F, -17.1071F, 4.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(104, 29).addBox(-1.3929F, -17.1071F, 2.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(104, 32).addBox(-1.3929F, -17.1071F, 0.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(5.0F, -6.0F, 1.0F));

        PartDefinition cube_r1 = right.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(24, 101).addBox(-2.0F, -3.0F, 1.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(20, 101).addBox(-2.0F, -3.0F, 3.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
        .texOffs(88, 59).addBox(-2.0F, -3.0F, 5.0F, 1.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.1F, -13.5716F, -1.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition cube_r2 = right.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 103).addBox(-5.7403F, -13.8582F, -7.0F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, -1.0F, 0.0F, 0.0F, 0.3927F));

        PartDefinition left = top.addOrReplaceChild("left", CubeListBuilder.create().texOffs(72, 92).addBox(0.0F, -14.0F, -7.0F, 1.0F, 14.0F, 6.0F, new CubeDeformation(0.0F))
        .texOffs(56, 59).addBox(-2.0F, -4.0F, -7.0F, 2.0F, 4.0F, 14.0F, new CubeDeformation(0.0F))
        .texOffs(0, 64).addBox(-2.0F, -6.0F, -7.0F, 2.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
        .texOffs(32, 77).addBox(-2.0F, -8.0F, -7.0F, 2.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
        .texOffs(64, 77).addBox(-2.0F, -10.0F, -7.0F, 2.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
        .texOffs(0, 79).addBox(-2.0F, -12.0F, -7.0F, 2.0F, 1.0F, 14.0F, new CubeDeformation(0.0F))
        .texOffs(56, 42).addBox(-2.0F, -16.0F, -7.0F, 4.0F, 3.0F, 14.0F, new CubeDeformation(0.0F))
        .texOffs(32, 92).addBox(0.0F, -13.0F, -1.0F, 2.0F, 13.0F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(20, 94).addBox(0.0F, -3.0F, -5.0F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), PartPose.offset(-5.0F, -6.0F, 0.0F));

        PartDefinition cube_r3 = left.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(104, 9).addBox(-10.6066F, -15.0208F, -3.0F, 1.0F, 3.0F, 6.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, 0.0F, 0.0F, 0.0F, 0.0F, 0.7854F));

        PartDefinition screen = workbench.addOrReplaceChild("screen", CubeListBuilder.create().texOffs(88, 63).addBox(-6.0F, -12.0F, -0.5F, 12.0F, 12.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, -7.0F, -8.0F));

        return LayerDefinition.create(meshdefinition, 128, 128);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {

    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        workbench.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }

    // ==========================================
    // THE ANIMATION MATH (Connected to Renderer)
    // ==========================================
    public void animate(float progress) {
        // 1. The Robotic Arms
        this.right.zRot = progress * 1.5708F; 
        this.left.zRot = progress * -1.5708F; 

        // 2. The Screen 
        this.screen.xRot = progress * -0.3054F; // Keeps the correct backward tilt
        this.screen.y = -7.0F - (progress * 2.0F); // Slides up
        this.screen.z = -8.0F + (progress * 16.0F); // FIX: Plus sign makes it slide backward into place!
    }
}