package com.rigmod.event;

import com.rigmod.RigMod;
import com.rigmod.item.ModItems;
import com.rigmod.client.KeyBindings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.joml.Vector3f;
import org.joml.Quaternionf;

@Mod.EventBusSubscriber(modid = RigMod.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class ArmorFlightHandler {

    public static boolean isStableMode = false;
    public static boolean isFlying = false;

    // THE FIX: Added physics-based roll variables for buttery smooth camera interpolation
    public static float oRoll = 0.0f; 
    public static float currentRoll = 0.0f;
    public static float rollVelocity = 0.0f;

    @SubscribeEvent
    public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            Player player = event.player;
            
            // THE FIX: Update old roll every tick for frame-perfect camera smoothing
            if (player.level().isClientSide() && player == Minecraft.getInstance().player) {
                oRoll = currentRoll;
                currentRoll += rollVelocity;
                rollVelocity *= 0.85f; // Friction so it gracefully slows down
                
                if (isStableMode && !KeyBindings.ROTATE_LEFT_KEY.isDown() && !KeyBindings.ROTATE_RIGHT_KEY.isDown()) {
                    // Auto-level the camera back to 0 if stable mode is on and no keys are pressed
                    currentRoll = Mth.lerp(0.1f, currentRoll, 0.0f); 
                }
            }

            ItemStack chestArmor = player.getItemBySlot(EquipmentSlot.CHEST);
            
            // THE FIX: Added Level 4 to the flight checks!
            boolean isLevel2 = chestArmor.getItem() == ModItems.ENGINEERING_LEVEL_2_CHESTPLATE.get();
            boolean isLevel3 = chestArmor.getItem() == ModItems.ENGINEERING_LEVEL_3_CHESTPLATE.get();
            boolean isLevel4 = chestArmor.getItem() == ModItems.ENGINEERING_LEVEL_4_CHESTPLATE.get();
            boolean isWearingFlightArmor = isLevel2 || isLevel3 || isLevel4;

            if (!player.isCreative() && !player.isSpectator()) {
                if (isWearingFlightArmor) {
                    
                    CompoundTag tag = chestArmor.getOrCreateTag();
                    if (!tag.contains("RigPower")) {
                        tag.putInt("RigPower", 0);
                    }
                    int currentFuel = tag.getInt("RigPower");

                    if (player.getAbilities().mayfly) {
                        player.getAbilities().mayfly = false;
                        player.getAbilities().flying = false;
                        player.onUpdateAbilities();
                    }

                    if (currentFuel > 0 && !player.onGround()) {
                        
                        if (player.level().isClientSide()) {
                            isFlying = true;
                        }

                        int flightTicks = tag.getInt("FlightTicks") + 1;
                        if (flightTicks >= 12000) {
                            currentFuel--;
                            tag.putInt("RigPower", currentFuel);
                            tag.putInt("FlightTicks", 0); 

                            if (currentFuel <= 0) {
                                player.displayClientMessage(net.minecraft.network.chat.Component.literal("§c[WARNING] Suit Power Depleted! Gravity Restored."), true);
                            }
                        } else {
                            tag.putInt("FlightTicks", flightTicks); 
                        }

                        if (player.level().isClientSide()) {
                            ClientPhysics.applyZeroG(player);
                        }
                    } else {
                        if (player.level().isClientSide()) {
                            isFlying = false; 
                        }
                        tag.putInt("FlightTicks", 0); 
                    }
                } 
                else {
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

    private static class ClientPhysics {
        public static void applyZeroG(Player player) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer localPlayer = mc.player;
            
            if (localPlayer == null || player.getId() != localPlayer.getId()) return;

            localPlayer.yBodyRot = localPlayer.yHeadRot;
            localPlayer.yBodyRotO = localPlayer.yHeadRotO;

            Vec3 motion = localPlayer.getDeltaMovement();
            Vec3 look = localPlayer.getLookAngle();
            
            double newY = motion.y + 0.075D; 
            double newX = motion.x;
            double newZ = motion.z;

            boolean isThrusting = false;
            double thrustPower = 0.02D; 

            float yaw = localPlayer.getYRot() * ((float)Math.PI / 180F);
            double rightX = -Math.cos(yaw);
            double rightZ = -Math.sin(yaw);

            if (mc.options.keyJump.isDown()) {
                newY += thrustPower; 
                isThrusting = true;
            }
            if (mc.options.keyShift.isDown()) {
                newY -= thrustPower; 
                isThrusting = true;
            }
            if (mc.options.keyUp.isDown()) { 
                newX += look.x * thrustPower; 
                newZ += look.z * thrustPower;
                isThrusting = true;
            }
            if (mc.options.keyDown.isDown()) { 
                newX -= look.x * thrustPower; 
                newZ -= look.z * thrustPower;
                isThrusting = true;
            }
            if (mc.options.keyLeft.isDown()) { 
                newX -= rightX * thrustPower;
                newZ -= rightZ * thrustPower;
                isThrusting = true;
            }
            if (mc.options.keyRight.isDown()) { 
                newX += rightX * thrustPower;
                newZ += rightZ * thrustPower;
                isThrusting = true;
            }

            // THE FIX: Inject physics-based acceleration into the roll velocity instead of snapping the value
            float rollThrust = 1.5f;
            if (KeyBindings.ROTATE_LEFT_KEY.isDown()) {
                ArmorFlightHandler.rollVelocity -= rollThrust;
                isThrusting = true;
            }
            if (KeyBindings.ROTATE_RIGHT_KEY.isDown()) {
                ArmorFlightHandler.rollVelocity += rollThrust;
                isThrusting = true;
            }

            if (isStableMode) {
                if (!isThrusting) {
                    newX *= 0.80D; 
                    newZ *= 0.80D;
                }
                if (!mc.options.keyJump.isDown() && !mc.options.keyShift.isDown()) {
                    newY = 0.0D; 
                    isThrusting = true; 
                }
            } else {
                if (!isThrusting) {
                    newX *= 0.98D;
                    newZ *= 0.98D;
                }
            }

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
            
            Quaternionf rollRot = new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 0, 1), ArmorFlightHandler.currentRoll);
            Quaternionf yawRot = new Quaternionf().fromAxisAngleDeg(new Vector3f(0, 1, 0), -player.yBodyRot);

            Vector3f leftBoot = new Vector3f(0.15f, -1.0f, 0.1f);
            Vector3f rightBoot = new Vector3f(-0.15f, -1.0f, 0.1f);
            Vector3f flameVel = new Vector3f(0.0f, -0.3f, 0.0f); 

            leftBoot.rotate(rollRot).rotate(yawRot);
            rightBoot.rotate(rollRot).rotate(yawRot);
            flameVel.rotate(rollRot).rotate(yawRot);

            double pivotX = player.getX();
            double pivotY = player.getY() + 1.0D; 
            double pivotZ = player.getZ();

            double spreadX = (random.nextDouble() - 0.5D) * 0.05D;
            double spreadZ = (random.nextDouble() - 0.5D) * 0.05D;

            player.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, pivotX + leftBoot.x() + spreadX, pivotY + leftBoot.y(), pivotZ + leftBoot.z() + spreadZ, flameVel.x(), flameVel.y(), flameVel.z());
            player.level().addParticle(ParticleTypes.SOUL_FIRE_FLAME, pivotX + rightBoot.x() + spreadX, pivotY + rightBoot.y(), pivotZ + rightBoot.z() + spreadZ, flameVel.x(), flameVel.y(), flameVel.z());
            
            if (random.nextInt(2) == 0) { 
                player.level().addParticle(ParticleTypes.SMOKE, pivotX + leftBoot.x() + spreadX, pivotY + leftBoot.y(), pivotZ + leftBoot.z() + spreadZ, flameVel.x() * 0.5, flameVel.y() * 0.5, flameVel.z() * 0.5);
                player.level().addParticle(ParticleTypes.SMOKE, pivotX + rightBoot.x() + spreadX, pivotY + rightBoot.y(), pivotZ + rightBoot.z() + spreadZ, flameVel.x() * 0.5, flameVel.y() * 0.5, flameVel.z() * 0.5);
            }
        }
    }
}