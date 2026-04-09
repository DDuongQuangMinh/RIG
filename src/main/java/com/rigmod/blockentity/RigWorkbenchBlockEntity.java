package com.rigmod.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

// FIX: Added MenuProvider to allow a GUI!
public class RigWorkbenchBlockEntity extends BlockEntity implements MenuProvider {
    public float animationProgress = 0.0f;
    public float prevAnimationProgress = 0.0f;

    // FIX: Create an inventory with 3 slots (e.g., 2 inputs, 1 output)
    private final ItemStackHandler itemHandler = new ItemStackHandler(3) {
        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.empty();

    public RigWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RIG_WORKBENCH_BE.get(), pos, state);
    }

    // --- GUI Methods ---
    @Override
    public Component getDisplayName() {
        return Component.literal("RIG Workbench");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        // We will create the RigWorkbenchMenu class in the next step!
        return new com.rigmod.menu.RigWorkbenchMenu(containerId, playerInventory, this); 
    }

    // --- Save/Load Items ---
    @Override
    public void onLoad() {
        super.onLoad();
        lazyItemHandler = LazyOptional.of(() -> itemHandler);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        lazyItemHandler.invalidate();
    }

    @Override
    protected void saveAdditional(CompoundTag tag) {
        tag.put("inventory", itemHandler.serializeNBT());
        super.saveAdditional(tag);
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        itemHandler.deserializeNBT(tag.getCompound("inventory"));
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