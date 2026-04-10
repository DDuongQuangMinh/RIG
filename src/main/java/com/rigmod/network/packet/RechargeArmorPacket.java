package com.rigmod.network.packet;

import com.rigmod.item.Custom3DArmorItem;
import com.rigmod.item.ModItems;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RechargeArmorPacket {
    private final int batteryIndex;

    // The Screen sends the index of the selected battery
    public RechargeArmorPacket(int batteryIndex) {
        this.batteryIndex = batteryIndex;
    }

    public RechargeArmorPacket(FriendlyByteBuf buf) {
        this.batteryIndex = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(batteryIndex);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack chestplate = player.getItemBySlot(net.minecraft.world.entity.EquipmentSlot.CHEST);
            if (!(chestplate.getItem() instanceof Custom3DArmorItem)) return;

            // ==========================================
            // 🔥 THE POWER CHART
            // ==========================================
            Item targetBattery = null;
            int powerAmount = 0;

            switch (batteryIndex) {
                case 0: targetBattery = ModItems.BATTERY_LEVEL_1.get(); powerAmount = 5;  break;
                case 1: targetBattery = ModItems.BATTERY_LEVEL_2.get(); powerAmount = 7;  break;
                case 2: targetBattery = ModItems.BATTERY_LEVEL_3.get(); powerAmount = 12; break;
                case 3: targetBattery = ModItems.BATTERY_LEVEL_4.get(); powerAmount = 15; break;
                case 4: targetBattery = ModItems.BATTERY_LEVEL_5.get(); powerAmount = 20; break;
                case 5: targetBattery = ModItems.BATTERY_LEVEL_6.get(); powerAmount = 30; break;
                case 6: targetBattery = ModItems.BATTERY_LEVEL_7.get(); powerAmount = 40; break;
            }

            if (targetBattery == null) return;

            // Check if player actually owns the battery
            int slot = -1;
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                if (player.getInventory().getItem(i).getItem() == targetBattery) {
                    slot = i;
                    break;
                }
            }

            if (slot != -1) {
                int currentPower = chestplate.getOrCreateTag().getInt("RigPower");
                if (currentPower < 100) {
                    // Consume 1 battery
                    player.getInventory().getItem(slot).shrink(1);
                    
                    // Add power (Cap at 100%)
                    int newPower = Math.min(100, currentPower + powerAmount);
                    chestplate.getOrCreateTag().putInt("RigPower", newPower);

                    // Sound & Feedback
                    player.level().playSound(null, player.blockPosition(), SoundEvents.BEACON_ACTIVATE, SoundSource.PLAYERS, 1.0F, 1.0F);
                    player.displayClientMessage(net.minecraft.network.chat.Component.literal("§b[RIG Matrix] Power Injected: +" + powerAmount + "%"), true);
                }
            } else {
                player.displayClientMessage(net.minecraft.network.chat.Component.literal("§c[RIG Matrix] You do not have the selected Power Core in your inventory!"), true);
            }
        });
        return true;
    }
}