package com.rigmod.client;

import com.rigmod.RigMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class StandardLevel1LeggingsModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "standard_level_1_leggings"), "main");

    public StandardLevel1LeggingsModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. EMPTY STANDARD PARTS
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);

        // --- 2. LEFT LEG ---
        PartDefinition leftLeg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

        // THE FIX: We added X = -2.0F to pull the leg inward and close the gap!
        PartDefinition userLeftLeg = leftLeg.addOrReplaceChild("user_left_leg", CubeListBuilder.create()
                .texOffs(20, 21).addBox(-2.0F, -7.5F, 3.25F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 24).addBox(-2.0F, -7.5F, -0.25F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 28).addBox(-2.0F, -3.0F, -0.25F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 29).addBox(-2.0F, -3.0F, 3.25F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 29).addBox(-2.0F, -4.5F, -0.25F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 30).addBox(-2.0F, -4.5F, 3.25F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 10).addBox(-2.25F, -4.5F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(10, 20).addBox(-2.25F, -3.0F, 0.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 20).addBox(-2.25F, -7.5F, 0.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 0).addBox(1.25F, -8.0F, 0.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), 
                PartPose.offsetAndRotation(-2.0F, 12.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        userLeftLeg.addOrReplaceChild("cube_r1", CubeListBuilder.create()
                .texOffs(10, 0).addBox(-1.0F, -3.0F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), 
                PartPose.offsetAndRotation(2.25F, -5.25F, 2.0F, 0.0F, 0.0F, 0.48F));

        // --- 3. RIGHT LEG ---
        PartDefinition rightLeg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));

        // THE FIX: We added X = 2.0F to pull the right leg inward and close the gap!
        PartDefinition userRightLeg = rightLeg.addOrReplaceChild("user_right_leg", CubeListBuilder.create()
                .texOffs(10, 25).addBox(-2.0F, -3.0F, -4.25F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 26).addBox(-2.0F, -3.0F, -0.75F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 27).addBox(-2.0F, -4.5F, -4.25F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 27).addBox(-2.0F, -4.5F, -0.75F, 4.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 15).addBox(-2.0F, -7.5F, -0.75F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 18).addBox(-2.0F, -7.5F, -4.25F, 4.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(20, 0).addBox(-2.25F, -3.0F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(20, 5).addBox(-2.25F, -4.5F, -4.0F, 1.0F, 1.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(10, 14).addBox(-2.25F, -7.5F, -4.0F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 10).addBox(1.25F, -8.0F, -4.0F, 1.0F, 6.0F, 4.0F, new CubeDeformation(0.0F)), 
                PartPose.offsetAndRotation(2.0F, 12.0F, 0.0F, 0.0F, 1.5708F, 0.0F));

        userRightLeg.addOrReplaceChild("cube_r2", CubeListBuilder.create()
                .texOffs(10, 7).addBox(-1.0F, -3.0F, -2.0F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F)), 
                PartPose.offsetAndRotation(2.25F, -5.25F, -2.0F, 0.0F, 0.0F, 0.48F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}