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

public class Level2LeggingsModel<T extends LivingEntity> extends HumanoidModel<T> {
    
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "level_2_leggings"), "main");

    public Level2LeggingsModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. Define standard dummy humanoid parts so the game doesn't crash
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);

        CubeDeformation pantsInflation = new CubeDeformation(0.25F); // Prevents base leg Z-fighting
        CubeDeformation armorInflation = new CubeDeformation(0.0F);  // Preserves your exact tiny gap

        // ==========================================
        // RIGHT LEG (Uses Blockbench's "left" group because of the 180 flip)
        // ==========================================
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        
        // THE MAGIC WRAPPER: Translates Vanilla Hip (-1.9, 12, 0) perfectly to Blockbench Floor (0, 24, 0)
        PartDefinition bb_left = right_leg.addOrReplaceChild("bb_left", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

        // EXACT Blockbench raw export for the Left Leg pasted inside the wrapper
        bb_left.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, -6.0F, -1.0F, 4.0F, 12.0F, 4.0F, pantsInflation), PartPose.offsetAndRotation(0.0F, -6.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));

        PartDefinition armor_left = bb_left.addOrReplaceChild("armor_left", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        armor_left.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(26, 5).addBox(3.4721F, -5.5564F, -0.5F, 1.0F, 2.0F, 3.0F, armorInflation), PartPose.offsetAndRotation(0.0F, -6.0F, 1.0F, 3.1416F, 0.0F, -3.098F));
        armor_left.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(26, 0).addBox(2.2F, -1.2F, -4.2F, 1.0F, 1.0F, 4.0F, armorInflation)
        .texOffs(16, 0).addBox(2.2F, 0.0F, -4.2F, 1.0F, 4.0F, 4.0F, armorInflation), PartPose.offsetAndRotation(0.0F, -6.0F, 1.0F, 0.0F, 1.5708F, 0.0F));
        armor_left.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(26, 15).addBox(3.2F, -1.2F, 0.0F, 1.0F, 1.0F, 3.0F, armorInflation)
        .texOffs(16, 21).addBox(3.2F, 0.0F, 0.0F, 1.0F, 4.0F, 3.0F, armorInflation), PartPose.offsetAndRotation(0.0F, -6.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));


        // ==========================================
        // LEFT LEG (Uses Blockbench's "right" group because of the 180 flip)
        // ==========================================
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        
        // THE MAGIC WRAPPER: Translates Vanilla Hip (1.9, 12, 0) perfectly to Blockbench Floor (0, 24, 0)
        PartDefinition bb_right = left_leg.addOrReplaceChild("bb_right", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));

        // EXACT Blockbench raw export for the Right Leg pasted inside the wrapper
        bb_right.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -6.0F, -1.0F, 4.0F, 12.0F, 4.0F, pantsInflation), PartPose.offsetAndRotation(0.0F, -6.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));

        PartDefinition armor_right = bb_right.addOrReplaceChild("armor_right", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        armor_right.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(26, 10).addBox(-4.4731F, -5.6F, -0.5F, 1.0F, 2.0F, 3.0F, armorInflation), PartPose.offsetAndRotation(0.0F, -6.0F, 1.0F, -3.1416F, 0.0F, 3.098F));
        armor_right.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(16, 16).addBox(2.2F, -1.2F, 0.2F, 1.0F, 1.0F, 4.0F, armorInflation)
        .texOffs(16, 8).addBox(2.2F, 0.0F, 0.2F, 1.0F, 4.0F, 4.0F, armorInflation), PartPose.offsetAndRotation(0.0F, -6.0F, 1.0F, 0.0F, 1.5708F, 0.0F));
        armor_right.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(16, 28).addBox(-4.2F, -1.2F, 0.0F, 1.0F, 1.0F, 3.0F, armorInflation)
        .texOffs(24, 21).addBox(-4.2F, 0.0F, 0.0F, 1.0F, 4.0F, 3.0F, armorInflation), PartPose.offsetAndRotation(0.0F, -6.0F, 1.0F, -3.1416F, 0.0F, 3.1416F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}