package com.rigmod.menu;

import com.rigmod.block.ModBlocks;
import com.rigmod.blockentity.RigWorkbenchBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot; // Make sure this is imported!
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public class RigWorkbenchMenu extends AbstractContainerMenu {
    public final RigWorkbenchBlockEntity blockEntity;
    public final Level level;

    public RigWorkbenchMenu(int id, Inventory inv, FriendlyByteBuf extraData) {
        this(id, inv, inv.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public RigWorkbenchMenu(int id, Inventory inv, BlockEntity entity) {
        super(ModMenuTypes.RIG_WORKBENCH_MENU.get(), id);
        this.blockEntity = (RigWorkbenchBlockEntity) entity;
        this.level = inv.player.level();

        // FIX: Put the slots back so the Server syncs your items...
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, ModBlocks.RIG_WORKBENCH.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        return ItemStack.EMPTY;
    }

    // ==========================================
    // THE OFF-SCREEN SLOTS TRICK
    // ==========================================
    private void addPlayerInventory(Inventory playerInventory) {
        for (int i = 0; i < 3; ++i) {
            for (int l = 0; l < 9; ++l) {
                // Set to -1000 X and Y! They exist to the server, but are invisible to the player.
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, -1000, -1000));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            // Set to -1000 X and Y!
            this.addSlot(new Slot(playerInventory, i, -1000, -1000));
        }
    }
}