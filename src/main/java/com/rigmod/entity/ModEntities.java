package com.rigmod.entity;

import com.rigmod.RigMod;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModEntities {
    public static final DeferredRegister<EntityType<?>> ENTITIES = DeferredRegister.create(ForgeRegistries.ENTITY_TYPES, RigMod.MODID);

    public static final RegistryObject<EntityType<PlasmaBulletEntity>> PLASMA_BULLET = ENTITIES.register("plasma_bullet",
            () -> EntityType.Builder.<PlasmaBulletEntity>of(PlasmaBulletEntity::new, MobCategory.MISC)
                    .sized(0.5f, 0.5f) // The size of the hitbox
                    .clientTrackingRange(4)
                    .updateInterval(20)
                    .build("plasma_bullet"));

    public static void register(IEventBus eventBus) {
        ENTITIES.register(eventBus);
    }
}