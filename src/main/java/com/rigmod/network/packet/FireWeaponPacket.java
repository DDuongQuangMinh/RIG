package com.rigmod.network.packet;

import com.rigmod.item.PlasmaCutterItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class FireWeaponPacket {
    public FireWeaponPacket() {}
    public FireWeaponPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player != null) {
                ItemStack stack = player.getMainHandItem();
                if (stack.getItem() instanceof PlasmaCutterItem gun) {
                    // This executes the logic (projectile + ammo - 1)
                    // We DO NOT call player.swing() here, which is key to stopping the dip.
                    gun.shoot(player.level(), player, stack);
                }
            }
        });
        context.setPacketHandled(true);
        return true; // Return true to signify the packet was handled successfully
    }
}