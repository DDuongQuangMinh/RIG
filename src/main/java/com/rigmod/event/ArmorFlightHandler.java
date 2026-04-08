package com.rigmod.event;

import com.rigmod.RigMod;
import com.rigmod.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RigMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArmorFlightHandler {

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            
            ItemStack chestArmor = player.getItemBySlot(EquipmentSlot.CHEST);
            boolean isWearingFlightArmor = chestArmor.getItem() == ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get();

            // ==========================================
            // 1. FLIGHT & FUEL LOGIC
            // ==========================================
            if (!player.isCreative() && !player.isSpectator()) {
                if (isWearingFlightArmor) {
                    
                    // Access the chestplate's hidden data
                    CompoundTag tag = chestArmor.getOrCreateTag();
                    int maxFuel = 1200; // 60 seconds of continuous flight
                    
                    // If the armor is brand new, fill the tank!
                    if (!tag.contains("JetpackFuel")) {
                        tag.putInt("JetpackFuel", maxFuel);
                    }
                    int currentFuel = tag.getInt("JetpackFuel");

                    // ⚙️ IF ACTIVELY FLYING IN THE AIR: Drain Fuel
                    if (player.getAbilities().flying) {
                        currentFuel--;
                        tag.putInt("JetpackFuel", currentFuel);

                        // HUD: Display fuel percentage on the Action Bar
                        if (currentFuel % 10 == 0 || currentFuel < 60) {
                            int percent = (int) (((float) currentFuel / maxFuel) * 100);
                            String color = currentFuel < 240 ? "§c" : "§b"; // Red if under 20%, Cyan otherwise
                            
                            // The 'true' at the end makes it appear above the hotbar instead of spamming chat!
                            player.displayClientMessage(net.minecraft.network.chat.Component.literal(color + "Jetpack Fuel: " + percent + "%"), true);
                        }

                        // OUT OF FUEL: Instantly drop the player
                        if (currentFuel <= 0) {
                            player.getAbilities().mayfly = false;
                            player.getAbilities().flying = false;
                            player.onUpdateAbilities();
                            player.displayClientMessage(net.minecraft.network.chat.Component.literal("§c[WARNING] Jetpack Depleted!"), true);
                        }
                    } 
                    // ⚙️ IF STANDING ON THE GROUND: Recharge Fuel
                    else if (player.onGround()) {
                        if (currentFuel < maxFuel) {
                            currentFuel += 4; // Recharges 4x faster than it drains
                            if (currentFuel > maxFuel) currentFuel = maxFuel;
                            tag.putInt("JetpackFuel", currentFuel);
                            
                            // Let the player know it is fully charged
                            if (currentFuel == maxFuel) {
                                player.displayClientMessage(net.minecraft.network.chat.Component.literal("§aJetpack Fully Charged!"), true);
                            }
                        }
                    }

                    // Always allow them to double-jump to start flying IF they have fuel
                    if (currentFuel > 0 && !player.getAbilities().mayfly) {
                        player.getAbilities().mayfly = true;
                        player.onUpdateAbilities();
                    }
                } 
                else {
                    // Instantly revoke flight if they take the armor off
                    if (player.getAbilities().mayfly) {
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                        player.onUpdateAbilities();
                    }
                }
            }

            // ==========================================
            // 2. 3D TWIN PARTICLE EFFECTS (Client ONLY)
            // ==========================================
            if (player.level().isClientSide() && isWearingFlightArmor && player.getAbilities().flying) {
                RandomSource random = player.getRandom();
                
                float bodyYaw = player.yBodyRot * ((float)Math.PI / 180F);
                
                double backwardX = Math.sin(bodyYaw) * 0.25D;
                double backwardZ = -Math.cos(bodyYaw) * 0.25D;
                
                double sideX = Math.cos(bodyYaw) * 0.22D;
                double sideZ = Math.sin(bodyYaw) * 0.22D;
                
                double y = player.getY() + 1.3D; 

                // --- LEFT THRUSTER ---
                double leftX = player.getX() + backwardX + sideX;
                double leftZ = player.getZ() + backwardZ + sideZ;
                spawnThrusterParticles(player, random, leftX, y, leftZ);

                // --- RIGHT THRUSTER ---
                double rightX = player.getX() + backwardX - sideX;
                double rightZ = player.getZ() + backwardZ - sideZ;
                spawnThrusterParticles(player, random, rightX, y, rightZ);
            }
        }
    }

    private static void spawnThrusterParticles(Player player, RandomSource random, double x, double y, double z) {
        double spreadX = (random.nextDouble() - 0.5D) * 0.15D;
        double spreadZ = (random.nextDouble() - 0.5D) * 0.15D;

        player.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, x + spreadX, y, z + spreadZ, 0.0D, -0.1D, 0.0D);
        
        if (random.nextInt(3) == 0) { 
            player.level().addParticle(ParticleTypes.SMOKE, x + spreadX, y, z + spreadZ, 0.0D, -0.05D, 0.0D);
        }
    }
}