package com.rigmod.network.packet;

import com.rigmod.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.entity.player.Player;
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

            Item targetItem = null;
            
            if (tab == 0) { // HELMETS
                if (index == 0) targetItem = ModItems.ENGINEERING_LEVEL_3_HELMET.get();
                else if (index == 1) targetItem = ModItems.ENGINEERING_LEVEL_2_HELMET.get();
                else if (index == 2) targetItem = ModItems.STANDARD_LEVEL_1_HELMET.get();
            } 
            else if (tab == 1) { // CHESTPLATES
                if (index == 0) targetItem = ModItems.ENGINEERING_LEVEL_3_CHESTPLATE.get();
                else if (index == 1) targetItem = ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get();
                else if (index == 2) targetItem = ModItems.STANDARD_LEVEL_1_CHEST_WHITE.get();
                else if (index == 3) targetItem = ModItems.STANDARD_LEVEL_1_CHEST_BRONZE.get();
            } 
            else if (tab == 2) { // LEGGINGS
                if (index == 0) targetItem = ModItems.ENGINEERING_LEVEL_3_LEGGINGS.get();
                else if (index == 1) targetItem = ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get();
                else if (index == 2) targetItem = ModItems.STANDARD_LEVEL_1_LEGGINGS.get();
            }
            else if (tab == 3) { // BOOTS
                // 🔥 ADDED: Level 2 Boots mapping
                if (index == 0) targetItem = ModItems.ENGINEERING_LEVEL_2_BOOTS.get();
                else if (index == 1) targetItem = ModItems.STANDARD_LEVEL_1_BOOTS.get();
            }
            else if (tab == 4) { // BATTERIES
                if (index == 0) targetItem = ModItems.BATTERY_LEVEL_1.get();
                else if (index == 1) targetItem = ModItems.BATTERY_LEVEL_2.get();
                else if (index == 2) targetItem = ModItems.BATTERY_LEVEL_3.get();
                else if (index == 3) targetItem = ModItems.BATTERY_LEVEL_4.get();
                else if (index == 4) targetItem = ModItems.BATTERY_LEVEL_5.get();
                else if (index == 5) targetItem = ModItems.BATTERY_LEVEL_6.get();
                else if (index == 6) targetItem = ModItems.BATTERY_LEVEL_7.get();
            }

            if (targetItem == null) return;

            boolean canCraft = false;

            // ==========================================
            // 2. RECIPE LOGIC
            // ==========================================
            
            // --- HELMETS ---
            if (targetItem.equals(ModItems.STANDARD_LEVEL_1_HELMET.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 5) && hasItem(player, ModItems.BATTERY_LEVEL_1.get(), 2) && hasItem(player, Items.ENDER_EYE, 1)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 5); consumeItem(player, ModItems.BATTERY_LEVEL_1.get(), 2); consumeItem(player, Items.ENDER_EYE, 1); canCraft = true;
                }
            } 
            else if (targetItem.equals(ModItems.ENGINEERING_LEVEL_2_HELMET.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 7) && hasItem(player, ModItems.BATTERY_LEVEL_2.get(), 2) && hasItem(player, Items.ENDER_EYE, 3)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 7); consumeItem(player, ModItems.BATTERY_LEVEL_2.get(), 2); consumeItem(player, Items.ENDER_EYE, 3); canCraft = true;
                }
            }
            else if (targetItem.equals(ModItems.ENGINEERING_LEVEL_3_HELMET.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 10) && hasItem(player, ModItems.BATTERY_LEVEL_3.get(), 1) && hasItem(player, Items.BLAZE_ROD, 5) && hasItem(player, Items.NETHERITE_INGOT, 3)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 10); consumeItem(player, ModItems.BATTERY_LEVEL_3.get(), 1); consumeItem(player, Items.BLAZE_ROD, 5); consumeItem(player, Items.NETHERITE_INGOT, 3); canCraft = true;
                }
            }
            
            // --- CHESTPLATES ---
            else if (targetItem.equals(ModItems.ENGINEERING_LEVEL_3_CHESTPLATE.get())) {
                if (hasItem(player, ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get(), 1) && hasItem(player, Items.NETHERITE_INGOT, 8) && hasItem(player, ModItems.BATTERY_LEVEL_4.get(), 4) && hasItem(player, Items.DIAMOND, 12) && hasItem(player, Items.IRON_INGOT, 24) && hasItem(player, Items.BLAZE_ROD, 5)) {
                    consumeItem(player, ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get(), 1); consumeItem(player, Items.NETHERITE_INGOT, 8); consumeItem(player, ModItems.BATTERY_LEVEL_4.get(), 4); consumeItem(player, Items.DIAMOND, 12); consumeItem(player, Items.IRON_INGOT, 24); consumeItem(player, Items.BLAZE_ROD, 5); canCraft = true;
                }
            }
            else if (targetItem.equals(ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 10) && hasItem(player, Items.QUARTZ, 28) && hasItem(player, ModItems.BATTERY_LEVEL_5.get(), 5) && hasItem(player, Items.DIAMOND, 12) && hasItem(player, Items.NETHERITE_INGOT, 3)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 10); consumeItem(player, Items.QUARTZ, 28); consumeItem(player, ModItems.BATTERY_LEVEL_5.get(), 5); consumeItem(player, Items.DIAMOND, 12); consumeItem(player, Items.NETHERITE_INGOT, 3); canCraft = true;
                }
            }
            else if (targetItem.equals(ModItems.STANDARD_LEVEL_1_CHEST_BRONZE.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 5) && hasItem(player, Items.COPPER_INGOT, 7) && hasItem(player, Items.REDSTONE, 5) && hasItem(player, Items.QUARTZ, 5)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 5); consumeItem(player, Items.COPPER_INGOT, 7); consumeItem(player, Items.REDSTONE, 5); consumeItem(player, Items.QUARTZ, 5); canCraft = true;
                }
            }
            else if (targetItem.equals(ModItems.STANDARD_LEVEL_1_CHEST_WHITE.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 5) && hasItem(player, Items.QUARTZ, 12) && hasItem(player, Items.REDSTONE, 7) && hasItem(player, Items.DIAMOND, 3)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 5); consumeItem(player, Items.QUARTZ, 12); consumeItem(player, Items.REDSTONE, 7); consumeItem(player, Items.DIAMOND, 3); canCraft = true;
                }
            }
            
            // --- LEGGINGS ---
            else if (targetItem.equals(ModItems.ENGINEERING_LEVEL_3_LEGGINGS.get())) {
                if (hasItem(player, ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get(), 1) && hasItem(player, Items.NETHERITE_INGOT, 4) && hasItem(player, ModItems.BATTERY_LEVEL_3.get(), 1) && hasItem(player, Items.DIAMOND, 8)) {
                    consumeItem(player, ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get(), 1); consumeItem(player, Items.NETHERITE_INGOT, 4); consumeItem(player, ModItems.BATTERY_LEVEL_3.get(), 1); consumeItem(player, Items.DIAMOND, 8); canCraft = true;
                }
            }
            else if (targetItem.equals(ModItems.ENGINEERING_LEVEL_2_LEGGINGS.get())) {
                if (hasItem(player, ModItems.STANDARD_LEVEL_1_LEGGINGS.get(), 1) && hasItem(player, Items.QUARTZ, 12) && hasItem(player, ModItems.BATTERY_LEVEL_2.get(), 2) && hasItem(player, Items.DIAMOND, 5) && hasItem(player, Items.IRON_INGOT, 12)) {
                    consumeItem(player, ModItems.STANDARD_LEVEL_1_LEGGINGS.get(), 1); consumeItem(player, Items.QUARTZ, 12); consumeItem(player, ModItems.BATTERY_LEVEL_2.get(), 2); consumeItem(player, Items.DIAMOND, 5); consumeItem(player, Items.IRON_INGOT, 12); canCraft = true;
                }
            }
            else if (targetItem.equals(ModItems.STANDARD_LEVEL_1_LEGGINGS.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 4) && hasItem(player, Items.BLACK_DYE, 3) && hasItem(player, Items.QUARTZ, 6)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 4); consumeItem(player, Items.BLACK_DYE, 3); consumeItem(player, Items.QUARTZ, 6); canCraft = true;
                }
            }

            // --- BOOTS ---
            // 🔥 ADDED: Level 2 Boots Recipe Consumption
            else if (targetItem.equals(ModItems.ENGINEERING_LEVEL_2_BOOTS.get())) {
                if (hasItem(player, ModItems.STANDARD_LEVEL_1_BOOTS.get(), 1) && hasItem(player, ModItems.TITANIUM_INGOT.get(), 4) && hasItem(player, ModItems.BATTERY_LEVEL_2.get(), 2) && hasItem(player, Items.IRON_INGOT, 4)) {
                    consumeItem(player, ModItems.STANDARD_LEVEL_1_BOOTS.get(), 1); consumeItem(player, ModItems.TITANIUM_INGOT.get(), 4); consumeItem(player, ModItems.BATTERY_LEVEL_2.get(), 2); consumeItem(player, Items.IRON_INGOT, 4); canCraft = true;
                }
            }
            else if (targetItem.equals(ModItems.STANDARD_LEVEL_1_BOOTS.get())) {
                if (hasItem(player, Items.QUARTZ, 4) && hasItem(player, Items.REDSTONE, 2)) {
                    consumeItem(player, Items.QUARTZ, 4); consumeItem(player, Items.REDSTONE, 2); canCraft = true;
                }
            }
            
            // --- BATTERIES ---
            else if (targetItem.equals(ModItems.BATTERY_LEVEL_1.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 1) && hasItem(player, Items.REDSTONE, 4)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 1); consumeItem(player, Items.REDSTONE, 4); canCraft = true;
                }
            } else if (targetItem.equals(ModItems.BATTERY_LEVEL_2.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 2) && hasItem(player, Items.REDSTONE, 8)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 2); consumeItem(player, Items.REDSTONE, 8); canCraft = true;
                }
            } else if (targetItem.equals(ModItems.BATTERY_LEVEL_3.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 3) && hasItem(player, Items.REDSTONE, 12)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 3); consumeItem(player, Items.REDSTONE, 12); canCraft = true;
                }
            } else if (targetItem.equals(ModItems.BATTERY_LEVEL_4.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 4) && hasItem(player, Items.REDSTONE, 16) && hasItem(player, Items.QUARTZ, 1)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 4); consumeItem(player, Items.REDSTONE, 16); consumeItem(player, Items.QUARTZ, 1); canCraft = true;
                }
            } else if (targetItem.equals(ModItems.BATTERY_LEVEL_5.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 5) && hasItem(player, Items.REDSTONE, 20) && hasItem(player, Items.QUARTZ, 2)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 5); consumeItem(player, Items.REDSTONE, 20); consumeItem(player, Items.QUARTZ, 2); canCraft = true;
                }
            } else if (targetItem.equals(ModItems.BATTERY_LEVEL_6.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 6) && hasItem(player, Items.REDSTONE, 24) && hasItem(player, Items.QUARTZ, 3)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 6); consumeItem(player, Items.REDSTONE, 24); consumeItem(player, Items.QUARTZ, 3); canCraft = true;
                }
            } else if (targetItem.equals(ModItems.BATTERY_LEVEL_7.get())) {
                if (hasItem(player, ModItems.TITANIUM_INGOT.get(), 7) && hasItem(player, Items.REDSTONE, 28) && hasItem(player, Items.QUARTZ, 4)) {
                    consumeItem(player, ModItems.TITANIUM_INGOT.get(), 7); consumeItem(player, Items.REDSTONE, 28); consumeItem(player, Items.QUARTZ, 4); canCraft = true;
                }
            }

            if (canCraft) {
                ItemStack itemToGive = new ItemStack(targetItem);
                if (!player.getInventory().add(itemToGive)) {
                    player.drop(itemToGive, false); 
                }
                player.containerMenu.broadcastChanges();
                player.level().playSound(null, player.blockPosition(), SoundEvents.SMITHING_TABLE_USE, SoundSource.BLOCKS, 1.0F, 1.0F);
                if (player.level() instanceof ServerLevel serverLevel) {
                    serverLevel.sendParticles(ParticleTypes.ELECTRIC_SPARK, player.getX(), player.getY() + 1.0, player.getZ(), 15, 0.5, 0.5, 0.5, 0.1);
                }
            }
        });
        
        context.setPacketHandled(true); 
        return true;
    }

    private boolean hasItem(Player player, Item item, int requiredAmount) {
        int count = 0;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem().equals(item)) count += stack.getCount();
        }
        return count >= requiredAmount;
    }

    private void consumeItem(Player player, Item item, int amountToConsume) {
        int remaining = amountToConsume;
        for (int i = 0; i < player.getInventory().getContainerSize(); i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem().equals(item)) {
                int toTake = Math.min(stack.getCount(), remaining);
                stack.shrink(toTake);
                if (stack.isEmpty()) player.getInventory().setItem(i, ItemStack.EMPTY);
                remaining -= toTake;
                if (remaining <= 0) break;
            }
        }
    }
}