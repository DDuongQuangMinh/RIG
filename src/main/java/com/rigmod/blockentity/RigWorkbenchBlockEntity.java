package com.rigmod.blockentity;

import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class RigWorkbenchBlockEntity extends BlockEntity implements MenuProvider {
    public float animationProgress = 0.0f;
    public float prevAnimationProgress = 0.0f;
    
    public int displayMode = -1; 

    // ==========================================
    // 🛑 THE MACHINE'S INVENTORY & THE BOUNCER
    // ==========================================
    public final ItemStackHandler itemHandler = new ItemStackHandler(2) {
        @Override
        public boolean isItemValid(int slot, @NotNull ItemStack stack) {
            // CHANGED TO SLOT 1 (The Armor Tab)
            if (slot == 1) {
                if (stack.getItem() instanceof Custom3DArmorItem armorItem) {
                    return armorItem.getArmorLevel() == 2; 
                }
                return false; 
            }
            return super.isItemValid(slot, stack); 
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
            if (level != null && !level.isClientSide()) {
                level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), 3);
            }
        }
    };

    private LazyOptional<ItemStackHandler> lazyItemHandler = LazyOptional.empty();

    public RigWorkbenchBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.RIG_WORKBENCH_BE.get(), pos, state);
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        if (cap == ForgeCapabilities.ITEM_HANDLER) {
            return lazyItemHandler.cast();
        }
        return super.getCapability(cap, side);
    }

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

    @Override
    protected void saveAdditional(CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("DisplayMode", this.displayMode);
        tag.put("inventory", itemHandler.serializeNBT()); 
    }

    @Override
    public void load(CompoundTag tag) {
        super.load(tag);
        this.displayMode = tag.getInt("DisplayMode");
        itemHandler.deserializeNBT(tag.getCompound("inventory")); 
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

    // ==========================================
    // THE TICKER & EJECTOR SEAT
    // ==========================================
    public static void tick(Level level, BlockPos pos, BlockState state, RigWorkbenchBlockEntity entity) {
        entity.prevAnimationProgress = entity.animationProgress;
        boolean isPlayerNear = !level.getEntitiesOfClass(Player.class, new AABB(pos).inflate(2.0)).isEmpty();

        if (isPlayerNear && entity.animationProgress < 1.0f) {
            entity.animationProgress += 0.05f; 
        } else if (!isPlayerNear && entity.animationProgress > 0.0f) {
            entity.animationProgress -= 0.05f; 
        }
        entity.animationProgress = Mth.clamp(entity.animationProgress, 0.0f, 1.0f);

        // 🛑 SERVER-SIDE EJECTOR SEAT
        if (!level.isClientSide()) {
            // LOOKS AT SLOT 1 (The Armor Tab)
            ItemStack armorStack = entity.itemHandler.getStackInSlot(1);
            
            if (!armorStack.isEmpty()) {
                if (!(armorStack.getItem() instanceof Custom3DArmorItem armorItem) || armorItem.getArmorLevel() != 2) {
                    if (entity.displayMode != -1) {
                        entity.setDisplayMode(-1); 
                    }
                    return; 
                }
            }
        }
    }
}