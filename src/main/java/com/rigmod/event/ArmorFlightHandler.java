package com.rigmod.event;

import com.rigmod.RigMod;
import com.rigmod.item.ModItems;
import net.minecraft.core.particles.ParticleTypes;
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
            // 1. FLIGHT LOGIC (Runs on Server & Client)
            // ==========================================
            if (!player.isCreative() && !player.isSpectator()) {
                if (isWearingFlightArmor) {
                    if (!player.getAbilities().mayfly) {
                        player.getAbilities().mayfly = true;
                        player.onUpdateAbilities();
                    }
                } else {
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
                
                // Get player's body rotation in radians
                float bodyYaw = player.yBodyRot * ((float)Math.PI / 180F);
                
                // PULLED IN TIGHTER: 0.25 blocks behind player (was 0.4)
                double backwardX = Math.sin(bodyYaw) * 0.25D;
                double backwardZ = -Math.cos(bodyYaw) * 0.25D;
                
                // Distance apart from each other (left and right)
                double sideX = Math.cos(bodyYaw) * 0.22D;
                double sideZ = Math.sin(bodyYaw) * 0.22D;
                
                // RAISED TO SHOULDERS: 1.3 is upper back height (was 0.8)
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