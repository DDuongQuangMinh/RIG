package com.rigmod.item;

import com.rigmod.client.renderer.PlasmaCutterRenderer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.client.extensions.common.IClientItemExtensions;
import software.bernie.geckolib.animatable.GeoItem;
import software.bernie.geckolib.core.animatable.instance.AnimatableInstanceCache;
import software.bernie.geckolib.core.animation.AnimatableManager;
import software.bernie.geckolib.core.animation.AnimationController;
import software.bernie.geckolib.core.animation.RawAnimation;
import software.bernie.geckolib.core.object.PlayState;
import software.bernie.geckolib.util.GeckoLibUtil;

import java.util.function.Consumer;

public class PlasmaCutterItem extends Item implements GeoItem {
    private final AnimatableInstanceCache cache = GeckoLibUtil.createInstanceCache(this);

    public PlasmaCutterItem(Properties properties) {
        super(properties);
    }

    @Override
    public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged) {
        return slotChanged;
    }

    public void shoot(Level level, Player player, ItemStack stack) {
        // 🔥 Prevent firing if the gun is still cooling down from the last shot or a reload!
        if (player.getCooldowns().isOnCooldown(this)) return;

        if (!level.isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            int ammo = tag.getInt("Ammo");

            if (ammo > 0) {
                boolean isVertical = tag.getBoolean("IsVertical");

                // ===============================================
                // 🔥 1. DYNAMICALLY SCAN THE NODE UPGRADES 🔥
                // ===============================================
                int dmgNodes = 0;
                int rofNodes = 0;
                
                for (int n = 0; n <= 22; n++) {
                    if (tag.getBoolean("RigNode_" + n)) {
                        if (n==1 || n==9 || n==12 || n==14 || n==15) dmgNodes++;
                        if (n==2 || n==3 || n==19) rofNodes++;
                    }
                }

                // Calculate Total Damage (Base 6.0 + 2.0 per DMG Node)
                float finalDamage = 6.0F + (dmgNodes * 2.0F);
                
                // Calculate Rate of Fire Cooldown (Normal = 20 ticks. Each ROF node cuts 4 ticks)
                int fireDelay = 20 - (rofNodes * 4); 
                if (fireDelay < 5) fireDelay = 5; // Safety floor so it doesn't break the game

                com.rigmod.entity.PlasmaBulletEntity bolt = new com.rigmod.entity.PlasmaBulletEntity(level, player);
                bolt.setVertical(isVertical); 
                
                // Offset Math
                Vec3 eyePos = player.getEyePosition();
                Vec3 look = player.getLookAngle();
                Vec3 up = player.getUpVector(1.0F); 
                Vec3 right = look.cross(up).normalize(); 
                
                Vec3 spawnPos = eyePos.add(look.scale(0.5)); 
                spawnPos = spawnPos.add(right.scale(0.4));
                if (isVertical) {
                    spawnPos = spawnPos.subtract(up.scale(0.4));
                }

                bolt.setPos(spawnPos.x, spawnPos.y, spawnPos.z);
                bolt.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 3.5F, 0.2F);
                
                // 🔥 APPLY THE CALCULATED DAMAGE TO THE BULLET
                bolt.setBaseDamage(finalDamage);
                
                level.addFreshEntity(bolt);

                tag.putInt("Ammo", ammo - 1);
                level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.ZOMBIE_VILLAGER_CURE, SoundSource.PLAYERS, 1.0f, 2.0f);

                // 🔥 APPLY THE DYNAMIC RATE OF FIRE COOLDOWN
                player.getCooldowns().addCooldown(this, fireDelay);

            } else {
                // Empty gun click
                level.playSound(null, player.getX(), player.getY(), player.getZ(), 
                    SoundEvents.DISPENSER_FAIL, SoundSource.PLAYERS, 1.0f, 1.2f);
                player.getCooldowns().addCooldown(this, 10);
            }
        }
    }

    public void triggerReloadAnimation(Player player, ItemStack stack) {
        if (!player.level().isClientSide) {
            CompoundTag tag = stack.getOrCreateTag();
            if (!tag.contains("GeckoLibID")) {
                tag.putLong("GeckoLibID", player.getRandom().nextLong());
            }
            long instanceId = tag.getLong("GeckoLibID");
            triggerAnim(player, instanceId, "controller", "reload_trigger");
        }
    }

    public void triggerRotateAnimation(Player player, ItemStack stack) {
    }

    @Override
    public void registerControllers(AnimatableManager.ControllerRegistrar controllers) {
        controllers.add(new AnimationController<>(this, "controller", 0, event -> {
            ItemStack stack = event.getData(software.bernie.geckolib.constant.DataTickets.ITEMSTACK);
            if (stack != null) {
                boolean isVertical = stack.getOrCreateTag().getBoolean("IsVertical");
                String anim = isVertical ? "Vertical rotaion" : "Horizontal rotation";
                return event.setAndContinue(RawAnimation.begin().thenPlayAndHold(anim));
            }
            return PlayState.CONTINUE;
        }).triggerableAnim("reload_trigger", RawAnimation.begin().thenPlay("reloading")));
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        CompoundTag tag = stack.getOrCreateTag();
        
        // 🔥 SCAN FOR CAPACITY NODES
        int capNodes = 0;
        for (int n = 0; n <= 22; n++) {
            if (tag.getBoolean("RigNode_" + n)) {
                if (n==4 || n==5 || n==6 || n==11 || n==16 || n==18 || n==21 || n==22) capNodes++;
            }
        }
        
        // 🔥 UPDATE MAX AMMO DYNAMICALLY (+2 Ammo per CAP Node)
        int maxAmmo = 10 + (capNodes * 2);
        tag.putInt("MaxAmmo", maxAmmo);

        if (!tag.contains("Ammo")) {
            tag.putInt("Ammo", maxAmmo);
        }
        
        player.startUsingItem(hand);
        return InteractionResultHolder.consume(stack);
    }

    @Override
    public UseAnim getUseAnimation(ItemStack stack) { 
        return UseAnim.NONE; 
    }

    @Override
    public int getUseDuration(ItemStack stack) { 
        return 72000; 
    }

    @Override
    public AnimatableInstanceCache getAnimatableInstanceCache() { 
        return this.cache; 
    }

    @Override
    public void initializeClient(Consumer<IClientItemExtensions> consumer) {
        consumer.accept(new IClientItemExtensions() {
            private PlasmaCutterRenderer renderer;
            
            @Override
            public BlockEntityWithoutLevelRenderer getCustomRenderer() {
                if (this.renderer == null) this.renderer = new PlasmaCutterRenderer();
                return this.renderer;
            }

            @Override
            public net.minecraft.client.model.HumanoidModel.ArmPose getArmPose(net.minecraft.world.entity.LivingEntity entityLiving, InteractionHand hand, ItemStack itemStack) {
                return net.minecraft.client.model.HumanoidModel.ArmPose.CROSSBOW_HOLD;
            }
        });
    }
}