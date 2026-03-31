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
    
    // Defines where the game looks for this specific model layer
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(
            new ResourceLocation(RigMod.MODID, "standard_level_1_helmet"), "main");
            
    // The master bone that holds all your cubes
    private final ModelPart head;

    public StandardLevel1HelmetModel(ModelPart root) {
        super(root);
        this.head = root.getChild("head");
    }

    public static LayerDefinition createBodyLayer() {
        // We use the standard Humanoid mesh to start so it has arms/legs (even if invisible)
        MeshDefinition meshdefinition = HumanoidModel.createMesh(CubeDeformation.NONE, 0.0F);
        PartDefinition partdefinition = meshdefinition.getRoot();

        // Overwrite the default head with your custom Blockbench Head
        PartDefinition Head = partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.0F));

        PartDefinition Front = Head.addOrReplaceChild("Front", CubeListBuilder.create().texOffs(35, 28).mirror().addBox(-5.1716F, -8.25F, -4.5519F, 0.65F, 1.7F, 1.45F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(24, 27).addBox(4.5216F, -8.25F, -3.1519F, 0.65F, 1.1F, 0.75F, new CubeDeformation(0.0F))
        .texOffs(45, 33).addBox(-5.1716F, -8.25F, -3.1519F, 0.65F, 1.1F, 0.75F, new CubeDeformation(0.0F))
        .texOffs(40, 34).addBox(4.5216F, -8.25F, -4.5519F, 0.65F, 1.7F, 1.45F, new CubeDeformation(0.0F))
        .texOffs(3, 35).addBox(-5.1716F, -15.05F, -4.5519F, 0.65F, 1.5F, 1.15F, new CubeDeformation(0.0F))
        .texOffs(7, 36).addBox(4.5216F, -15.05F, -4.5519F, 0.65F, 1.5F, 1.15F, new CubeDeformation(0.0F))
        .texOffs(4, 11).addBox(-5.1716F, -13.25F, -4.5519F, 0.65F, 1.5F, 2.25F, new CubeDeformation(0.0F))
        .texOffs(20, 42).mirror().addBox(4.5216F, -13.25F, -4.5519F, 0.65F, 1.5F, 2.25F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(7, 32).addBox(-4.5F, -15.5F, -4.75F, 9.0F, 8.5F, 0.75F, new CubeDeformation(0.0F))
        .texOffs(18, 16).addBox(-2.5F, -15.5F, -5.7F, 5.0F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(21, 32).mirror().addBox(-2.5F, -13.25F, -5.7F, 5.0F, 1.5F, 0.5F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(42, 38).addBox(-2.5F, -10.75F, -5.7F, 5.0F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(25, 13).addBox(-2.5F, -8.25F, -5.7F, 5.0F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(22, 43).mirror().addBox(4.5216F, -10.75F, -4.5519F, 0.65F, 1.5F, 2.25F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(22, 23).addBox(-5.1716F, -10.75F, -4.5519F, 0.65F, 1.5F, 2.25F, new CubeDeformation(0.0F))
        .texOffs(45, 25).addBox(-3.45F, -15.55F, -5.2F, 6.9F, 0.15F, 1.5F, new CubeDeformation(0.0F))
        .texOffs(43, 45).addBox(1.6F, -16.0383F, -6.1424F, 0.5F, 1.6F, 0.45F, new CubeDeformation(0.0F))
        .texOffs(46, 46).addBox(-2.1F, -13.25F, -6.15F, 0.5F, 0.7F, 0.45F, new CubeDeformation(0.0F))
        .texOffs(46, 46).addBox(1.6F, -13.25F, -6.15F, 0.5F, 0.7F, 0.45F, new CubeDeformation(0.0F))
        .texOffs(34, 15).addBox(-2.1F, -16.0383F, -6.1424F, 0.5F, 1.6F, 0.45F, new CubeDeformation(0.0F))
        .texOffs(17, 46).addBox(-2.1F, -11.25F, -6.15F, 0.5F, 1.2F, 0.45F, new CubeDeformation(0.0F))
        .texOffs(22, 46).addBox(1.6F, -8.25F, -5.95F, 0.5F, 1.2F, 0.25F, new CubeDeformation(0.0F))
        .texOffs(46, 46).addBox(-2.1F, -8.25F, -5.95F, 0.5F, 1.2F, 0.25F, new CubeDeformation(0.0F))
        .texOffs(46, 46).addBox(1.6F, -8.75F, -6.15F, 0.5F, 0.7F, 0.25F, new CubeDeformation(0.0F))
        .texOffs(46, 46).addBox(-2.1F, -8.75F, -6.15F, 0.5F, 0.7F, 0.25F, new CubeDeformation(0.0F))
        .texOffs(46, 46).addBox(1.6F, -7.3F, -6.3F, 0.5F, 0.55F, 0.6F, new CubeDeformation(0.0F))
        .texOffs(46, 46).addBox(-2.1F, -7.3F, -6.3F, 0.5F, 0.55F, 0.6F, new CubeDeformation(0.0F))
        .texOffs(46, 46).addBox(1.6F, -11.75F, -6.15F, 0.5F, 0.7F, 0.25F, new CubeDeformation(0.0F))
        .texOffs(46, 46).addBox(-2.1F, -11.75F, -6.15F, 0.5F, 0.7F, 0.25F, new CubeDeformation(0.0F))
        .texOffs(1, 2).addBox(1.6F, -11.25F, -6.15F, 0.5F, 1.2F, 0.45F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        PartDefinition cube_r1 = Front.addOrReplaceChild("cube_r1", CubeListBuilder.create().texOffs(13, 14).addBox(-1.0F, -0.1F, 1.15F, 0.5F, 0.6F, 0.45F, new CubeDeformation(0.0F))
        .texOffs(46, 10).addBox(2.7F, -0.1F, 1.15F, 0.5F, 0.6F, 0.45F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-1.1F, -16.9403F, -7.0135F, -0.3927F, 0.0F, 0.0F));

        PartDefinition cube_r2 = Front.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(43, 41).addBox(-3.5F, -6.5F, 1.5F, 3.0F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(38, 39).addBox(-1.25F, -12.25F, 1.5F, 0.75F, 0.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(5, 41).mirror().addBox(-3.5F, -13.75F, 1.5F, 3.0F, 1.5F, 0.5F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(42, 40).addBox(-3.5F, -11.5F, 1.5F, 3.0F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(29, 40).addBox(-3.5F, -9.0F, 1.5F, 3.0F, 1.5F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(6.2119F, -1.75F, -5.7655F, 0.0F, -0.3927F, 0.0F));

        PartDefinition cube_r3 = Front.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(41, 42).addBox(-1.5F, -5.5F, 1.5F, 0.75F, 0.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(6, 16).mirror().addBox(-1.5F, -7.0F, 1.5F, 3.0F, 1.5F, 0.5F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(2, 40).mirror().addBox(-1.5F, -4.75F, 1.5F, 3.0F, 1.5F, 0.5F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(31, 29).addBox(-1.5F, -2.25F, 1.5F, 3.0F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(26, 36).addBox(-1.5F, 0.25F, 1.5F, 3.0F, 1.5F, 0.5F, new CubeDeformation(0.0F)), PartPose.offsetAndRotation(-4.3642F, -8.5F, -6.5308F, 0.0F, 0.3927F, 0.0F));

        PartDefinition Back = Head.addOrReplaceChild("Back", CubeListBuilder.create().texOffs(1, 31).addBox(-2.0F, -15.0F, 4.1F, 4.0F, 3.0F, 0.6F, new CubeDeformation(0.0F))
        .texOffs(33, 25).addBox(-2.0F, -15.6F, -4.35F, 4.0F, 0.6F, 8.65F, new CubeDeformation(0.0F))
        .texOffs(1, 1).addBox(-4.0F, -12.0F, 4.0F, 8.0F, 3.5F, 1.4F, new CubeDeformation(0.0F))
        .texOffs(7, 34).mirror().addBox(-5.05F, -11.7F, 0.0F, 1.05F, 3.2F, 5.15F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(29, 35).addBox(3.95F, -11.7F, 0.0F, 1.05F, 3.2F, 5.15F, new CubeDeformation(0.0F))
        .texOffs(2, 3).addBox(-4.0F, -15.0F, 4.0F, 8.0F, 8.0F, 0.65F, new CubeDeformation(0.0F))
        .texOffs(9, 30).addBox(-4.55F, -15.5F, -4.0F, 0.55F, 8.5F, 8.6F, new CubeDeformation(0.0F))
        .texOffs(0, 33).addBox(4.0F, -15.5F, -4.0F, 0.55F, 8.5F, 8.6F, new CubeDeformation(0.0F))
        .texOffs(34, 18).addBox(-4.05F, -15.2F, -4.0F, 8.1F, 0.25F, 8.0F, new CubeDeformation(0.0F))
        .texOffs(37, 3).addBox(-4.0F, -8.5F, 4.0F, 0.5F, 1.5F, 0.9F, new CubeDeformation(0.0F))
        .texOffs(42, 4).mirror().addBox(-3.0F, -8.5F, 4.0F, 0.5F, 1.5F, 0.9F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(1, 5).mirror().addBox(-2.0F, -8.5F, 4.0F, 0.5F, 1.5F, 0.9F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(2, 6).addBox(-1.1F, -8.5F, 4.0F, 0.5F, 1.5F, 0.9F, new CubeDeformation(0.0F))
        .texOffs(6, 7).addBox(3.5F, -8.5F, 4.0F, 0.5F, 1.5F, 0.9F, new CubeDeformation(0.0F))
        .texOffs(44, 8).addBox(0.6F, -8.5F, 4.0F, 0.5F, 1.5F, 0.9F, new CubeDeformation(0.0F))
        .texOffs(13, 9).addBox(1.5F, -8.5F, 4.0F, 0.5F, 1.5F, 0.9F, new CubeDeformation(0.0F))
        .texOffs(16, 10).addBox(2.5F, -8.5F, 4.0F, 0.5F, 1.5F, 0.9F, new CubeDeformation(0.0F))
        .texOffs(21, 11).mirror().addBox(-0.25F, -8.5F, 4.0F, 0.5F, 1.5F, 0.9F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(21, 12).addBox(4.2F, -8.5F, 0.0F, 0.6F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(27, 13).mirror().addBox(-4.8F, -8.5F, 3.0F, 0.5F, 1.5F, 0.5F, new CubeDeformation(0.0F)).mirror(false)
        .texOffs(27, 14).addBox(-4.8F, -8.5F, 2.0F, 0.5F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(30, 15).addBox(-4.8F, -8.5F, 1.0F, 0.5F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(44, 16).addBox(-4.8F, -8.5F, 0.0F, 0.5F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(44, 17).addBox(4.2F, -8.5F, 1.0F, 0.6F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(45, 18).addBox(4.2F, -8.5F, 2.0F, 0.6F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(45, 19).addBox(4.2F, -8.5F, 3.0F, 0.6F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(10, 20).addBox(4.2F, -8.5F, 4.0F, 0.6F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(45, 21).addBox(-4.8F, -8.5F, 4.0F, 0.6F, 1.5F, 0.5F, new CubeDeformation(0.0F))
        .texOffs(33, 26).addBox(2.0F, -16.0F, -4.5F, 2.0F, 1.0F, 8.5F, new CubeDeformation(0.0F))
        .texOffs(33, 27).addBox(-4.0F, -16.0F, -4.5F, 2.0F, 1.0F, 8.5F, new CubeDeformation(0.0F))
        .texOffs(12, 21).addBox(-4.0F, -15.6F, 4.0F, 2.0F, 3.6F, 1.1F, new CubeDeformation(0.0F))
        .texOffs(38, 23).addBox(2.0F, -15.6F, 4.0F, 2.0F, 3.6F, 1.1F, new CubeDeformation(0.0F))
        .texOffs(0, 22).addBox(1.0F, -15.4F, 4.3F, 0.2F, 3.4F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(27, 23).addBox(-1.0F, -15.4F, 4.3F, 0.2F, 3.4F, 0.7F, new CubeDeformation(0.0F))
        .texOffs(34, 34).addBox(1.0F, -15.9F, -3.9F, 0.2F, 0.5F, 8.4F, new CubeDeformation(0.0F))
        .texOffs(34, 34).addBox(-1.0F, -15.9F, -3.9F, 0.2F, 0.5F, 8.4F, new CubeDeformation(0.0F)), PartPose.offset(0.0F, 8.0F, 0.0F));

        return LayerDefinition.create(meshdefinition, 64, 64);
    }

    private LivingEntity currentEntity;

    @Override
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch) {
        super.setupAnim(entity, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch);
        // Store the entity currently being animated
        this.currentEntity = entity; 
    }

    @Override
    public void renderToBuffer(PoseStack poseStack, VertexConsumer vertexConsumer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha) {
        // If the camera is in 1st person AND the helmet belongs to YOU, cancel the render
        if (Minecraft.getInstance().options.getCameraType().isFirstPerson() && this.currentEntity == Minecraft.getInstance().player) {
            return; 
        }
        
        // Otherwise, render it normally (for 3rd person, or if looking at another player)
        head.render(poseStack, vertexConsumer, packedLight, packedOverlay, red, green, blue, alpha);
    }
}