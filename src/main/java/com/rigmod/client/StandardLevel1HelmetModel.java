package com.rigmod.client;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.rigmod.RigMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class StandardLevel1HelmetModel<T extends LivingEntity> extends HumanoidModel<T> {
    
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "standard_level_1_helmet"), "main");
    
    private final ModelPart Helmet;
    private final ModelPart Front;
    private final ModelPart front_armor;
    private final ModelPart back;
    private final ModelPart back_armor;
    private final ModelPart Top;

    private LivingEntity currentEntity;

    public StandardLevel1HelmetModel(ModelPart root) {
        super(root);
        this.Helmet = root.getChild("head").getChild("Helmet");
        this.Front = this.Helmet.getChild("Front");
        this.front_armor = this.Front.getChild("front_armor");
        this.back = this.Helmet.getChild("back");
        this.back_armor = this.back.getChild("back_armor");
        this.Top = this.Helmet.getChild("Top");
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        // 1. Define standard humanoid parts
        PartDefinition head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("body", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        // ==========================================
        // 100% PURE BLOCKBENCH GEOMETRY. NO DEFORMATIONS.
        // ==========================================
        PartDefinition Helmet = head.addOrReplaceChild("Helmet", CubeListBuilder.create()
            .texOffs(0, 0).addBox(-4.0F, -32.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F)), 
            PartPose.offset(0.0F, 24.0F, 0.0F));

        PartDefinition Front = Helmet.addOrReplaceChild("Front", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        
        Front.addOrReplaceChild("front_armor", CubeListBuilder.create()
            .texOffs(18, 24).addBox(-4.0F, -26.0F, -5.0F, 8.0F, 2.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(18, 27).addBox(-4.0F, -28.05F, -5.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 28).addBox(-4.0F, -29.75F, -5.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(18, 29).addBox(-4.0F, -32.0F, -5.0F, 8.0F, 1.0F, 1.0F, new CubeDeformation(0.0F)), 
            PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition back = Helmet.addOrReplaceChild("back", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));
        
        back.addOrReplaceChild("back_armor", CubeListBuilder.create()
            .texOffs(0, 24).addBox(-4.0F, -29.5F, 3.4F, 8.0F, 3.0F, 1.0F, new CubeDeformation(0.0F))
            .texOffs(0, 30).addBox(2.4F, -29.5F, 0.4F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(12, 31).addBox(-4.4F, -29.5F, 0.4F, 2.0F, 3.0F, 4.0F, new CubeDeformation(0.0F))
            .texOffs(30, 31).addBox(3.4F, -27.5F, -0.6F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F))
            .texOffs(24, 31).addBox(-4.4F, -27.5F, -0.6F, 1.0F, 2.0F, 2.0F, new CubeDeformation(0.0F)), 
            PartPose.offset(0.0F, 0.0F, 0.0F));

        Helmet.addOrReplaceChild("Top", CubeListBuilder.create()
            .texOffs(0, 16).addBox(1.0F, -32.3F, -3.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F))
            .texOffs(18, 16).addBox(-3.0F, -32.3F, -3.5F, 2.0F, 1.0F, 7.0F, new CubeDeformation(0.0F)), 
            PartPose.offset(0.0F, 0.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        this.currentEntity = entity;
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && this.currentEntity == Minecraft.getInstance().player) {
            return; 
        }
        
        poseStack.pushPose();
        
        // THE PERFECT FIX: Matrix Scaling
        // This expands the entire helmet together by 2% to perfectly hide the player's head skin.
        // Because it scales via rendering instead of math-boxes, your textures will never bleed, 
        // and your exact Blockbench gaps and depth will remain flawless!
        poseStack.scale(1.02F, 1.02F, 1.02F);
        
        this.head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
        
        poseStack.popPose();
    }
}