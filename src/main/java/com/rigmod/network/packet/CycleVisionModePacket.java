package com.rigmod.network.packet;

import com.rigmod.item.Custom3DArmorItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CycleVisionModePacket {
    
    // We don't actually need to send any data inside the packet, 
    // because the server already knows WHO sent it. So these are empty!
    public CycleVisionModePacket() {}
    public CycleVisionModePacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    // THIS IS WHERE THE MAGIC HAPPENS ON THE SERVER
    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        
        context.enqueueWork(() -> {
            // Get the player who pressed the key
            ServerPlayer player = context.getSender();
            if (player != null) {
                // Check their head slot
                ItemStack helmet = player.getItemBySlot(EquipmentSlot.HEAD);
                
                // Ensure it's our tactical helmet
                if (helmet.getItem() instanceof Custom3DArmorItem) {
                    CompoundTag tag = helmet.getOrCreateTag();
                    
                    // Cycle the mode: 0 -> 1 -> 2 -> 3 -> 0
                    int currentMode = tag.getInt("VisionMode");
                    int newMode = (currentMode + 1) % 4; 
                    tag.putInt("VisionMode", newMode);
                    
                    // Play a satisfying click sound so the player knows it worked
                    player.level().playSound(null, player.blockPosition(), 
                            SoundEvents.UI_BUTTON_CLICK.get(), SoundSource.PLAYERS, 0.4f, 1.2f);
                }
            }
        });
        
        return true;
    }
}