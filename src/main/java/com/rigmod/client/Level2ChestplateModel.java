package com.rigmod.client;

import com.rigmod.RigMod;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelLayerLocation;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class Level2ChestplateModel<T extends LivingEntity> extends HumanoidModel<T> {
    public static final ModelLayerLocation LAYER_LOCATION = new ModelLayerLocation(new ResourceLocation(RigMod.MODID, "level_2_chestplate"), "main");

    public Level2ChestplateModel(ModelPart root) {
        super(root);
    }

    public static LayerDefinition createBodyLayer() {
        MeshDefinition meshdefinition = new MeshDefinition();
        PartDefinition partdefinition = meshdefinition.getRoot();

        partdefinition.addOrReplaceChild("head", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("hat", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("right_leg", CubeListBuilder.create(), PartPose.ZERO);
        partdefinition.addOrReplaceChild("left_leg", CubeListBuilder.create(), PartPose.ZERO);

        // The Three Perfected Inflation Rules
        CubeDeformation armorInflation = new CubeDeformation(0.25F); // Puffs out base to hide skin
        CubeDeformation noInflation = new CubeDeformation(0.0F);     // Keeps ribs crisp and sharp
        CubeDeformation gauntletInflation = new CubeDeformation(0.25F, 0.0F, 0.25F); // Puffs X/Z but keeps Y gaps open

        // ==========================================
        // BASE TORSO & WRAP-AROUNDS (Inflated)
        // ==========================================
        PartDefinition body = partdefinition.addOrReplaceChild("body", CubeListBuilder.create().texOffs(0, 0).addBox(-4.0F, 0.0F, -2.0F, 8.0F, 12.0F, 4.0F, armorInflation), PartPose.ZERO);

        body.addOrReplaceChild("shoulder", CubeListBuilder.create()
            .texOffs(24, 6).addBox(-4.5F, -0.15F, -2.5F, 2.0F, 1.0F, 5.0F, armorInflation)
            .texOffs(0, 32).addBox(2.5F, -0.15F, -2.5F, 2.0F, 1.0F, 5.0F, armorInflation), PartPose.ZERO);

        PartDefinition belt = body.addOrReplaceChild("belt", CubeListBuilder.create()
            .texOffs(24, 0).addBox(-4.0F, 10.0F, -2.25F, 8.0F, 2.0F, 1.0F, armorInflation)
            .texOffs(24, 3).addBox(-4.0F, 10.0F, 1.25F, 8.0F, 2.0F, 1.0F, armorInflation)
            .texOffs(44, 41).addBox(-1.35F, 10.0F, -2.5F, 1.0F, 2.0F, 1.0F, armorInflation)
            .texOffs(42, 44).addBox(0.35F, 10.0F, -2.5F, 1.0F, 2.0F, 1.0F, armorInflation), PartPose.ZERO);

        belt.addOrReplaceChild("cube_r1", CubeListBuilder.create()
            .texOffs(40, 32).addBox(-2.0F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, armorInflation)
            .texOffs(40, 29).addBox(-7.2F, -2.0F, -1.0F, 2.0F, 2.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(3.6F, 12.0795F, -1.4281F, -0.0873F, 0.0F, 0.0F));

        // ==========================================
        // FRONT DETAILS 
        // ==========================================
        PartDefinition front_details = body.addOrReplaceChild("front_details", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -0.35F));

        front_details.addOrReplaceChild("front_armor", CubeListBuilder.create()
            .texOffs(42, 21).addBox(2.0F, 8.6F, -2.4F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(42, 39).addBox(-4.0F, 8.6F, -2.4F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(8, 38).addBox(2.0F, 7.4F, -2.4F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(42, 19).addBox(-4.0F, 7.4F, -2.4F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(22, 42).addBox(2.0F, 6.2F, -2.4F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(42, 37).addBox(-4.0F, 6.2F, -2.4F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(42, 23).addBox(2.0F, 5.0F, -2.4F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(42, 25).addBox(-4.0F, 5.0F, -2.4F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(38, 6).addBox(-2.0F, 5.0F, -2.2F, 4.0F, 1.0F, 1.0F, noInflation)
            .texOffs(38, 8).addBox(-2.0F, 6.2F, -2.2F, 4.0F, 1.0F, 1.0F, noInflation)
            .texOffs(32, 37).addBox(-2.0F, 7.4F, -2.2F, 4.0F, 1.0F, 1.0F, noInflation)
            .texOffs(38, 10).addBox(-2.0F, 8.6F, -2.2F, 4.0F, 1.0F, 1.0F, noInflation), PartPose.ZERO);

        PartDefinition Tablet = front_details.addOrReplaceChild("Tablet", CubeListBuilder.create()
            .texOffs(0, 42).addBox(-1.0F, 2.4F, -2.1F, 2.0F, 2.0F, 1.0F, noInflation)
            .texOffs(0, 42).addBox(-1.0F, 2.4F, -2.8F, 2.0F, 2.0F, 1.0F, noInflation), PartPose.ZERO);

        PartDefinition ipad = Tablet.addOrReplaceChild("ipad", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, -0.7F));
        ipad.addOrReplaceChild("cube_r12", CubeListBuilder.create().texOffs(26, 46).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 0.0F, noInflation), PartPose.offsetAndRotation(1.5412F, 3.7467F, -0.9685F, 0.6931F, -0.3085F, -0.247F));
        ipad.addOrReplaceChild("cube_r13", CubeListBuilder.create().texOffs(12, 46).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 0.0F, noInflation), PartPose.offsetAndRotation(-1.5412F, 3.7467F, -0.9685F, 0.6931F, 0.3085F, 0.247F));
        ipad.addOrReplaceChild("cube_r14", CubeListBuilder.create().texOffs(8, 46).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 0.0F, noInflation), PartPose.offsetAndRotation(0.0F, 3.9F, -1.234F, 0.6545F, 0.0F, 0.0F));

        // ==========================================
        // BACK DETAILS 
        // ==========================================
        PartDefinition back_details = body.addOrReplaceChild("back_details", CubeListBuilder.create(), PartPose.offset(0.0F, 0.0F, 0.35F));
        PartDefinition spine = back_details.addOrReplaceChild("spine", CubeListBuilder.create(), PartPose.ZERO);

        PartDefinition RIG = spine.addOrReplaceChild("RIG", CubeListBuilder.create().texOffs(4, 46).addBox(-0.5F, 10.0F, 1.7F, 1.0F, 2.0F, 1.0F, noInflation), PartPose.ZERO);
        RIG.addOrReplaceChild("cube_r2", CubeListBuilder.create().texOffs(42, 0).addBox(0.0F, -2.0F, -1.0F, 1.0F, 1.0F, 2.0F, noInflation), PartPose.offsetAndRotation(-1.0F, 4.5F, 1.6F, 0.0F, 0.0F, -0.7854F));
        RIG.addOrReplaceChild("cube_r3", CubeListBuilder.create().texOffs(0, 45).addBox(0.0F, -2.0F, 0.0F, 1.0F, 2.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-0.5F, 10.6536F, 1.6472F, -0.1309F, 0.0F, 0.0F));
        RIG.addOrReplaceChild("cube_r4", CubeListBuilder.create().texOffs(38, 44).addBox(0.0F, -6.0F, 0.0F, 1.0F, 2.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-0.5F, 6.9742F, 3.5723F, 0.2182F, 0.0F, 0.0F));
        RIG.addOrReplaceChild("cube_r5", CubeListBuilder.create().texOffs(18, 40).addBox(0.0F, -4.0F, 0.0F, 1.0F, 6.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-0.5F, 6.6878F, 2.1693F, -0.1309F, 0.0F, 0.0F));

        back_details.addOrReplaceChild("armor2", CubeListBuilder.create()
            .texOffs(44, 14).addBox(2.0F, 8.9F, 1.2F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(44, 12).addBox(-4.0F, 8.9F, 1.2F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(32, 44).addBox(2.0F, 7.7F, 1.2F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(44, 16).addBox(-4.0F, 7.7F, 1.2F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(12, 44).addBox(2.0F, 6.5F, 1.2F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(6, 44).addBox(-4.0F, 6.5F, 1.2F, 2.0F, 1.0F, 1.0F, noInflation)
            .texOffs(8, 40).addBox(-2.0F, 6.5F, 1.4F, 4.0F, 1.0F, 1.0F, noInflation)
            .texOffs(40, 27).addBox(-2.0F, 7.7F, 1.4F, 4.0F, 1.0F, 1.0F, noInflation)
            .texOffs(32, 39).addBox(-2.0F, 8.9F, 1.4F, 4.0F, 1.0F, 1.0F, noInflation), PartPose.ZERO);

        PartDefinition RIG_backpack = back_details.addOrReplaceChild("RIG_backpack", CubeListBuilder.create(), PartPose.ZERO);
        RIG_backpack.addOrReplaceChild("cube_r6", CubeListBuilder.create().texOffs(22, 44).addBox(0.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, noInflation), PartPose.offsetAndRotation(2.6F, 4.1F, 1.1F, 0.0F, 0.0F, -0.3054F));
        RIG_backpack.addOrReplaceChild("cube_r7", CubeListBuilder.create().texOffs(28, 42).addBox(-1.0F, -3.0F, 0.0F, 1.0F, 3.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-2.6F, 4.1F, 1.1F, 0.0F, 0.0F, 0.3054F));
        RIG_backpack.addOrReplaceChild("cube_r8", CubeListBuilder.create().texOffs(24, 37).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, noInflation), PartPose.offsetAndRotation(-0.8578F, 4.4775F, 1.8609F, 0.1175F, -0.3309F, 0.1818F));
        RIG_backpack.addOrReplaceChild("cube_r9", CubeListBuilder.create().texOffs(32, 32).addBox(-1.0F, -3.0F, -1.0F, 2.0F, 3.0F, 2.0F, noInflation), PartPose.offsetAndRotation(0.8586F, 4.4422F, 1.8623F, 0.1175F, 0.3309F, -0.1818F));
        RIG_backpack.addOrReplaceChild("cube_r10", CubeListBuilder.create().texOffs(38, 41).addBox(0.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, noInflation), PartPose.offsetAndRotation(-1.2007F, 5.959F, 1.5347F, -0.0026F, -0.3609F, -0.2747F));
        RIG_backpack.addOrReplaceChild("cube_r11", CubeListBuilder.create().texOffs(32, 41).addBox(-2.0F, -2.0F, 0.0F, 2.0F, 2.0F, 1.0F, noInflation), PartPose.offsetAndRotation(1.2507F, 5.9424F, 1.5277F, -0.0071F, 0.3608F, 0.2469F));

        // ==========================================
        // RIGHT ARM & 3D SHOULDER PAD
        // ==========================================
        PartDefinition right_arm = partdefinition.addOrReplaceChild("right_arm", CubeListBuilder.create().texOffs(0, 16).addBox(-3.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, armorInflation), PartPose.offset(-5.0F, 2.0F, 0.0F));
        
        // SHOULDER FIX: Removed X offset completely, applied armorInflation so it scales seamlessly with the sleeve
        PartDefinition armor = right_arm.addOrReplaceChild("armor", CubeListBuilder.create(), PartPose.ZERO);
        armor.addOrReplaceChild("cube_r15", CubeListBuilder.create()
            .texOffs(32, 27).addBox(-1.0F, -2.0F, 0.0F, 3.0F, 4.0F, 1.0F, armorInflation)
            .texOffs(32, 12).addBox(-1.0F, -2.0F, 0.65F, 3.0F, 4.0F, 3.0F, armorInflation)
            .texOffs(24, 32).addBox(-1.0F, -2.0F, 3.25F, 3.0F, 4.0F, 1.0F, armorInflation), PartPose.offsetAndRotation(-2.25F, -0.25F, -2.1F, 0.0F, 0.0F, 0.0436F));

        // GAUNTLET FIX: Restored Y to 7.5F to get your perfect "little gap" back
        right_arm.addOrReplaceChild("hand_armor", CubeListBuilder.create()
            .texOffs(14, 32).addBox(-3.3F, 3.0F, -2.0F, 1.0F, 4.0F, 4.0F, gauntletInflation)
            .texOffs(40, 35).addBox(-3.3F, 3.0F, -2.2F, 2.0F, 1.0F, 1.0F, gauntletInflation)
            .texOffs(42, 3).addBox(-3.3F, 3.0F, 1.2F, 2.0F, 1.0F, 1.0F, gauntletInflation)
            .texOffs(24, 12).addBox(-3.25F, 7.5F, -1.0F, 2.0F, 2.0F, 2.0F, gauntletInflation), PartPose.ZERO);

        // ==========================================
        // LEFT ARM 
        // ==========================================
        PartDefinition left_arm = partdefinition.addOrReplaceChild("left_arm", CubeListBuilder.create().texOffs(16, 16).addBox(-1.0F, -2.0F, -2.0F, 4.0F, 12.0F, 4.0F, armorInflation), PartPose.offset(5.0F, 2.0F, 0.0F));

        // GAUNTLET FIX: Restored Y to 7.5F to get your perfect "little gap" back
        left_arm.addOrReplaceChild("hand_armor2", CubeListBuilder.create()
            .texOffs(32, 19).addBox(2.3F, 3.0F, -2.0F, 1.0F, 4.0F, 4.0F, gauntletInflation)
            .texOffs(0, 38).addBox(1.25F, 7.5F, -1.0F, 2.0F, 2.0F, 2.0F, gauntletInflation)
            .texOffs(6, 42).addBox(1.3F, 3.0F, -2.2F, 2.0F, 1.0F, 1.0F, gauntletInflation)
            .texOffs(12, 42).addBox(1.3F, 3.0F, 1.2F, 2.0F, 1.0F, 1.0F, gauntletInflation), PartPose.ZERO);

        return LayerDefinition.create(meshdefinition, 64, 64);
    }
}