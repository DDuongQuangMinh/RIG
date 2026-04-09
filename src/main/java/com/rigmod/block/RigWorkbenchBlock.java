package com.rigmod.block;

import com.rigmod.item.ModItems;
import com.rigmod.blockentity.ModBlockEntities;
import com.rigmod.blockentity.RigWorkbenchBlockEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

// Changed to BaseEntityBlock to support the ticking animation brain!
public class RigWorkbenchBlock extends BaseEntityBlock {

    public RigWorkbenchBlock(Properties properties) {
        super(properties);
    }

    // Tells Minecraft to hide the static JSON block and draw your animated Java Model instead
    @Override
    public RenderShape getRenderShape(BlockState state) {
        return RenderShape.ENTITYBLOCK_ANIMATED;
    }

    // Links this block to your custom BlockEntity class
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new RigWorkbenchBlockEntity(pos, state);
    }

    // Tells the game to execute the 'tick' method every single frame to run the animation math
    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
        return createTickerHelper(type, ModBlockEntities.RIG_WORKBENCH_BE.get(), RigWorkbenchBlockEntity::tick);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
        ItemStack heldItem = player.getItemInHand(hand);

        // ==========================================
        // RECHARGE MECHANIC
        // ==========================================
        if (heldItem.getItem() == ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get()) {
            if (!level.isClientSide()) {
                CompoundTag tag = heldItem.getOrCreateTag();
                int currentFuel = tag.contains("JetpackFuel") ? tag.getInt("JetpackFuel") : 0;
                int maxFuel = 1200;

                if (currentFuel < maxFuel) {
                    // Refill the tank
                    tag.putInt("JetpackFuel", maxFuel);
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("§b[RIG Workbench] Jetpack Fully Recharged!"), true);
                    
                    // Play a high-tech sound effect
                    level.playSound(null, pos, SoundEvents.BEACON_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    
                    return InteractionResult.SUCCESS;
                } else {
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("§a[RIG Workbench] Jetpack is already at 100%."), true);
                    return InteractionResult.CONSUME;
                }
            }
            return InteractionResult.sidedSuccess(level.isClientSide());
        }

        // ==========================================
        // CRAFTING UI (ONLINE)
        // ==========================================
        if (!level.isClientSide()) {
            BlockEntity entity = level.getBlockEntity(pos);
            if (entity instanceof RigWorkbenchBlockEntity) {
                // Opens the GUI for the player!
                net.minecraftforge.network.NetworkHooks.openScreen(((net.minecraft.server.level.ServerPlayer) player), (RigWorkbenchBlockEntity) entity, pos);
            } else {
                throw new IllegalStateException("Our Container provider is missing!");
            }
        }

        return InteractionResult.sidedSuccess(level.isClientSide());
    }
}