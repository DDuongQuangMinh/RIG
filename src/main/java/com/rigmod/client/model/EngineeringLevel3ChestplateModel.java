package com.rigmod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rigmod.RigMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class EngineeringLevel3ChestplateModel<T extends LivingEntity> extends HumanoidModel<T> {
    
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "engineering_level_3_chestplate"), "main");

    public EngineeringLevel3ChestplateModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        CubeDeformation armorInflation = new CubeDeformation(0.25F); 
        CubeDeformation noInflation = new CubeDeformation(0.0F);     

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create()
        .texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, armorInflation), PartPose.ZERO);

        body.addOrReplaceChild("belt", CubeListBuilder.create()
        .texOffs(24, 0).addBox(-4.0F, 10.0F, 1.2F, 8.0F, 2.0F, 1.0F, armorInflation)
        .texOffs(24, 3).addBox(-4.0F, 10.0F, -2.2F, 8.0F, 2.0F, 1.0F, armorInflation)
        .texOffs(42, 4).addBox(-3.0F, 10.0F, -2.5F, 1.0F, 2.0F, 1.0F, armorInflation)
        .texOffs(6, 42).addBox(2.0F, 10.0F, -2.5F, 1.0F, 2.0F, 1.0F, armorInflation)
        .texOffs(42, 7).addBox(-3.0F, 10.0F, 1.5F, 1.0F, 2.0F, 1.0F, armorInflation)
        .texOffs(42, 33).addBox(2.0F, 10.0F, 1.5F, 1.0F, 2.0F, 1.0F, armorInflation), PartPose.ZERO);

        PartDefinition front_armor = body.addOrReplaceChild("front_armor", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -0.25F));
        front_armor.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(40, 19).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-2.5F, 2.3F, -1.2F, 0.0F, 0.0F, 0.0698F));
        front_armor.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(40, 17).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(2.5F, 2.3F, -1.2F, 0.0F, 0.0F, -0.0698F));
        front_armor.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(40, 15).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-2.5F, 3.5F, -1.2F, 0.0F, 0.0F, 0.0698F));
        front_armor.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(40, 13).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(2.5F, 3.5F, -1.2F, 0.0F, 0.0F, -0.0698F));
        front_armor.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(26, 39).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-2.5F, 4.7F, -1.2F, 0.0F, 0.0F, 0.0698F));
        front_armor.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(20, 39).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(2.5F, 4.7F, -1.2F, 0.0F, 0.0F, -0.0698F));
        front_armor.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(38, 27).addBox(-2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(2.5F, 5.9F, -1.2F, 0.0F, 0.0F, -0.0698F));
        front_armor.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(38, 25).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-2.5F, 5.9F, -1.2F, 0.0F, 0.0F, 0.0698F));
        front_armor.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(38, 23).addBox(-2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(2.5F, 7.1F, -1.2F, 0.0F, 0.0F, -0.0698F));
        front_armor.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(38, 21).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-2.5F, 7.1F, -1.2F, 0.0F, 0.0F, 0.0698F));
        front_armor.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(28, 37).addBox(-2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(2.5F, 8.3F, -1.2F, 0.0F, 0.0F, -0.0698F));
        front_armor.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(12, 37).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-2.5F, 8.3F, -1.2F, 0.0F, 0.0F, 0.0698F));
        front_armor.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(36, 11).addBox(-2.0F, -1.0F, -1.0F, 3.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(2.5F, 9.5F, -1.2F, 0.0F, 0.0F, -0.0698F));
        front_armor.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(24, 14).addBox(-1.0F, -1.0F, -1.0F, 3.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-2.5F, 9.5F, -1.2F, 0.0F, 0.0F, 0.0698F));

        PartDefinition tablet = body.addOrReplaceChild("tablet", CubeListBuilder.create(), PartPose.offset(0.0F, 0.2F, -0.25F));
        tablet.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(42, 40).addBox(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 0.0F, noInflation), PartPose.offsetAndRotation(-1.653F, 2.1379F, -1.2984F, 0.4996F, 0.27F, 0.1446F));
        tablet.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(10, 42).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 0.0F, noInflation), PartPose.offsetAndRotation(1.653F, 2.1379F, -1.2984F, 0.4996F, -0.27F, -0.1446F));
        tablet.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(42, 36).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 0.0F, noInflation), PartPose.offsetAndRotation(0.0F, 2.2554F, -1.524F, 0.48F, 0.0F, 0.0F));
        tablet.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(36, 38).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, noInflation), PartPose.offsetAndRotation(0.0F, 4.7F, -2.15F, 0.1309F, 0.0F, 0.0F));

        PartDefinition back_armor = body.addOrReplaceChild("back_armor", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.25F));
        back_armor.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(36, 41).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-2.7F, 7.4F, 2.2F, 0.0F, 0.0F, 0.0698F));
        back_armor.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(26, 41).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-2.7F, 8.5F, 2.2F, 0.0F, 0.0F, 0.0698F));
        back_armor.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(0, 42).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(2.7F, 9.6F, 2.2F, 0.0F, 0.0F, -0.0698F));
        back_armor.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(42, 0).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(2.7F, 8.5F, 2.2F, 0.0F, 0.0F, -0.0698F));
        back_armor.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(42, 2).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(2.7F, 7.4F, 2.2F, 0.0F, 0.0F, -0.0698F));
        back_armor.addOrReplaceChild("cube_r25", CubeListBuilder.create().texOffs(20, 41).addBox(-1.0F, -1.0F, -1.0F, 2.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-2.7F, 9.6F, 2.2F, 0.0F, 0.0F, 0.0698F));
        back_armor.addOrReplaceChild("cube_r30", CubeListBuilder.create().texOffs(32, 27).addBox(-2.0F, -4.0F, -1.0F, 2.0F, 5.0F, 1.0F, noInflation), PartPose.offsetAndRotation(0.0F, 5.0F, 2.5F, 0.0F, -0.1309F, 0.0F));
        back_armor.addOrReplaceChild("cube_r31", CubeListBuilder.create().texOffs(32, 21).addBox(0.0F, -4.0F, -1.0F, 2.0F, 5.0F, 1.0F, noInflation), PartPose.offsetAndRotation(0.0F, 5.0F, 2.5F, 0.0F, 0.1309F, 0.0F));

        PartDefinition spine = body.addOrReplaceChild("spine", CubeListBuilder.create(), PartPose.offset(0.0F, 0.2F, 0.25F));
        spine.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(42, 38).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 1.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-0.5F, 3.0F, 1.5F, 0.0F, 0.0F, -0.7854F));
        spine.addOrReplaceChild("cube_r26", CubeListBuilder.create().texOffs(16, 39).addBox(0.0F, -2.0F, -1.0F, 1.0F, 4.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-0.5F, 9.2392F, 2.3791F, -0.0873F, 0.0F, 0.0F));
        spine.addOrReplaceChild("cube_r27", CubeListBuilder.create().texOffs(32, 39).addBox(0.0F, -2.0F, -1.0F, 1.0F, 3.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-0.5F, 6.262F, 2.727F, -0.1745F, 0.0F, 0.0F));
        spine.addOrReplaceChild("cube_r28", CubeListBuilder.create().texOffs(12, 39).addBox(0.0F, -2.0F, -1.0F, 1.0F, 4.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-0.5F, 2.3F, 2.9F, 0.0873F, 0.0F, 0.0F));
        spine.addOrReplaceChild("cube_r29", CubeListBuilder.create().texOffs(6, 37).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 1.0F, noInflation), PartPose.offsetAndRotation(0.0F, 5.9848F, 2.6F, -0.0873F, 0.0F, 0.0F));
        spine.addOrReplaceChild("cube_r32", CubeListBuilder.create().texOffs(0, 37).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 4.0F, 1.0F, noInflation), PartPose.offsetAndRotation(0.0F, 2.0F, 2.6F, 0.0873F, 0.0F, 0.0F));

        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
        .texOffs(16, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, armorInflation)
        .texOffs(10, 32).addBox(0.0F, -2.2F, -2.0F, 1.0F, 1.0F, 4.0F, armorInflation), PartPose.ZERO); 

        right_arm.addOrReplaceChild("right_panels", CubeListBuilder.create()
        .texOffs(20, 32).addBox(-3.2F, 2.4F, -1.5F, 1.0F, 4.0F, 3.0F, noInflation) 
        .texOffs(38, 29).addBox(-3.2F, 6.75F, -1.0F, 1.0F, 2.0F, 2.0F, noInflation),
        PartPose.offset(-0.25F, 0.0F, 0.0F));

        right_arm.addOrReplaceChild("cube_r33", CubeListBuilder.create()
        .texOffs(24, 6).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 4.0F, 4.0F, armorInflation)
        .texOffs(36, 6).addBox(-1.0F, -2.0F, 1.2F, 2.0F, 4.0F, 1.0F, armorInflation)
        .texOffs(36, 33).addBox(-1.0F, -2.0F, -2.2F, 2.0F, 4.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(-2.3F, -0.25F, 0.0F, 0.0F, 0.0F, 0.0873F));

        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
        .texOffs(0, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, armorInflation)
        .texOffs(0, 32).addBox(-1.0F, -2.2F, -2.0F, 1.0F, 1.0F, 4.0F, armorInflation), PartPose.ZERO); 

        left_arm.addOrReplaceChild("left_panels", CubeListBuilder.create()
        .texOffs(28, 33).addBox(2.2F, 7.5F, -1.5F, 1.0F, 1.0F, 3.0F, noInflation)  
        .texOffs(32, 14).addBox(2.2F, 2.4F, -1.5F, 1.0F, 4.0F, 3.0F, noInflation),
        PartPose.offset(0.25F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}