package com.rigmod.network.packet;

import com.rigmod.item.ModItems;
import com.rigmod.item.PlasmaCutterItem;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class ReloadWeaponPacket {
    public ReloadWeaponPacket() {}
    public ReloadWeaponPacket(FriendlyByteBuf buf) {}
    public void toBytes(FriendlyByteBuf buf) {}

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            ItemStack stack = player.getMainHandItem();
            
            if (stack.getItem() instanceof PlasmaCutterItem gun) {
                CompoundTag tag = stack.getOrCreateTag();
                
                // ===============================================
                // 🔥 SCAN THE DEAD SPACE UPGRADE TREE 🔥
                // ===============================================
                int capNodes = 0;
                int relNodes = 0;
                for (int n = 0; n <= 22; n++) {
                    if (tag.getBoolean("RigNode_" + n)) {
                        // IDs for Capacity and Reload based on your layout
                        if (n==4 || n==5 || n==6 || n==11 || n==16 || n==18 || n==21 || n==22) capNodes++;
                        if (n==8 || n==10 || n==13) relNodes++;
                    }
                }
                
                // Calculate Dynamic Capacity (+2 Ammo per CAP Node)
                int maxAmmo = 10 + (capNodes * 2);
                int currentAmmo = tag.getInt("Ammo");

                // If the gun is already full, don't waste an animation or ammo!
                if (currentAmmo >= maxAmmo) return; 

                // --- 1. SCAN INVENTORY FOR AMMO ---
                int neededAmmo = maxAmmo - currentAmmo;
                int ammoFound = 0;
                Inventory inv = player.getInventory();
                boolean isCreative = player.isCreative();

                if (isCreative) {
                    ammoFound = neededAmmo; // Creative mode gets free ammo
                } else {
                    for (int i = 0; i < inv.getContainerSize(); i++) {
                        ItemStack slotStack = inv.getItem(i);
                        if (slotStack.getItem() == ModItems.PLASMA_ENERGY.get()) {
                            int take = Math.min(slotStack.getCount(), neededAmmo - ammoFound);
                            slotStack.shrink(take); 
                            ammoFound += take;
                            
                            if (ammoFound >= neededAmmo) break; 
                        }
                    }
                }

                // --- 2. ONLY RELOAD IF AMMO WAS FOUND ---
                if (ammoFound > 0) {
                    tag.putInt("Ammo", currentAmmo + ammoFound);
                    gun.triggerReloadAnimation(player, stack);
                    
                    // ===============================================
                    // 🔥 RELOAD SPEED COOLDOWN 🔥
                    // ===============================================
                    // Default = 60 ticks (3s). Each REL node reduces it by 10 ticks (0.5s).
                    int reloadTicks = 60 - (relNodes * 10);
                    if (reloadTicks < 10) reloadTicks = 10; // Safety floor
                    
                    // Lock the gun so you cannot fire while the reload animation is playing!
                    player.getCooldowns().addCooldown(gun, reloadTicks);
                    
                    // Pitch shifts up as the reload gets faster!
                    float pitch = 1.0f + (relNodes * 0.15f);
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), 
                            SoundEvents.CROSSBOW_LOADING_END, SoundSource.PLAYERS, 1.0f, pitch);
                } else {
                    // Out of ammo sound
                    player.level().playSound(null, player.getX(), player.getY(), player.getZ(), 
                            SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 1.0f);
                }
            }
        });
        context.setPacketHandled(true);
        return true;
    }
}