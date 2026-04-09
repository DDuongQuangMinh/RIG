package com.rigmod.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

public class RigWorkbenchBlockEntity extends BlockEntity implements MenuProvider {
    public float animationProgress = 0.0f;
    public float prevAnimationProgress = 0.0f;
    
    // NEW: Hologram Tracker (-1 = Offline/Closed, 0 = Crafting, 1 = Recharging)
    public int displayMode = -1; 

    public RigWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RIG_WORKBENCH_BE.get(), pos, state);
    }

    // NEW: Safely updates the mode and tells the Server to update all players!
    public void setDisplayMode(int mode) {
        this.displayMode = mode;
        setChanged();
        if (level != null && !level.isClientSide()) {
            level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
        }
    }

    @Override
    public Component getDisplayName() {
        return Component.literal("RIG Workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new com.rigmod.menu.RigWorkbenchMenu(containerId, playerInventory, this);
    }

    // ==========================================
    // MULTIPLAYER SYNCING (Crucial for Holograms)
    // ==========================================
    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("DisplayMode", this.displayMode);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.displayMode = tag.getInt("DisplayMode");
    }

    @Override
    public CompoundTag getUpdateTag() {
        CompoundTag tag = new CompoundTag();
        saveAdditional(tag);
        return tag;
    }

    @Nullable
    @Override
    public Packet<ClientGamePacketListener> getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    // --- Animation Ticker ---
    public static void tick(Level level, BlockPos pos, BlockState state, RigWorkbenchBlockEntity entity) {
        entity.prevAnimationProgress = entity.animationProgress;
        boolean isPlayerNear = !level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(2.0)).isEmpty();

        if (isPlayerNear && entity.animationProgress < 1.0f) {
            entity.animationProgress += 0.05f; 
        } else if (!isPlayerNear && entity.animationProgress > 0.0f) {
            entity.animationProgress -= 0.05f; 
        }
        entity.animationProgress = Mth.clamp(entity.animationProgress, 0.0f, 1.0f);
    }
}