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

        // Dummy parts to prevent animation crashes
        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);

        // THE FIX: Expand the pants in all directions, but ONLY expand the armor horizontally! 
        // This stops Z-fighting but stops the plates from growing vertically into each other and eating the gap!
        CubeDeformation pantsDef = new CubeDeformation(0.25F); 
        CubeDeformation armorDef = new CubeDeformation(0.26F, 0.0F, 0.26F); 

        // ==========================================
        // RIGHT LEG
        // ==========================================
        PartDefinition right_leg = partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));
        
        // Wrapper translates Minecraft Hip (-1.9, 12, 0) to your Blockbench Origin (0, 24, 0)
        PartDefinition bb_right2 = right_leg.addOrReplaceChild("bb_right2", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));

        // YOUR EXACT BLOCKBENCH EXPORT
        bb_right2.addOrReplaceChild("base_pants_right", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, pantsDef), PartPose.ZERO);

        PartDefinition armor_right2 = bb_right2.addOrReplaceChild("armor_right2", CubeListBuilder.create()
            .texOffs(16, 0).addBox(-4.0F, -5.0F, -2.2F, 3.0F, 3.0F, 1.0F, armorDef)
            .texOffs(16, 18).addBox(-4.0F, -6.2F, -2.2F, 3.0F, 1.0F, 1.0F, armorDef)
            .texOffs(16, 13).addBox(-4.2F, -5.0F, -2.2F, 1.0F, 3.0F, 2.0F, armorDef)
            .texOffs(22, 22).addBox(-4.2F, -6.2F, -2.2F, 1.0F, 1.0F, 2.0F, armorDef), PartPose.ZERO);

        armor_right2.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(22, 8).addBox(0.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, armorDef), PartPose.offsetAndRotation(-4.5F, -9.0F, 0.0F, 0.0F, 0.0F, 0.0873F));


        // ==========================================
        // LEFT LEG
        // ==========================================
        PartDefinition left_leg = partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.offset(1.9F, 12.0F, 0.0F));
        
        // Wrapper translates Minecraft Hip (1.9, 12, 0) to your Blockbench Origin (0, 24, 0)
        PartDefinition bb_left = left_leg.addOrReplaceChild("bb_left", CubeListBuilder.create(), PartPose.offset(-1.9F, 12.0F, 0.0F));

        // YOUR EXACT BLOCKBENCH EXPORT
        bb_left.addOrReplaceChild("base_pants_left", CubeListBuilder.create().texOffs(0, 16).addBox(0.0F, -12.0F, -2.0F, 4.0F, 12.0F, 4.0F, pantsDef), PartPose.ZERO);

        PartDefinition armor_left = bb_left.addOrReplaceChild("armor_left", CubeListBuilder.create()
            .texOffs(16, 4).addBox(1.0F, -5.0F, -2.2F, 3.0F, 3.0F, 1.0F, armorDef)
            .texOffs(16, 20).addBox(1.0F, -6.2F, -2.2F, 3.0F, 1.0F, 1.0F, armorDef)
            .texOffs(16, 8).addBox(3.2F, -5.0F, -2.2F, 1.0F, 3.0F, 2.0F, armorDef)
            .texOffs(16, 22).addBox(3.2F, -6.2F, -2.2F, 1.0F, 1.0F, 2.0F, armorDef), PartPose.ZERO);

        armor_left.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(22, 12).addBox(-1.0F, -2.0F, -1.0F, 1.0F, 2.0F, 2.0F, armorDef), PartPose.offsetAndRotation(4.5F, -9.0F, 0.0F, 0.0F, 0.0F, -0.0873F));

        return LayerDefinition.create(meshdefinition, 32, 32);
    }
}