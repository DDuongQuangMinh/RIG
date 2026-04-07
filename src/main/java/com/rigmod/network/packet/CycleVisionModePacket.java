package com.rigmod.network.packet;

import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CycleVisionModePacket {
    public CycleVisionModePacket() {}

    public CycleVisionModePacket(FriendlyByteBuf buf) {}

    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                
                if (!helmet.isEmpty() && helmet.getItem() instanceof Custom3DArmorItem armor) {
                    int currentMode = helmet.getOrCreateTag().getInt("VisionMode");
                    int nextMode;
                    
                    if (armor.getArmorLevel() == 2) {
                        // Level 2 Helmet: Simple Thermal toggle (0 or 1)
                        nextMode = (currentMode == 0) ? 1 : 0;
                    } else {
                        // Level 1 Helmet: Cycle through 0, 1, 2, 3
                        nextMode = (currentMode + 1) % 4; 
                    }
                    
                    helmet.getOrCreateTag().putInt("VisionMode", nextMode);
                }
            }
        });
        return true;
    }
}