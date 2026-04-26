package com.rigmod.client.model;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class EngineeringLevel4ChestplateModel<T extends LivingEntity> extends HumanoidModel<T> {
    
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation("rigmod", "engineering_level_4_chestplate"), "main");

    public EngineeringLevel4ChestplateModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 🔥 WIPE VANILLA GHOST BOXES to stop texture tearing
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));
        PartDefinition leftArm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.offset(5.0F, 2.0F, 0.0F));

        // ==========================================
        // 🔥 CUSTOM BODY (Base inflated 0.26F to hide skin permanently)
        // ==========================================
        PartDefinition customBody = body.addOrReplaceChild("custom_body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -24.0F, -2.0F, 8.0F, 12.0F, 4.0F, new CubeDeformation(0.26F)), PartPose.offset(0.0F, 24.0F, 0.0F));
        PartDefinition armor = customBody.addOrReplaceChild("armor", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        
        // 🔥 THE FIX: All details use CubeDeformation(0.26F, 0.0F, 0.26F) to expand perfectly outward without breaking UV maps!
        PartDefinition Front = armor.addOrReplaceChild("Front", CubeListBuilder.create()
        .texOffs(0, 46).addBox(-3.5F, -15.2F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(28, 47).addBox(0.4848F, -15.2F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 46).addBox(-3.5F, -16.4F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(20, 47).addBox(0.4848F, -16.4F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 22).addBox(-3.5F, -17.6F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 28).addBox(0.4848F, -17.6F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 24).addBox(-3.5F, -18.8F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 26).addBox(0.4848F, -18.8F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 30).addBox(-3.5F, -20.2F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 32).addBox(0.4848F, -20.2F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 34).addBox(-3.5F, -21.3F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 36).addBox(0.4848F, -21.3F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 38).addBox(-3.5F, -22.4F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 40).addBox(0.4848F, -22.4F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 42).addBox(-3.5F, -23.5F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(44, 44).addBox(0.4848F, -23.5F, -2.25F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Side = armor.addOrReplaceChild("Side", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        Side.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(36, 45).addBox(-2.0F, -1.0F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(8, 48).addBox(-2.0F, 0.2F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(28, 45).addBox(-2.0F, 1.4F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(48, 6).addBox(-2.0F, 2.6F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(3.2F, -17.8F, -0.5F, 0.0F, 1.5708F, 0.0F));
        Side.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(20, 45).addBox(-2.0F, -1.0F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(0, 48).addBox(-2.0F, 0.2F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(42, 4).addBox(-2.0F, 1.4F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(36, 47).addBox(-2.0F, 2.6F, 0.0F, 3.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(-4.25F, -17.8F, -0.5F, 0.0F, 1.5708F, 0.0F));

        armor.addOrReplaceChild("back", CubeListBuilder.create().texOffs(20, 37).addBox(-2.5152F, -17.6F, 1.2F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(20, 39).addBox(-2.5152F, -16.4F, 1.2F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(32, 39).addBox(-2.5152F, -15.2F, 1.2F, 5.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(24, 6).addBox(-2.5F, -23.35F, 1.2F, 5.0F, 5.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(24, 49).addBox(2.0F, -24.0F, 1.2F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(28, 49).addBox(-3.0F, -24.0F, 1.2F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition rig_system = customBody.addOrReplaceChild("rig_system", CubeListBuilder.create().texOffs(44, 11).addBox(-1.0F, -22.75F, 1.7F, 2.0F, 4.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(20, 49).addBox(-0.5F, -17.5152F, 1.6514F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        rig_system.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(52, 19).addBox(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(-0.5F, -12.5323F, 2.9124F, 0.1309F, 0.0F, 0.0F));
        rig_system.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(16, 48).addBox(0.0F, -2.0F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(-0.5F, -18.5076F, 2.8257F, -0.0873F, 0.0F, 0.0F));
        rig_system.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(32, 37).addBox(0.0F, -2.0F, -1.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(-2.75F, -20.1F, 2.5F, 0.0322F, -0.0295F, 0.7413F));
        rig_system.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(12, 50).addBox(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(-0.5F, -20.5F, 3.0F, 0.0436F, 0.0F, 0.0F));
        rig_system.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(14, 42).addBox(-2.0F, -3.0F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(0.0F, -20.0F, 2.6F, 0.0F, -0.1309F, 0.0F));
        rig_system.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(8, 42).addBox(0.0F, -3.0F, -1.0F, 2.0F, 5.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(0.0F, -20.0F, 2.6F, 0.0F, 0.1309F, 0.0F));

        PartDefinition belt = customBody.addOrReplaceChild("belt", CubeListBuilder.create().texOffs(24, 0).addBox(-4.0F, -14.0F, -2.25F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(24, 3).addBox(-4.0F, -14.0F, 1.25F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        
        PartDefinition pocket = belt.addOrReplaceChild("pocket", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        pocket.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(44, 16).addBox(-2.0F, -1.0F, 0.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(2.75F, -13.0F, 1.5F, 0.0F, 0.0F, -0.1309F));
        pocket.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(44, 19).addBox(-1.0F, -1.0F, 0.0F, 3.0F, 2.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(-2.75F, -13.0F, 1.5F, 0.0F, 0.0F, 0.1309F));
        pocket.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(44, 48).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(2.5F, -12.0F, -2.5F, 0.0F, 0.0F, -0.1309F));
        pocket.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(48, 8).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(-2.5F, -12.0F, -2.5F, 0.0F, 0.0F, 0.1309F));

        // Pushed the entire tablet outward by -0.3F so we don't need any deformation to clear the chest
        // 🔥 THE FIX: Pushed the flat plane forward by -0.3F so it clears the chest perfectly
        PartDefinition Tablet = customBody.addOrReplaceChild("Tablet", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -0.3F));
        Tablet.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(52, 24).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.866F, -21.474F, -2.2506F, 0.725F, -0.4079F, -0.338F));
        Tablet.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(52, 26).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.866F, -21.474F, -2.2506F, 0.725F, 0.4079F, 0.338F));
        Tablet.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(52, 22).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 2.0F, 0.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -21.1696F, -2.6473F, 0.6545F, 0.0F, 0.0F));
        Tablet.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(32, 49).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.0F, -19.2F, -2.3F, 0.1745F, 0.0F, 0.0F));

        // ==========================================
        // 🔥 CUSTOM LEFT ARM (Base inflated 0.26F. Coordinates completely restored)
        // ==========================================
        PartDefinition customLeft = leftArm.addOrReplaceChild("custom_left", CubeListBuilder.create().texOffs(0, 16).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.26F)), PartPose.offset(1.0F, 4.0F, 0.0F));
        
        // Pushed up by 0.2F (-0.2F on the Y axis)
        PartDefinition armor_plate_left = customLeft.addOrReplaceChild("armor_plate_left", CubeListBuilder.create(), PartPose.offset(0.0F, -0.2F, 0.0F));
        armor_plate_left.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(52, 17).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(36, 6).addBox(-1.0F, -2.0F, -3.2F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(52, 15).addBox(-1.0F, -2.0F, -3.4F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(1.75F, -0.7F, 1.2F, 0.0F, 0.0F, -0.1309F));
        armor_plate_left.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(6, 52).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(32, 27).addBox(-1.0F, -2.0F, -3.2F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(0, 52).addBox(-1.0F, -2.0F, -3.4F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(1.55F, -1.9F, 1.2F, 0.0F, 0.0F, -0.1309F));
        armor_plate_left.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(44, 51).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(24, 32).addBox(-1.0F, -2.0F, -3.2F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(38, 51).addBox(-1.0F, -2.0F, -3.4F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(1.45F, -3.1F, 1.2F, 0.0F, 0.0F, -0.1309F));
        armor_plate_left.addOrReplaceChild("cube_r20", CubeListBuilder.create().texOffs(0, 50).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(12, 32).addBox(-1.0F, -2.0F, -3.2F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(38, 49).addBox(-1.0F, -2.0F, -3.4F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(1.25F, -4.3F, 1.2F, 0.0F, 0.0F, -0.1309F));
        
        customLeft.addOrReplaceChild("shoulder_left", CubeListBuilder.create().texOffs(0, 37).addBox(-2.0F, -6.25F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.26F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        
        customLeft.addOrReplaceChild("hand_plate_left", CubeListBuilder.create().texOffs(0, 42).addBox(1.2F, 4.0F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(42, 0).addBox(1.2F, 2.0F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(24, 12).addBox(1.2F, 0.9F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(20, 41).addBox(1.2F, -0.2F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(28, 41).addBox(1.2F, -1.3F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        // ==========================================
        // 🔥 CUSTOM RIGHT ARM (Base inflated 0.26F. Coordinates completely restored)
        // ==========================================
        PartDefinition customRight = rightArm.addOrReplaceChild("custom_right", CubeListBuilder.create().texOffs(16, 16).addBox(-2.0F, -6.0F, -2.0F, 4.0F, 12.0F, 4.0F, new CubeDeformation(0.26F)), PartPose.offset(-1.0F, 4.0F, 0.0F));
        
        // Pushed up by 0.2F (-0.2F on the Y axis)
        PartDefinition armor_plate_right = customRight.addOrReplaceChild("armor_plate_right", CubeListBuilder.create(), PartPose.offset(0.0F, -0.2F, 0.0F));
        armor_plate_right.addOrReplaceChild("cube_r21", CubeListBuilder.create().texOffs(50, 50).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(50, 48).addBox(-1.0F, -2.0F, 2.4F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(32, 22).addBox(-1.0F, -2.0F, -0.8F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(-1.65F, -0.7F, -1.2F, 0.0F, 0.0F, 0.1309F));
        armor_plate_right.addOrReplaceChild("cube_r22", CubeListBuilder.create().texOffs(50, 13).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(50, 11).addBox(-1.0F, -2.0F, 2.4F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(32, 17).addBox(-1.0F, -2.0F, -0.8F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(-1.55F, -1.9F, -1.2F, 0.0F, 0.0F, 0.1309F));
        armor_plate_right.addOrReplaceChild("cube_r23", CubeListBuilder.create().texOffs(6, 50).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(50, 4).addBox(-1.0F, -2.0F, 2.4F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(32, 12).addBox(-1.0F, -2.0F, -0.8F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(-1.45F, -3.1F, -1.2F, 0.0F, 0.0F, 0.1309F));
        armor_plate_right.addOrReplaceChild("cube_r24", CubeListBuilder.create().texOffs(50, 2).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(50, 0).addBox(-1.0F, -2.0F, 2.4F, 2.0F, 1.0F, 1.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(0, 32).addBox(-1.0F, -2.0F, -0.8F, 2.0F, 1.0F, 4.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offsetAndRotation(-1.25F, -4.3F, -1.2F, 0.0F, 0.0F, 0.1309F));
        
        customRight.addOrReplaceChild("shoulder_right", CubeListBuilder.create().texOffs(10, 37).addBox(1.0F, -6.25F, -2.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.26F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        
        customRight.addOrReplaceChild("hand_plate_right", CubeListBuilder.create().texOffs(36, 32).addBox(-2.2F, -1.0F, -1.5F, 1.0F, 4.0F, 3.0F, new CubeDeformation(0.26F, 0.0F, 0.26F))
        .texOffs(36, 41).addBox(-2.2F, 4.0F, -1.5F, 1.0F, 1.0F, 3.0F, new CubeDeformation(0.26F, 0.0F, 0.26F)), PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        this.head.visible = false;
        this.hat.visible = false;
        this.rightLeg.visible = false;
        this.leftLeg.visible = false;
        
        super.renderToBuffer(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}