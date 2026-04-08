package com.rigmod.client;

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

public class StandardLevel1LeggingsModel<T extends LivingEntity> extends HumanoidModel<T> {
    
    // I updated the layer location to 'leggings' to keep things organized!
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "standard_level_1_leggings"), "main");

    public StandardLevel1LeggingsModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. Define standard dummy humanoid parts so the game doesn't crash
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);

        // TARGETED INFLATION RULES
        CubeDeformation armorInflation = new CubeDeformation(0.25F); // Puffs base legs to hide skin
        CubeDeformation outerInflation = new CubeDeformation(0.26F); // 0.01F thicker to permanently stop Z-fighting!

        // ==========================================
        // RIGHT LEG (Mathematically locked to Vanilla pivot: -1.9F, 12.0F, 0.0F)
        // ==========================================
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, armorInflation), 
            PartPose.offset(-1.9F, 12.0F, 0.0F));

        // Shifted by (0.9F, +12.0F, -1.0F) to perfectly match your Blockbench design relative to the vanilla bone
        right_leg.addOrReplaceChild("right_armor", CubeListBuilder.create()
            .texOffs(16, 24).addBox(-3.0F, -6.0F, -1.2F, 4.0F, 4.0F, 1.0F, outerInflation)
            .texOffs(28, 12).addBox(-3.0F, -6.0F, 2.2F, 4.0F, 4.0F, 1.0F, outerInflation)
            .texOffs(16, 0).addBox(-3.0F, -7.75F, -1.5F, 4.0F, 1.0F, 5.0F, outerInflation)
            .texOffs(16, 12).addBox(-3.4F, -7.75F, -1.5F, 1.0F, 1.0F, 5.0F, outerInflation), 
            PartPose.offset(0.9F, 12.0F, -1.0F));

        // ==========================================
        // LEFT LEG (Mathematically locked to Vanilla pivot: 1.9F, 12.0F, 0.0F)
        // ==========================================
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create()
            .texOffs(0, 16).addBox(-2.0F, 0.0F, -2.0F, 4.0F, 12.0F, 4.0F, armorInflation), 
            PartPose.offset(1.9F, 12.0F, 0.0F));

        // Shifted by (-1.9F, +12.0F, 0.0F) to perfectly match your Blockbench design relative to the vanilla bone
        left_leg.addOrReplaceChild("left_armor", CubeListBuilder.create()
            .texOffs(26, 24).addBox(0.0F, -6.0F, -2.2F, 4.0F, 4.0F, 1.0F, outerInflation)
            .texOffs(16, 6).addBox(0.0F, -7.75F, -2.5F, 4.0F, 1.0F, 5.0F, outerInflation)
            .texOffs(28, 17).addBox(0.0F, -6.0F, 1.2F, 4.0F, 4.0F, 1.0F, outerInflation)
            .texOffs(16, 18).addBox(3.4F, -7.75F, -2.5F, 1.0F, 1.0F, 5.0F, outerInflation), 
            PartPose.offset(-1.9F, 12.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}