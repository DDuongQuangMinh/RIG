package com.rigmod.network.packet;

import com.rigmod.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class CraftArmorPacket {
    private final int tab;
    private final int index;

    public CraftArmorPacket(int tab, int index) {
        this.tab = tab;
        this.index = index;
    }

    public CraftArmorPacket(FriendlyByteBuf buf) {
        this.tab = buf.readInt();
        this.index = buf.readInt();
    }

    public void toBytes(FriendlyByteBuf buf) {
        buf.writeInt(tab);
        buf.writeInt(index);
    }

    public boolean handle(Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            if (player == null) return;

            int requiredAmount = 8; 
            int titaniumCount = 0;

            // 1. Double-check the player's inventory on the Server side
            for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                ItemStack stack = player.getInventory().getItem(i);
                if (stack.getItem() == ModItems.TITANIUM_INGOT.get()) {
                    titaniumCount += stack.getCount();
                }
            }

            // 2. If they have enough, consume the Titanium!
            if (titaniumCount >= requiredAmount) {
                int removed = 0;
                for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
                    ItemStack stack = player.getInventory().getItem(i);
                    if (stack.getItem() == ModItems.TITANIUM_INGOT.get()) {
                        int toRemove = Math.min(stack.getCount(), requiredAmount - removed);
                        stack.shrink(toRemove);
                        removed += toRemove;
                        if (removed >= requiredAmount) break;
                    }
                }

                // 3. Select the correct Armor based on what they clicked in the UI
                ItemStack itemToGive = ItemStack.EMPTY;
                if (tab == 0) {
                    if (index == 0) itemToGive = new ItemStack(ModItems.ENGINEERING_LEVEL_2_HELMET.get());
                    else if (index == 1) itemToGive = new ItemStack(ModItems.STANDARD_LEVEL_1_HELMET.get());
                } else if (tab == 1) {
                    if (index == 0) itemToGive = new ItemStack(ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get());
                    else if (index == 1) itemToGive = new ItemStack(ModItems.STANDARD_LEVEL_1_CHEST_WHITE.get());
                    else if (index == 2) itemToGive = new ItemStack(ModItems.STANDARD_LEVEL_1_CHEST_BRONZE.get());
                } else if (tab == 2) {
                    if (index == 0) itemToGive = new ItemStack(ModItems.STANDARD_LEVEL_1_LEGGINGS.get());
                }

                // 4. Give the Armor AND play the feedback!
                if (!itemToGive.isEmpty()) {
                    // Hand over the item
                    if (!player.getInventory().add(itemToGive)) {
                        player.drop(itemToGive, false); // Drops on ground if inventory is full
                    }

                    // NEW: Play a heavy mechanical Smithing sound!
                    player.level().playSound(null, player.blockPosition(), SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                    
                    // NEW: Spawn a burst of electric sparks around the Workbench!
                    if (player.level() instanceof ServerLevel serverLevel) {
                        serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, 
                            player.getX(), player.getY() + 1.0, player.getZ(), 
                            15, 0.5, 0.5, 0.5, 0.1);
                    }
                }
            }
        });
        return true;
    }
}