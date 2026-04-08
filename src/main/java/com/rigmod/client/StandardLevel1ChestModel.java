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

public class StandardLevel1ChestModel<T extends LivingEntity> extends HumanoidModel<T> {
    
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "standard_level_1_chest"), "main");

    public StandardLevel1ChestModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        // TARGETED INFLATION RULES
        CubeDeformation armorInflation = new CubeDeformation(0.25F); 
        CubeDeformation gauntletInflation = new CubeDeformation(0.25F, 0.0F, 0.25F);
        
        // PERFECT TABLET FIX: 0 Width/Height inflation (so it stays sleek), but 0.3 Depth inflation (so it gets thick)
        CubeDeformation tabletBaseInflation = new CubeDeformation(0.0F, 0.0F, 0.3F);
        CubeDeformation noInflation = new CubeDeformation(0.0F);

        // ==========================================
        // TORSO & BELT
        // ==========================================
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);

        body.addOrReplaceChild("torso", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, armorInflation), PartPose.ZERO);

        PartDefinition chest_tablet = body.addOrReplaceChild("chest_tablet", CubeListBuilder.create()
            .texOffs(36, 10).addBox(-1.0F, 3.0F, -2.25F, 2.0F, 2.0F, 1.0F, tabletBaseInflation), PartPose.ZERO);

        // SCREEN FIX: Pushes the glowing screens forward perfectly to match the 0.3 thickness of the base!
        PartDefinition tablet_screens = chest_tablet.addOrReplaceChild("tablet_screens", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -0.3F));

        tablet_screens.addOrReplaceChild("cube_r1", CubeListBuilder.create()
            .texOffs(34, 39).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 0.0F, noInflation), 
            PartPose.offsetAndRotation(1.5977F, 4.6685F, -1.0129F, 0.3695F, -0.3272F, -0.1238F));

        tablet_screens.addOrReplaceChild("cube_r2", CubeListBuilder.create()
            .texOffs(30, 39).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 0.0F, noInflation), 
            PartPose.offsetAndRotation(-1.5977F, 4.6685F, -1.0129F, 0.3695F, 0.3272F, 0.1238F));

        tablet_screens.addOrReplaceChild("cube_r3", CubeListBuilder.create()
            .texOffs(26, 39).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 0.0F, noInflation), 
            PartPose.offsetAndRotation(0.0F, 4.7414F, -1.285F, 0.3491F, 0.0F, 0.0F));

        // ==========================================
        // BACKPACK
        // ==========================================
        PartDefinition Back = body.addOrReplaceChild("Back", CubeListBuilder.create(), PartPose.ZERO);

        Back.addOrReplaceChild("cube_r4", CubeListBuilder.create()
            .texOffs(16, 39).addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(-3.7F, 4.0F, 1.1F, 0.0F, 0.0F, 0.2182F));
        Back.addOrReplaceChild("cube_r5", CubeListBuilder.create()
            .texOffs(12, 39).addBox(-1.0F, -2.0F, 0.0F, 1.0F, 3.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(3.4F, 3.0F, 1.1F, 0.0F, 0.0F, -0.2182F));
        Back.addOrReplaceChild("cube_r6", CubeListBuilder.create()
            .texOffs(8, 39).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(-1.9576F, 6.639F, 1.1F, 0.0F, 0.0F, -0.2618F));
        Back.addOrReplaceChild("cube_r7", CubeListBuilder.create()
            .texOffs(4, 39).addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(1.8741F, 6.6153F, 1.1F, 0.0F, 0.0F, 0.2618F));
        Back.addOrReplaceChild("cube_r8", CubeListBuilder.create()
            .texOffs(38, 32).addBox(-1.0F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(-1.0F, 3.7114F, 2.6615F, 0.1355F, -0.2595F, -0.035F));
        Back.addOrReplaceChild("cube_r9", CubeListBuilder.create()
            .texOffs(38, 28).addBox(-1.0F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(1.9659F, 3.7452F, 2.4049F, 0.1355F, 0.2595F, 0.035F));
        Back.addOrReplaceChild("cube_r10", CubeListBuilder.create()
            .texOffs(20, 36).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(0.0F, 3.7114F, 2.6615F, 0.1309F, 0.0F, 0.0F));
        Back.addOrReplaceChild("cube_r11", CubeListBuilder.create()
            .texOffs(24, 13).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 2.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(0.0F, 9.7F, 2.15F, -0.0873F, 0.0F, 0.0F));
        Back.addOrReplaceChild("cube_r12", CubeListBuilder.create()
            .texOffs(36, 6).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(0.0F, 6.7F, 2.4F, -0.0873F, 0.0F, 0.0F));

        PartDefinition spine = Back.addOrReplaceChild("spine", CubeListBuilder.create()
            .texOffs(0, 39).addBox(-0.5F, 8.6886F, 1.4385F, 1.0F, 3.0F, 1.0F, armorInflation), PartPose.ZERO);

        spine.addOrReplaceChild("cube_r13", CubeListBuilder.create()
            .texOffs(40, 13).addBox(0.0F, -4.0F, 0.0F, 1.0F, 1.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(0.7F, 6.3886F, 1.6385F, 0.0436F, -0.2182F, -0.7854F));
        spine.addOrReplaceChild("cube_r14", CubeListBuilder.create()
            .texOffs(38, 20).addBox(0.0F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(-0.5F, 9.6886F, 2.4385F, -0.0873F, 0.0F, 0.0F));
        spine.addOrReplaceChild("cube_r15", CubeListBuilder.create()
            .texOffs(38, 36).addBox(0.0F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(-0.5F, 3.7114F, 2.9615F, 0.1745F, 0.0F, 0.0F));
        spine.addOrReplaceChild("cube_r16", CubeListBuilder.create()
            .texOffs(38, 24).addBox(0.0F, -3.0F, -1.0F, 1.0F, 3.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(-0.5F, 6.7F, 2.7F, -0.0873F, 0.0F, 0.0F));

        body.addOrReplaceChild("belt", CubeListBuilder.create()
            .texOffs(24, 0).addBox(-4.0F, 10.0F, -2.2F, 8.0F, 2.0F, 1.0F, armorInflation)
            .texOffs(24, 3).addBox(-4.0F, 10.0F, 1.2F, 8.0F, 2.0F, 1.0F, armorInflation)
            .texOffs(26, 36).addBox(-3.0F, 9.9F, -2.4F, 2.0F, 2.0F, 1.0F, armorInflation)
            .texOffs(32, 36).addBox(1.0F, 9.9F, -2.4F, 2.0F, 2.0F, 1.0F, armorInflation), PartPose.ZERO);

        // ==========================================
        // RIGHT ARM 
        // ==========================================
        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create()
            .texOffs(0, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, armorInflation), PartPose.offset(-5.0F, 2.0F, 0.0F));

        PartDefinition right_armor = right_arm.addOrReplaceChild("right_armor", CubeListBuilder.create()
            .texOffs(12, 32).addBox(-3.2F, 2.6F, -1.5F, 1.0F, 4.0F, 3.0F, armorInflation), PartPose.ZERO);

        right_armor.addOrReplaceChild("cube_r17", CubeListBuilder.create()
            .texOffs(32, 20).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 3.0F, 1.0F, armorInflation)
            .texOffs(20, 32).addBox(-1.0F, -2.0F, -5.4F, 2.0F, 3.0F, 1.0F, armorInflation)
            .texOffs(24, 6).addBox(-1.0F, -2.0F, -5.2F, 2.0F, 3.0F, 4.0F, armorInflation), 
            PartPose.offsetAndRotation(-2.3F, -0.2F, 3.2F, 0.0F, 0.0F, 0.0873F));

        right_arm.addOrReplaceChild("right_palm_armor", CubeListBuilder.create()
            .texOffs(32, 28).addBox(-3.2F, 7.3F, -1.0F, 1.0F, 2.0F, 2.0F, gauntletInflation), PartPose.ZERO);

        // ==========================================
        // LEFT ARM 
        // ==========================================
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create()
            .texOffs(16, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, armorInflation), PartPose.offset(5.0F, 2.0F, 0.0F));

        PartDefinition left_armor = left_arm.addOrReplaceChild("left_armor", CubeListBuilder.create()
            .texOffs(32, 13).addBox(2.2F, 2.6F, -1.5F, 1.0F, 4.0F, 3.0F, armorInflation), PartPose.ZERO);

        left_armor.addOrReplaceChild("cube_r18", CubeListBuilder.create()
            .texOffs(0, 32).addBox(-1.0F, -2.0F, -2.0F, 2.0F, 3.0F, 4.0F, armorInflation)
            .texOffs(32, 24).addBox(-1.0F, -2.0F, -2.2F, 2.0F, 3.0F, 1.0F, armorInflation)
            .texOffs(26, 32).addBox(-1.0F, -2.0F, 1.2F, 2.0F, 3.0F, 1.0F, armorInflation), 
            PartPose.offsetAndRotation(2.3F, -0.2F, 0.0F, 0.0F, 0.0F, -0.0873F));

        left_arm.addOrReplaceChild("left_palm_armor", CubeListBuilder.create()
            .texOffs(32, 32).addBox(2.2F, 7.3F, -1.0F, 1.0F, 2.0F, 2.0F, gauntletInflation), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}