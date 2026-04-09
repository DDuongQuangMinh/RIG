package com.rigmod.menu;

import com.rigmod.block.ModBlocks;
import com.rigmod.blockentity.RigWorkbenchBlockEntity;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot; 
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

        // FIX: Track the inventory, hotbar, AND ARMOR so the UI updates instantly!
        addPlayerInventory(inv);
        addPlayerHotbar(inv);
        addPlayerArmor(inv);
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
                this.addSlot(new Slot(playerInventory, l + i * 9 + 9, -1000, -1000));
            }
        }
    }

    private void addPlayerHotbar(Inventory playerInventory) {
        for (int i = 0; i < 9; ++i) {
            this.addSlot(new Slot(playerInventory, i, -1000, -1000));
        }
    }

    // NEW: Puts your 4 armor slots off-screen so the Server instantly syncs power levels!
    private void addPlayerArmor(Inventory playerInventory) {
        for (int i = 0; i < 4; ++i) {
            // Vanilla armor slots are indices 36, 37, 38, and 39
            this.addSlot(new Slot(playerInventory, 36 + i, -1000, -1000));
        }
    }
}