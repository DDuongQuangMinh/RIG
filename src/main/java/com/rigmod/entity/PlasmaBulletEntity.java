package com.rigmod.entity;

import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.EntityHitResult;
import net.minecraftforge.network.NetworkHooks;

public class PlasmaBulletEntity extends AbstractArrow {

    private static final EntityDataAccessor<Boolean> IS_VERTICAL = SynchedEntityData.defineId(PlasmaBulletEntity.class, EntityDataSerializers.BOOLEAN);

    public PlasmaBulletEntity(EntityType<? extends AbstractArrow> type, Level level) {
        super(type, level);
        this.setNoGravity(true); 
    }

    public PlasmaBulletEntity(Level level, LivingEntity shooter) {
        super(ModEntities.PLASMA_BULLET.get(), shooter, level);
        this.setNoGravity(true);
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(IS_VERTICAL, false);
    }

    public void setVertical(boolean vertical) {
        this.entityData.set(IS_VERTICAL, vertical);
    }

    public boolean isVertical() {
        return this.entityData.get(IS_VERTICAL);
    }

    @Override
    public void addAdditionalSaveData(CompoundTag tag) {
        super.addAdditionalSaveData(tag);
        tag.putBoolean("IsVertical", this.isVertical());
    }

    @Override
    public void readAdditionalSaveData(CompoundTag tag) {
        super.readAdditionalSaveData(tag);
        this.setVertical(tag.getBoolean("IsVertical"));
    }

    @Override
    protected ItemStack getPickupItem() {
        return ItemStack.EMPTY; 
    }

    @Override
    public Packet<ClientGamePacketListener> getAddEntityPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public void tick() {
        super.tick();
        // Delete the bullet if it flies into the sky for too long
        if (this.tickCount > 100) this.discard(); 
    }

    // ==========================================
    // 🔥 THE FIX: IMPACT BEHAVIOR
    // ==========================================

    @Override
    protected void onHitBlock(BlockHitResult result) {
        super.onHitBlock(result);
        
        if (!this.level().isClientSide()) {
            // Spawn a burst of cyan electrical sparks exactly where it hit the wall!
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.ELECTRIC_SPARK, 
                    result.getLocation().x, result.getLocation().y, result.getLocation().z, 
                    10, 0.1, 0.1, 0.1, 0.05);
            
            // Instantly delete the 3D model so it doesn't stick
            this.discard(); 
        }
    }

    @Override
    protected void onHitEntity(EntityHitResult result) {
        super.onHitEntity(result); // This applies the damage to the mob
        
        if (!this.level().isClientSide()) {
            // Spawn sparks on the zombie/mob when you shoot them
            ((ServerLevel) this.level()).sendParticles(ParticleTypes.ELECTRIC_SPARK, 
                    result.getLocation().x, result.getLocation().y, result.getLocation().z, 
                    15, 0.2, 0.2, 0.2, 0.05);
            
            // Instantly delete the bullet so it doesn't float on their body
            this.discard(); 
        }
    }
}