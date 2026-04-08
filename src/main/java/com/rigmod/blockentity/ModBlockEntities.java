package com.rigmod.blockentity;

import com.rigmod.RigMod;
import com.rigmod.block.ModBlocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, RigMod.MODID);

    public static final RegistryObject<BlockEntityType<RigWorkbenchBlockEntity>> RIG_WORKBENCH_BE =
            BLOCK_ENTITIES.register("rig_workbench_be", () ->
                    BlockEntityType.Builder.of(RigWorkbenchBlockEntity::new, ModBlocks.RIG_WORKBENCH.get()).build(null));

    public static void register(IEventBus eventBus) {
        BLOCK_ENTITIES.register(eventBus);
    }
}