package io.github.lucunji.uusiaurinko.entity;

import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ProjectileHelper;
import net.minecraft.entity.projectile.ThrowableEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.network.NetworkHooks;

import javax.annotation.Nullable;

public class ThrownTabletEntity extends ThrowableEntity {
    private boolean inGround;
    private boolean leftOwner;

    public ThrownTabletEntity(EntityType<? extends ThrownTabletEntity> entityEntityType, World world) {
        super(entityEntityType, world);
    }

    /**
     * Borrowed code from {@link net.minecraft.entity.projectile.AbstractArrowEntity}.
     * <p>
     * It does not call the super method since it is more kind of an arrow entity
     * rather than something like a snowball.
     */
    public void tick() {
        // ProjectileEntity.tick()
        if (!this.leftOwner) {
            this.leftOwner = this.leaveOwner();
        }

        // Entity.tick()
        if (!this.world.isRemote) {
            this.setFlag(6, this.isGlowing());
        }
        this.baseTick();

        // AbstractArrowEntity.tick() (modified version)
        Vector3d motionVec = this.getMotion();

        if (!this.inGround) {
            this.prevRotationPitch = this.rotationPitch;
            this.rotationPitch += motionVec.length() * 30;
        }

        // check if the entity is in ground (only set inGround to true uni-directionally)
        BlockPos blockpos = this.getPosition();
        BlockState blockstate = this.world.getBlockState(blockpos);
        if (!this.world.isAirBlock(blockpos) && !this.noClip) {
            VoxelShape voxelshape = blockstate.getCollisionShapeUncached(this.world, blockpos);
            if (!voxelshape.isEmpty()) {
                Vector3d positionVec = this.getPositionVec();

                for (AxisAlignedBB axisalignedbb : voxelshape.toBoundingBoxList()) {
                    if (axisalignedbb.offset(blockpos).contains(positionVec)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        if (this.inGround && !this.noClip) {
            if (this.shouldLeaveGround()) {
                this.leaveGround();
            }
        } else {
            // compute the next position by doing raytrace
            Vector3d positionVec = this.getPositionVec();
            Vector3d nextVec = positionVec.add(motionVec);
            RayTraceResult rayTraceResult = this.world.rayTraceBlocks(new RayTraceContext(
                    positionVec,
                    nextVec,
                    RayTraceContext.BlockMode.COLLIDER,
                    RayTraceContext.FluidMode.NONE,
                    this));
            if (rayTraceResult.getType() != RayTraceResult.Type.MISS) {
                nextVec = rayTraceResult.getHitVec();
            }

            if (this.isAlive()) {
                EntityRayTraceResult entityRayTraceResult = this.rayTraceEntities(positionVec, nextVec);
                if (entityRayTraceResult != null) {
                    rayTraceResult = entityRayTraceResult;
                }

                // filter out cases when a player tries to attack another in the same team
                if (rayTraceResult.getType() == RayTraceResult.Type.ENTITY) {
                    //noinspection ConstantConditions
                    Entity entityHit = ((EntityRayTraceResult) rayTraceResult).getEntity();
                    Entity shooter = this.getShooter();
                    if (entityHit instanceof PlayerEntity && shooter instanceof PlayerEntity
                            && !((PlayerEntity) shooter).canAttackPlayer((PlayerEntity) entityHit)) {
                        rayTraceResult = null;
                    }
                }

                // hit entity or block
                if (rayTraceResult != null && rayTraceResult.getType() != RayTraceResult.Type.MISS
                        && !this.noClip && !ForgeEventFactory.onProjectileImpact(this, rayTraceResult)) {
                    this.onImpact(rayTraceResult);
                    this.isAirBorne = true;
                }
            }

            motionVec = this.getMotion();
            double newX = this.getPosX() + motionVec.getX();
            double newY = this.getPosY() + motionVec.getY();
            double newZ = this.getPosZ() + motionVec.getZ();

            this.setMotion(motionVec.scale(this.getFluidDrag()));
            if (!this.hasNoGravity() && !this.noClip) {
                Vector3d motion = this.getMotion();
                this.setMotion(motion.x, motion.y - this.getGravityVelocity(), motion.z);
            }

            this.setPosition(newX, newY, newZ);
            this.doBlockCollisions();
        }
    }

    /**
     * Borrowed from {@link net.minecraft.entity.projectile.AbstractArrowEntity#func_234593_u_}
     */
    private boolean shouldLeaveGround() {
        return this.inGround && this.world.hasNoCollisions((new AxisAlignedBB(this.getPositionVec(), this.getPositionVec())).grow(0.06D));
    }

    /**
     * Borrowed from {@link net.minecraft.entity.projectile.AbstractArrowEntity#func_234594_z_}
     */
    private void leaveGround() {
        this.inGround = false;
        this.setMotion(this.getMotion().mul(
                this.rand.nextFloat() * 0.2F,
                this.rand.nextFloat() * 0.2F,
                this.rand.nextFloat() * 0.2F));
    }

    /**
     * Gets the EntityRayTraceResult representing the entity hit.
     * <p>
     * Borrowed from {@link net.minecraft.entity.projectile.AbstractArrowEntity#rayTraceEntities}
     */
    @Nullable
    private EntityRayTraceResult rayTraceEntities(Vector3d startVec, Vector3d endVec) {
        return ProjectileHelper.rayTraceEntities(
                this.world,
                this,
                startVec,
                endVec,
                this.getBoundingBox().expand(this.getMotion()).grow(1.0D), this::raytraceFilter);
    }

    /**
     * Borrowed from {@link net.minecraft.entity.projectile.AbstractArrowEntity#func_230298_a_}
     */
    private boolean raytraceFilter(Entity entityIn) {
        if (!entityIn.isSpectator() && entityIn.isAlive() && entityIn.canBeCollidedWith()) {
            Entity shooter = this.getShooter();
            return shooter == null || this.leftOwner || !shooter.isRidingSameEntity(entityIn);
        } else {
            return false;
        }
    }

    /**
     * Borrowed from {@link net.minecraft.entity.projectile.AbstractArrowEntity#move}
     */
    public void move(MoverType typeIn, Vector3d pos) {
        super.move(typeIn, pos);
        if (typeIn != MoverType.SELF && this.shouldLeaveGround()) this.leaveGround();
    }

    /**
     * Borrowed from {@link net.minecraft.entity.projectile.ProjectileEntity#func_234615_h_}
     */
    private boolean leaveOwner() {
        Entity shooter = this.getShooter();
        if (shooter != null) {
            for(Entity entity1 : this.world.getEntitiesInAABBexcluding(
                    this,
                    this.getBoundingBox().expand(this.getMotion()).grow(1.0D),
                    (entity) -> !entity.isSpectator() && entity.canBeCollidedWith())) {

                if (entity1.getLowestRidingEntity() == shooter.getLowestRidingEntity()) {
                    return false;
                }
            }
        }
        return true;
    }

    protected float getFluidDrag() {
        return this.isInWater() ? 0.6F : 0.99F;
    }

    @Override
    protected void onEntityHit(EntityRayTraceResult result) {
    }

    /**
     * When hitting a block.
     */
    @Override
    protected void func_230299_a_(BlockRayTraceResult result) {
        super.func_230299_a_(result);

        // prevent the entity from straightly going into the ground
        Vector3d hitVec = result.getHitVec().subtract(this.getPosX(), this.getPosY(), this.getPosZ());
        this.setMotion(hitVec);
        Vector3d hitVecScaled = hitVec.normalize().scale(0.05F);
        this.setRawPosition(this.getPosX() - hitVecScaled.x, this.getPosY() - hitVecScaled.y, this.getPosZ() - hitVecScaled.z);
        this.inGround = true;
        // TODO: play sound
    }

    /**
     * Set yaw and pitch in my own way.
     */
    @Override
    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vector3d vector3d = (new Vector3d(x, y, z)).normalize().add(
                this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy,
                this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy,
                this.rand.nextGaussian() * (double) 0.0075F * (double) inaccuracy)
                .scale(velocity);
        this.setMotion(vector3d);
        this.prevRotationYaw = this.rotationYaw = (float) Math.random() * 360;
        this.prevRotationPitch = this.rotationPitch = 0;
    }

    /**
     * Sets motion only. The super method also sets yaw and pitch.
     */
    @Override
    public void setVelocity(double x, double y, double z) {
        this.setMotion(x, y, z);
    }

    @Override
    public void writeAdditional(CompoundNBT compound) {
        super.writeAdditional(compound);
        compound.putBoolean("inGround", this.inGround);
        if (this.leftOwner) {
            compound.putBoolean("LeftOwner", true);
        }
    }

    @Override
    public void readAdditional(CompoundNBT compound) {
        super.readAdditional(compound);
        this.inGround = compound.getBoolean("inGround");
        this.leftOwner = compound.getBoolean("LeftOwner");
    }

    @Override
    protected void registerData() {
    }

    /**
     * Immune to explosion.
     */
    @Override
    public boolean attackEntityFrom(DamageSource source, float amount) {
        if (source.isExplosion()) return false;
        return super.attackEntityFrom(source, amount);
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }
}
