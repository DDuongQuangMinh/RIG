package com.rigmod.network.packet;

import com.rigmod.item.PlasmaCutterItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class RotateBladePacket {
    
    public RotateBladePacket() {}
    public RotateBladePacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack stack = player.getMainHandItem();
            
            if (stack.getItem() instanceof PlasmaCutterItem plasmaCutter) {
                // 1. Flip the Vertical NBT data
                boolean currentState = stack.getOrCreateTag().getBoolean("IsVertical");
                stack.getOrCreateTag().putBoolean("IsVertical", !currentState);
                
                // 2. THE CLONE FIX: Break the shared animation cache!
                // By forcing a new random ID, GeckoLib instantly disconnects this gun from any Item Frame clones.
                stack.getOrCreateTag().putLong("geckoAnimId", player.getRandom().nextLong());
                
                // 3. Play the mechanical click sound
                player.level().playSound(null, player.blockPosition(), SoundEvents.IRON_TRAPDOOR_OPEN, SoundSource.PLAYERS, 1.0f, 1.5f);
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}