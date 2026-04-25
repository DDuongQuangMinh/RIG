package com.rigmod.network.packet;

import com.rigmod.item.ModItems;
import com.rigmod.item.Custom3DArmorItem;
import com.rigmod.item.PlasmaCutterItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ApplyNodePacket {
    private final int nodeId;
    private final int targetSlot; 

    public ApplyNodePacket(int nodeId, int targetSlot) {
        this.nodeId = nodeId;
        this.targetSlot = targetSlot;
    }

    public ApplyNodePacket(FriendlyByteBuf buf) {
        this.nodeId = buf.readInt();
        this.targetSlot = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(nodeId);
        buf.writeInt(targetSlot);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack targetGear = ItemStack.EMPTY;
            if (targetSlot == -100) {
                targetGear = player.getItemBySlot(EquipmentSlot.CHEST); 
            } else if (targetSlot >= 0 && targetSlot < player.getInventory().getContainerSize()) {
                targetGear = player.getInventory().getItem(targetSlot); 
            }

            if (targetGear.isEmpty() || (!(targetGear.getItem() instanceof Custom3DArmorItem) && !(targetGear.getItem() instanceof PlasmaCutterItem))) {
                return;
            }

            int nodeSlot = -1;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack checkStack = player.getInventory().getItem(i);
                if (checkStack.getItem() == ModItems.UPGRADE_NODE.get() && !checkStack.isEmpty()) {
                    nodeSlot = i;
                    break;
                }
            }

            if (nodeSlot != -1) {
                // 1. Physically consume 1 Node safely
                ItemStack nodeStack = player.getInventory().getItem(nodeSlot);
                nodeStack.shrink(1);
                if (nodeStack.isEmpty()) {
                    player.getInventory().setItem(nodeSlot, ItemStack.EMPTY);
                }

                // 2. Permanently save the Upgrade to the target gear's NBT
                targetGear.getOrCreateTag().putBoolean("RigNode_" + nodeId, true);
                
                // 3. 🔥 THE FIX: Forcefully re-insert the item so the server registers the NBT change!
                if (targetSlot == -100) {
                    player.setItemSlot(EquipmentSlot.CHEST, targetGear);
                } else {
                    player.getInventory().setItem(targetSlot, targetGear);
                }

                // 4. 🔥 THE FIX: Force the server to sync the currently open Workbench container!
                player.getInventory().setChanged();
                if (player.containerMenu != null) {
                    player.containerMenu.broadcastChanges();
                }
                player.inventoryMenu.broadcastChanges();

                // 5. Play Sound
                player.level().playSound(null, player.blockPosition(), SoundEvents.SMITHING_TABLE_USE, SoundSource.PLAYERS, 1.0F, 1.5F);
            }
        });
        
        context.setPacketHandled(true);
        return true;
    }
}