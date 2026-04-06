package com.rigmod.network.packet;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CycleRadarModePacket {
    public CycleRadarModePacket() {}

    public CycleRadarModePacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                
                if (!helmet.isEmpty()) {
                    int currentMode = helmet.getOrCreateTag().getInt("RadarMode");
                    
                    // Cycle from 0 -> 1 -> 2 -> 3 -> 0
                    int nextMode = (currentMode + 1) % 4; 
                    
                    helmet.getOrCreateTag().putInt("RadarMode", nextMode);
                }
            }
        });
        return true;
    }
}