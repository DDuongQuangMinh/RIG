package com.rigmod.network.packet;

import com.rigmod.item.Custom3DArmorItem;
import com.rigmod.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RechargeArmorPacket {

    public RechargeArmorPacket() {}
    public RechargeArmorPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack chest = player.getItemBySlot(EquipmentSlot.CHEST);
            if (!(chest.getItem() instanceof Custom3DArmorItem)) return;

            int batterySlot = -1;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                if (player.getInventory().getItem(i).getItem() == ModItems.BATTERY_LEVEL_1.get()) {
                    batterySlot = i;
                    break;
                }
            }

            if (batterySlot != -1) {
                CompoundTag tag = chest.getOrCreateTag();
                int currentPower = tag.getInt("RigPower"); 
                
                // THE LOCK: Refuse to consume the battery if already full!
                if (currentPower >= 100) {
                    // Send a quick warning message above their hotbar
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("§eSuit is already at maximum capacity!"), true);
                    return; 
                }

                // If not full, consume 1 battery and charge it
                player.getInventory().getItem(batterySlot).shrink(1); 
                
                int newPower = Math.min(100, currentPower + 5);
                tag.putInt("RigPower", newPower);

                player.level().playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 2.0F);
                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ENCHANTED_HIT, player.getX(), player.getY() + 1.0, player.getZ(), 10, 0.5, 0.5, 0.5, 0.1);
                }
            }
        });
        return true;
    }
}