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

public class EngineeringLevel2BootsModel<T extends LivingEntity> extends HumanoidModel<T> {
    
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "engineering_level_2_boots"), "main");

    public EngineeringLevel2BootsModel(ModelPart root) {
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

        CubeDeformation bootDef = new CubeDeformation(0.25F); // Base Boot Inflation
        CubeDeformation armorDef = new CubeDeformation(0.26F); // Armor Details Inflation

        // ==========================================
        // RIGHT BOOT
        // ==========================================
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        
        // Wrapper translates Vanilla Hip (Y=12) down to your Blockbench Boot Root (Y=16)
        PartDefinition bb_right = right_leg.addOrReplaceChild("bb_right", CubeListBuilder.create(), PartPose.offset(1.9F, 4.0F, 0.0F));

        bb_right.addOrReplaceChild("right_boot", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-4.0F, 6.0F, -2.0F, 4.0F, 2.0F, 4.0F, bootDef)
            .texOffs(0, 12).addBox(-4.0F, 6.0F, -2.3F, 4.0F, 2.0F, 1.0F, armorDef)
            .texOffs(0, 15).addBox(-4.0F, 7.0F, 1.2F, 4.0F, 1.0F, 1.0F, armorDef), PartPose.ZERO);

        // ==========================================
        // LEFT BOOT
        // ==========================================
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        
        // Wrapper translates Vanilla Hip (Y=12) down to your Blockbench Boot Root (Y=16)
        PartDefinition bb_left = left_leg.addOrReplaceChild("bb_left", CubeListBuilder.create(), PartPose.offset(-1.9F, 4.0F, 0.0F));

        bb_left.addOrReplaceChild("left_boot", CubeListBuilder.create()
            .texOffs(0, 6).addBox(0.0F, 6.0F, -2.0F, 4.0F, 2.0F, 4.0F, bootDef)
            .texOffs(10, 12).addBox(0.0F, 6.0F, -2.3F, 4.0F, 2.0F, 1.0F, armorDef)
            .texOffs(10, 15).addBox(0.0F, 7.0F, 1.2F, 4.0F, 1.0F, 1.0F, armorDef), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}