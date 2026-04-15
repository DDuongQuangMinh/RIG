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

public class EngineeringLevel3LeggingsModel<T extends LivingEntity> extends HumanoidModel<T> {
    
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "engineering_level_3_leggings"), "main");

    public EngineeringLevel3LeggingsModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Dummy parts
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation pantsDef = new CubeDeformation(0.25F);
        CubeDeformation armorDef = new CubeDeformation(0.26F);

        // ==========================================
        // RIGHT LEG
        // ==========================================
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        PartDefinition bb_right = right_leg.addOrReplaceChild("bb_right", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

        bb_right.addOrReplaceChild("base_pants_right", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -2.0F, 4.0F, 10.0F, 4.0F, pantsDef), PartPose.ZERO);

        PartDefinition armor_right = bb_right.addOrReplaceChild("armor_right", CubeListBuilder.create()
            .texOffs(16, 0).addBox(-4.0F, -6.0F, -2.2F, 4.0F, 4.0F, 1.0F, armorDef)
            .texOffs(16, 10).addBox(-4.0F, -8.0F, -2.2F, 4.0F, 1.0F, 1.0F, armorDef)
            .texOffs(16, 14).addBox(-4.2F, -6.0F, -2.2F, 1.0F, 4.0F, 2.0F, armorDef)
            .texOffs(26, 3).addBox(-4.2F, -8.0F, -2.2F, 1.0F, 1.0F, 2.0F, armorDef)
            .texOffs(22, 14).addBox(-4.0F, -6.0F, -2.7F, 4.0F, 1.0F, 1.0F, armorDef)
            .texOffs(22, 22).addBox(-4.2F, -6.0F, -2.7F, 1.0F, 1.0F, 2.0F, armorDef), PartPose.ZERO);

        armor_right.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(22, 18).addBox(-2.0F, -5.0F, 0.0F, 4.0F, 2.0F, 0.0F, armorDef), PartPose.offsetAndRotation(-2.0F, -3.2811F, -1.4321F, 0.4363F, 0.0F, 0.0F));


        // ==========================================
        // LEFT LEG
        // ==========================================
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        PartDefinition bb_left = left_leg.addOrReplaceChild("bb_left", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));

        bb_left.addOrReplaceChild("base_pants_left", CubeListBuilder.create().texOffs(0, 14).addBox(0.0F, -12.0F, -2.0F, 4.0F, 10.0F, 4.0F, pantsDef), PartPose.ZERO);

        PartDefinition armor_left = bb_left.addOrReplaceChild("armor_left", CubeListBuilder.create()
            .texOffs(16, 5).addBox(0.0F, -6.0F, -2.2F, 4.0F, 4.0F, 1.0F, armorDef)
            .texOffs(16, 12).addBox(0.0F, -8.0F, -2.2F, 4.0F, 1.0F, 1.0F, armorDef)
            .texOffs(16, 20).addBox(3.2F, -6.0F, -2.2F, 1.0F, 4.0F, 2.0F, armorDef)
            .texOffs(26, 0).addBox(3.2F, -8.0F, -2.2F, 1.0F, 1.0F, 2.0F, armorDef)
            .texOffs(22, 16).addBox(0.0F, -6.0F, -2.7F, 4.0F, 1.0F, 1.0F, armorDef)
            .texOffs(22, 25).addBox(3.2F, -6.0F, -2.7F, 1.0F, 1.0F, 2.0F, armorDef), PartPose.ZERO);

        armor_left.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(22, 20).addBox(-2.0F, -5.0F, 0.0F, 4.0F, 2.0F, 0.0F, armorDef), PartPose.offsetAndRotation(2.0F, -3.2811F, -1.4321F, 0.4363F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}