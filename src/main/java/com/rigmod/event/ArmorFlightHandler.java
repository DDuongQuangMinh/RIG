package com.rigmod.event;

import com.rigmod.RigMod;
import com.rigmod.item.ModItems;
import com.rigmod.client.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = RigMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArmorFlightHandler {

    // Global toggles and state variables
    public static boolean isStableMode = false;
    
    // 🔥 NEW: Tracks the exact tilt angle of the player's perspective
    public static float currentRoll = 0.0f;
    public static boolean isFlying = false;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            
            ItemStack chestArmor = player.getItemBySlot(EquipmentSlot.CHEST);
            
            boolean isLevel2 = chestArmor.getItem() == ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get();
            boolean isLevel3 = chestArmor.getItem() == ModItems.ENGINEERING_LEVEL_3_CHESTPLATE.get();
            boolean isWearingFlightArmor = isLevel2 || isLevel3;

            if (!player.isCreative() && !player.isSpectator()) {
                if (isWearingFlightArmor) {
                    
                    CompoundTag tag = chestArmor.getOrCreateTag();
                    if (!tag.contains("RigPower")) {
                        tag.putInt("RigPower", 0);
                    }
                    int currentFuel = tag.getInt("RigPower");

                    // ⛔ DISABLE VANILLA CREATIVE FLIGHT (To allow our custom physics)
                    if (player.getAbilities().mayfly) {
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                        player.onUpdateAbilities();
                    }

                    // 🚀 ASTRONAUT MODE (Powered standing flight)
                    if (currentFuel > 0 && !player.onGround()) {
                        
                        // Set flight state for the camera and body locking
                        if (player.level().isClientSide()) {
                            isFlying = true;
                        }

                        // 1. DRAIN FUEL
                        int drainInterval = isLevel3 ? 60 : 20;
                        if (player.tickCount % drainInterval == 0) {
                            currentFuel--;
                            tag.putInt("RigPower", currentFuel);

                            if (currentFuel <= 0) {
                                player.displayClientMessage(net.minecraft.network.chat.Component.literal("§c[WARNING] Suit Power Depleted! Gravity Restored."), true);
                            }
                        }

                        // 2. APPLY PHYSICS (Client-Side Only)
                        if (player.level().isClientSide()) {
                            ClientPhysics.applyZeroG(player);
                        }
                    } else {
                        // Reset flight state when landed
                        if (player.level().isClientSide()) {
                            isFlying = false; 
                        }
                    }
                } 
                else {
                    // Not wearing flight armor, clean up state
                    if (player.level().isClientSide()) isFlying = false;
                    
                    if (player.getAbilities().mayfly) {
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                        player.onUpdateAbilities();
                    }
                }
            }
        }
    }

    // ==========================================
    // 🌌 ZERO-G CLIENT PHYSICS ENGINE 🌌
    // ==========================================
    private static class ClientPhysics {
        public static void applyZeroG(Player player) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer localPlayer = mc.player;
            
            if (localPlayer == null || player.getId() != localPlayer.getId()) return;

            Vec3 motion = localPlayer.getDeltaMovement();
            Vec3 look = localPlayer.getLookAngle();
            
            // ANTI-GRAVITY: Counteracts the default downward pull
            double newY = motion.y + 0.075D; 
            double newX = motion.x;
            double newZ = motion.z;

            boolean isThrusting = false;
            double thrustPower = 0.02D; 

            // Calculate precise Left/Right vectors based on Yaw for proper strafing
            float yaw = localPlayer.getYRot() * ((float)Math.PI / 180F);
            double rightX = -Math.cos(yaw);
            double rightZ = -Math.sin(yaw);

            // MAIN THRUSTER CONTROLS
            if (mc.options.keyJump.isDown()) {
                newY += thrustPower; // Up
                isThrusting = true;
            }
            if (mc.options.keyShift.isDown()) {
                newY -= thrustPower; // Down
                isThrusting = true;
            }
            if (mc.options.keyUp.isDown()) { // W key
                newX += look.x * thrustPower; 
                newZ += look.z * thrustPower;
                isThrusting = true;
            }
            if (mc.options.keyDown.isDown()) { // S key
                newX -= look.x * thrustPower; 
                newZ -= look.z * thrustPower;
                isThrusting = true;
            }
            
            // SIDEWAYS THRUSTER CONTROLS (A and D keys)
            if (mc.options.keyLeft.isDown()) { // A key
                newX -= rightX * thrustPower;
                newZ -= rightZ * thrustPower;
                isThrusting = true;
            }
            if (mc.options.keyRight.isDown()) { // D key
                newX += rightX * thrustPower;
                newZ += rightZ * thrustPower;
                isThrusting = true;
            }

            // STABLE MODE vs DRIFT MODE
            if (isStableMode) {
                // STABLE: Strong friction for quick braking
                if (!isThrusting) {
                    newX *= 0.80D; 
                    newZ *= 0.80D;
                }
                // STABLE: Altitude locking
                if (!mc.options.keyJump.isDown() && !mc.options.keyShift.isDown()) {
                    newY = 0.0D; 
                    isThrusting = true; // Visual Fix: Keeps flames spawning when hovering
                }
            } else {
                // DRIFT: Minimal friction for endless momentum
                if (!isThrusting) {
                    newX *= 0.98D;
                    newZ *= 0.98D;
                }
            }

            // MAX SPEED CAP
            double maxSpeedHorizontal = 0.5D; 
            double maxSpeedVertical = 0.4D;   

            newX = Math.max(-maxSpeedHorizontal, Math.min(maxSpeedHorizontal, newX));
            newY = Math.max(-maxSpeedVertical, Math.min(maxSpeedVertical, newY));
            newZ = Math.max(-maxSpeedHorizontal, Math.min(maxSpeedHorizontal, newZ));

            localPlayer.setDeltaMovement(newX, newY, newZ);

            if (isThrusting) {
                spawnAstronautParticles(localPlayer, localPlayer.getRandom());
            }
        }

        private static void spawnAstronautParticles(Player player, RandomSource random) {
            float bodyYaw = player.yBodyRot * ((float)Math.PI / 180F);
            
            double backwardX = Math.sin(bodyYaw) * 0.35D; 
            double backwardZ = -Math.cos(bodyYaw) * 0.35D; 
            
            double sideX = Math.cos(bodyYaw) * 0.20D;
            double sideZ = Math.sin(bodyYaw) * 0.20D;
            
            double y = player.getY() + 1.25D; // Just behind the 3D pack thrusters

            double leftX = player.getX() + backwardX - sideX;
            double leftZ = player.getZ() + backwardZ - sideZ;
            
            double rightX = player.getX() + backwardX + sideX;
            double rightZ = player.getZ() + backwardZ + sideZ;

            double spreadX = (random.nextDouble() - 0.5D) * 0.1D;
            double spreadZ = (random.nextDouble() - 0.5D) * 0.1D;

            // Spawn Particles behind the pack
            player.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, leftX + spreadX, y, leftZ + spreadZ, 0.0D, -0.15D, 0.0D);
            player.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, rightX + spreadX, y, rightZ + spreadZ, 0.0D, -0.15D, 0.0D);
            
            if (random.nextInt(2) == 0) { 
                player.level().addParticle(ParticleTypes.SMOKE, leftX + spreadX, y, leftZ + spreadZ, 0.0D, -0.1D, 0.0D);
                player.level().addParticle(ParticleTypes.SMOKE, rightX + spreadX, y, rightZ + spreadZ, 0.0D, -0.1D, 0.0D);
            }
        }
    }
}