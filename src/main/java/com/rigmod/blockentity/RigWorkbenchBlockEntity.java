package com.rigmod.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class RigWorkbenchBlockEntity extends BlockEntity {
    public float animationProgress = 0.0f;
    public float prevAnimationProgress = 0.0f;

    public RigWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RIG_WORKBENCH_BE.get(), pos, state);
    }

    public static void tick(Level level, BlockPos pos, BlockState state, RigWorkbenchBlockEntity entity) {
        entity.prevAnimationProgress = entity.animationProgress;
        
        // FIX: Changed inflate(5.0) to inflate(2.0) to reduce the scan range to 2 blocks
        boolean isPlayerNear = !level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(2.0)).isEmpty();

        // Smoothly adjust the animation progress
        if (isPlayerNear && entity.animationProgress < 1.0f) {
            entity.animationProgress += 0.05f; // Opening speed
        } else if (!isPlayerNear && entity.animationProgress > 0.0f) {
            entity.animationProgress -= 0.05f; // Closing speed
        }
        
        entity.animationProgress = Mth.clamp(entity.animationProgress, 0.0f, 1.0f);
    }
}