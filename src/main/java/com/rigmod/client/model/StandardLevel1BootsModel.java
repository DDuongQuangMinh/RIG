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

public class StandardLevel1BootsModel<T extends LivingEntity> extends HumanoidModel<T> {
    
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "standard_level_1_boots"), "main");

    public StandardLevel1BootsModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Dummy parts to prevent animation crashes
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation bootDef = new CubeDeformation(0.25F); // Inflates base boot to hide skin
        CubeDeformation armorDef = new CubeDeformation(0.26F); // Slightly thicker for the 3D details

        // ==========================================
        // RIGHT BOOT
        // ==========================================
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        
        // Wrapper translates Minecraft Hip (-1.9, 12, 0) to your Blockbench Origin (0, 24, 0)
        PartDefinition bb_right = right_leg.addOrReplaceChild("bb_right", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

        // Base Boot
        bb_right.addOrReplaceChild("right_base", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-4.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, bootDef), PartPose.ZERO);

        // 3D Armor Details
        bb_right.addOrReplaceChild("right_armor", CubeListBuilder.create()
            .texOffs(10, 12).addBox(-4.0F, -2.0F, -2.2F, 4.0F, 2.0F, 1.0F, armorDef)
            .texOffs(0, 12).addBox(-4.0F, -2.0F, 1.2F, 4.0F, 2.0F, 1.0F, armorDef)
            .texOffs(16, 5).addBox(-4.2F, -2.0F, -2.2F, 1.0F, 2.0F, 3.0F, armorDef)
            .texOffs(6, 18).addBox(-4.2F, -2.0F, 0.2F, 1.0F, 2.0F, 2.0F, armorDef), PartPose.ZERO);


        // ==========================================
        // LEFT BOOT
        // ==========================================
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        
        // Wrapper translates Minecraft Hip (1.9, 12, 0) to your Blockbench Origin (0, 24, 0)
        PartDefinition bb_left = left_leg.addOrReplaceChild("bb_left", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));

        // Base Boot
        bb_left.addOrReplaceChild("left_base", CubeListBuilder.create()
            .texOffs(0, 6).addBox(0.0F, -2.0F, -2.0F, 4.0F, 2.0F, 4.0F, bootDef), PartPose.ZERO);

        // 3D Armor Details
        bb_left.addOrReplaceChild("left_armor", CubeListBuilder.create()
            .texOffs(0, 15).addBox(0.0F, -2.0F, -2.2F, 4.0F, 2.0F, 1.0F, armorDef)
            .texOffs(10, 15).addBox(0.0F, -2.0F, 1.2F, 4.0F, 2.0F, 1.0F, armorDef)
            .texOffs(16, 0).addBox(3.2F, -2.0F, -2.2F, 1.0F, 2.0F, 3.0F, armorDef)
            .texOffs(0, 18).addBox(3.2F, -2.0F, 0.2F, 1.0F, 2.0F, 2.0F, armorDef), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}