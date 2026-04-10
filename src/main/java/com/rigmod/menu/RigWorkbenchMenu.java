package com.rigmod.menu;

import com.rigmod.block.ModBlocks;
import com.rigmod.blockentity.RigWorkbenchBlockEntity;
import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot; 
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.SlotItemHandler;

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

        // ==========================================
        // 🛑 THE MACHINE'S 2 SLOTS (HIDDEN)
        // ==========================================
        this.blockEntity.getCapability(ForgeCapabilities.ITEM_HANDLER).ifPresent(iItemHandler -> {
            
            // SLOT 0: The Battery Slot
            // Moved to -1000 because your new custom UI uses buttons and packets instead!
            this.addSlot(new SlotItemHandler(iItemHandler, 0, -1000, -1000)); 

            // SLOT 1: The Armor Slot - LEVEL 2 BOUNCER ACTIVE
            // Moved to -1000 so it doesn't float over your new 50/50 holographic preview.
            this.addSlot(new SlotItemHandler(iItemHandler, 1, -1000, -1000) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    // Physically rejects anything that isn't exactly Level 2 Armor
                    if (stack.getItem() instanceof Custom3DArmorItem armorItem) {
                        return armorItem.getArmorLevel() == 2; 
                    }
                    return false; 
                }
            });
        });

        // Track the inventory, hotbar, and armor
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

    private void addPlayerArmor(Inventory playerInventory) {
        for (int i = 0; i < 4; ++i) {
            this.addSlot(new Slot(playerInventory, 36 + i, -1000, -1000));
        }
    }
}