package com.rigmod.client;

import com.rigmod.RigMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class Level2HelmetModel<T extends LivingEntity> extends HumanoidModel<T> {

    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "level_2_helmet"), "main");

    public Level2HelmetModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Empty vanilla parts to prevent visual conflicts
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        // Vanilla Head Anchor
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);

        // We attach your Blockbench Helmet directly to the vanilla head bone
        PartDefinition Helmet = head.addOrReplaceChild("Helmet", CubeListBuilder.create()
                .texOffs(0, 0).addBox(-4.0F, -24.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition Front = Helmet.addOrReplaceChild("Front", CubeListBuilder.create()
                .texOffs(0, 25).addBox(-4.0F, -4.0F, 4.0F, 8.0F, 4.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(0, 30).addBox(-4.0F, -6.0F, 4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 29).addBox(-4.0F, -8.0F, 4.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(32, 13).addBox(-4.25F, -6.0F, 3.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(16, 38).addBox(3.25F, -6.0F, 3.0F, 1.0F, 1.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(32, 6).addBox(-4.25F, -6.0F, 2.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(10, 32).addBox(3.25F, -6.0F, 2.0F, 1.0F, 5.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(36, 25).addBox(-4.25F, -6.25F, 1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(0, 38).addBox(2.25F, -6.25F, 1.0F, 2.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(38, 6).addBox(-4.25F, -4.0F, 3.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(38, 11).addBox(3.25F, -4.0F, 3.0F, 1.0F, 3.0F, 2.0F, new CubeDeformation(0.0F))
                .texOffs(22, 38).addBox(3.0F, -7.0F, 3.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(26, 38).addBox(-4.0F, -7.0F, 3.25F, 1.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, -16.0F, 0.0F));

        PartDefinition Top = Helmet.addOrReplaceChild("Top", CubeListBuilder.create()
                .texOffs(0, 16).addBox(-3.0F, -24.25F, -3.5F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F))
                .texOffs(20, 16).addBox(1.0F, -24.25F, -3.5F, 2.0F, 1.0F, 8.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition back = Helmet.addOrReplaceChild("back", CubeListBuilder.create()
                .texOffs(18, 25).addBox(-4.0F, -22.0F, -4.25F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
                .texOffs(18, 31).addBox(-4.25F, -22.0F, -4.25F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(28, 31).addBox(3.25F, -22.0F, -4.25F, 1.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(0, 32).addBox(-4.25F, -20.5F, -3.5F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F))
                .texOffs(32, 0).addBox(3.25F, -20.5F, -3.5F, 1.0F, 2.0F, 4.0F, new CubeDeformation(0.0F)), 
                PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}