package com.rigmod.client.model; // Make sure your package name matches here!

import com.rigmod.RigMod;
import com.rigmod.item.PlasmaCutterItem;
import net.minecraft.resources.ResourceLocation;
import software.bernie.geckolib.model.GeoModel;

public class PlasmaCutterModel extends GeoModel<PlasmaCutterItem> {
    @Override
    public ResourceLocation getModelResource(PlasmaCutterItem animatable) {
        return new ResourceLocation(RigMod.MODID, "geo/item/plasma_cutter.geo.json");
    }

    @Override
    public ResourceLocation getTextureResource(PlasmaCutterItem animatable) {
        return new ResourceLocation(RigMod.MODID, "textures/item/plasma_cutter.png");
    }

    @Override
    public ResourceLocation getAnimationResource(PlasmaCutterItem animatable) {
        // We are changing this to point to the NEW clean animation file!
        return new ResourceLocation("rigmod", "animations/item/plasma_cutter.animation.json");
    }
}