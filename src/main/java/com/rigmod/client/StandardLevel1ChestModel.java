package com.rigmod.client;

import com.rigmod.RigMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class StandardLevel1ChestModel<T extends LivingEntity> extends HumanoidModel<T> {
    
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "standard_level_1_chest"), "main");
    
    private final ModelPart lifeSupport;
    public final ModelPart rightArm; // 1. ADDED THIS LINE

    public StandardLevel1ChestModel(ModelPart root) {
        super(root);
        this.lifeSupport = root.getChild("body").getChild("lifeSupport");
        this.rightArm = root.getChild("right_arm"); // 2. ADDED THIS LINE
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. STANDARD PARTS (Head, Hat, Legs, Left Arm stay empty so they don't overwrite vanilla armor)
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        // 2. RIGHT ARM (We attach the "tablet" here so it animates with the arm!)
        PartDefinition rightArm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.offset(-5.0F, 2.0F, 0.0F));

        rightArm.addOrReplaceChild("tablet", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-3.25F, 0.0F, -2.0F, 1.0F, 4.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 8).addBox(-3.0F, 0.0F, -2.25F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(10, 0).addBox(-3.0F, 0.0F, 1.25F, 4.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), 
                // FIXED: Decreased from 6.0F to 4.0F to pull it exactly 2 pixels higher
                PartPose.offset(0.0F, 4.0F, 0.0F));

        // 3. BODY (Spine and Health Bar stay here so they follow the torso)
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);

        // FIXED: Changed to 17.0F to pull the top of the RIG away from the neck
        PartDefinition lifeSupport = body.addOrReplaceChild("lifeSupport", CubeListBuilder.create(), PartPose.offset(0.0F, 17.0F, 0.0F));

        PartDefinition healthBar = lifeSupport.addOrReplaceChild("healthBar", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        healthBar.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(6, 17).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.8558F, -14.5795F, 1.25F, 0.0F, 0.0F, 0.2182F));
        healthBar.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(0, 17).addBox(0.0F, -2.0F, -1.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.8558F, -14.6295F, 1.25F, 0.0F, 0.0F, -0.2182F));
        healthBar.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(16, 9).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.8558F, -13.6295F, 1.25F, 0.0F, 0.0F, -0.2182F));
        healthBar.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(16, 5).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.8558F, -13.5795F, 1.25F, 0.0F, 0.0F, 0.2182F));
        healthBar.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(8, 13).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.5F, -12.05F, 1.25F, 0.0F, 0.0F, 0.1745F));
        healthBar.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(0, 13).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -12.0F, 1.25F, 0.0F, 0.0F, -0.1745F));
        healthBar.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(12, 17).addBox(0.0F, -8.0F, -1.0F, 1.0F, 2.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -11.0F, 4.75F, 0.1745F, 0.0F, 0.0F));
        healthBar.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(16, 13).addBox(0.0F, -6.0F, -1.0F, 1.0F, 6.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -10.9602F, 2.9249F, -0.1309F, 0.0F, 0.0F));
        healthBar.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(20, 13).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -17.0F, 3.0F, 0.2618F, 0.0F, 0.0F));
        healthBar.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(10, 5).addBox(-1.0F, -2.0F, -1.0F, 2.0F, 7.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -14.25F, 2.75F, -0.0873F, 0.0F, 0.0F));
        healthBar.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(4, 22).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.75F, -14.0F, 1.5F, 0.0F, 0.0F, -0.7854F));

        PartDefinition spine = lifeSupport.addOrReplaceChild("spine", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        spine.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(20, 25).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(16, 25).addBox(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(24, 23).addBox(0.0F, 1.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.6164F, -8.1F, 1.0251F, 0.0F, -0.1745F, 0.0F));
        spine.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(24, 21).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(12, 24).addBox(0.0F, 0.25F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(24, 10).addBox(0.0F, 1.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-2.6249F, -8.1F, 1.1651F, 0.0F, 0.1745F, 0.0F));
        spine.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(8, 24).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(24, 6).addBox(0.0F, -2.25F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(24, 4).addBox(0.0F, -3.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.25F, -5.6F, 1.25F, 0.0F, 0.2182F, 0.0F));
        spine.addOrReplaceChild("cube_r15", CubeListBuilder.create().texOffs(24, 8).addBox(0.0F, -1.0F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(4, 24).addBox(0.0F, -2.25F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)).texOffs(8, 22).addBox(0.0F, -3.5F, 0.0F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.25F, -5.6F, 1.0F, 0.0F, -0.2182F, 0.0F));
        spine.addOrReplaceChild("cube_r16", CubeListBuilder.create().texOffs(0, 22).addBox(0.0F, -2.0F, -1.0F, 1.0F, 4.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-0.5F, -7.5F, 2.5F, 0.0873F, 0.0F, 0.0F));

        PartDefinition chestTablet = lifeSupport.addOrReplaceChild("chestTablet", CubeListBuilder.create().texOffs(20, 17).addBox(-1.0F, -16.0F, -2.25F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 0.0F, 0.0F));
        chestTablet.addOrReplaceChild("cube_r17", CubeListBuilder.create().texOffs(12, 20).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(1.9063F, -14.1049F, -2.5732F, 0.8777F, -0.2895F, -0.3311F));
        chestTablet.addOrReplaceChild("cube_r18", CubeListBuilder.create().texOffs(20, 0).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.9063F, -14.1049F, -2.5732F, 0.8777F, 0.2895F, 0.3311F));
        chestTablet.addOrReplaceChild("cube_r19", CubeListBuilder.create().texOffs(18, 21).addBox(-1.0F, -2.0F, 0.0F, 2.0F, 3.0F, 1.0F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(0.0F, -13.7934F, -2.8588F, 0.829F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
    }
}