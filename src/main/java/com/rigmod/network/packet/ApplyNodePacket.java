package com.rigmod.network.packet;

import com.rigmod.item.ModItems;
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
    private final int targetSlot; // 🔥 NEW: Tracks which inventory slot the suit is in!

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

            // 🔥 1. Find the exact suit the player selected
            ItemStack targetSuit = ItemStack.EMPTY;
            if (targetSlot == -100) {
                targetSuit = player.getItemBySlot(EquipmentSlot.CHEST); // -100 means "Equipped"
            } else {
                targetSuit = player.getInventory().getItem(targetSlot); // Pull from inventory slot
            }

            if (!(targetSuit.getItem() instanceof com.rigmod.item.Custom3DArmorItem)) return;

            // 🔥 2. Find the Upgrade Node in the player's inventory
            int nodeSlot = -1;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                if (player.getInventory().getItem(i).getItem() == ModItems.UPGRADE_NODE.get()) {
                    nodeSlot = i;
                    break;
                }
            }

            if (nodeSlot != -1) {
                // Consume 1 Node
                player.getInventory().getItem(nodeSlot).shrink(1);
                if (player.getInventory().getItem(nodeSlot).isEmpty()) {
                    player.getInventory().setItem(nodeSlot, ItemStack.EMPTY);
                }

                // Permanently save the Upgrade to the target suit's NBT!
                targetSuit.getOrCreateTag().putBoolean("RigNode_" + nodeId, true);

                // Play the satisfying mechanical success sound
                player.level().playSound(null, player.blockPosition(), SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.PLAYERS, 1.0F, 1.2F);
            }
        });
        
        context.setPacketHandled(true);
        return true;
    }
}