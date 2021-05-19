package io.github.lucunji.uusiaurinko.entity;

import io.github.lucunji.uusiaurinko.item.radiative.ItemRadiative;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.IPacket;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkHooks;

import java.util.UUID;

/**
 * Entity of thrown Noita stone, such as firestone.
 * Most code related to data and motion is ported from
 * https://github.com/LucunJi/NoitaCraft/blob/2d6430fbcceef8d53357a8744435140896fe7fc9/src/main/java/io/github/lucunji/noitacraft/entity/spell/SpellEntityBase.java
 * which should be similar to {@link ItemEntity}'s behavior
 */
public class ThrownRockEntity extends Entity {
    protected UUID ownerUUID;
    protected boolean inGround;
    protected int age;
    private static final DataParameter<ItemStack> ITEM = EntityDataManager.createKey(ItemEntity.class, DataSerializers.ITEMSTACK);

    public ThrownRockEntity(EntityType<?> entityTypeIn, World worldIn) {
        super(entityTypeIn, worldIn);
    }

    /********** Motion Mechanics **********/

    @Override
    public void tick() {
        super.tick();

        this.prevPosX = this.getPosX();
        this.prevPosY = this.getPosY();
        this.prevPosZ = this.getPosZ();
        this.setMotion(this.getMotion().add(0.0D, -0.08D, 0.0D));

        // idk what is the exact use of noClip, it seems to affect push-out-of-block behaviors
        if (this.world.isRemote) {
            this.noClip = false;
        } else {
            this.noClip = !this.world.hasNoCollisions(this);
            if (this.noClip) {
                this.pushOutOfBlocks(this.getPosX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0D, this.getPosZ());
            }
        }

        // make it move due to momentum
        if (!this.onGround || horizontalMag(this.getMotion()) > (double)1.0E-5F || (this.ticksExisted + this.getEntityId()) % 4 == 0) {
            this.move(MoverType.SELF, this.getMotion());
            float horizontalFactor = 0.98F;
            if (this.onGround) {
                BlockPos pos = new BlockPos(this.getPosX(), this.getPosY() - 1.0D, this.getPosZ());
                // multiplier of 0.1F makes the rock immediately stop when touches ground
                horizontalFactor = this.world.getBlockState(pos).getSlipperiness(this.world, pos, this) * 0.1F;
            }

            this.setMotion(this.getMotion().mul(horizontalFactor, 0.98D, horizontalFactor));
            if (this.onGround) {
                this.setMotion(this.getMotion().mul(1.0D, -0.5D, 1.0D));
            }
        }

        // landing logic
        BlockPos blockpos = this.getPosition();
        BlockState blockstate = this.world.getBlockState(blockpos);
        if (!this.world.isAirBlock(blockpos)) {
            VoxelShape voxelshape = blockstate.getCollisionShape(this.world, blockpos);
            if (!voxelshape.isEmpty()) {
                Vector3d vec3d1 = this.getPositionVec();

                for(AxisAlignedBB axisalignedbb : voxelshape.toBoundingBoxList()) {
                    if (axisalignedbb.offset(blockpos).contains(vec3d1)) {
                        this.inGround = true;
                        break;
                    }
                }
            }
        }

        // motion in water
        Vector3d motionVec = this.getMotion();
        if (this.isInWater()) {
            this.setMotion(motionVec.scale(this.getWaterDrag()));
        }

        // radiation in world
        ItemStack itemStack = this.getItem();
        Item item = itemStack.getItem();
        if (item instanceof ItemRadiative) {
            ((ItemRadiative) item).radiationInWorld(itemStack, this);
        }
    }

    protected float getWaterDrag() {
        return 0.6f;
    }

    public void setDirectionAndMovement(Entity projectile, float x, float y, float z, float velocity, float inaccuracy) {
        float f = -MathHelper.sin(y * ((float)Math.PI / 180F)) * MathHelper.cos(x * ((float)Math.PI / 180F));
        float f1 = -MathHelper.sin((x + z) * ((float)Math.PI / 180F));
        float f2 = MathHelper.cos(y * ((float)Math.PI / 180F)) * MathHelper.cos(x * ((float)Math.PI / 180F));
        this.shoot((double)f, (double)f1, (double)f2, velocity, inaccuracy);
        Vector3d vector3d = projectile.getMotion();
        this.setMotion(this.getMotion().add(vector3d.x, projectile.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
    }

    public void shoot(double x, double y, double z, float velocity, float inaccuracy) {
        Vector3d vector3d = (new Vector3d(x, y, z)).normalize().add(this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy, this.rand.nextGaussian() * (double)0.0075F * (double)inaccuracy).scale((double)velocity);
        this.setMotion(vector3d);
        float f = MathHelper.sqrt(horizontalMag(vector3d));
        this.rotationYaw = (float)(MathHelper.atan2(vector3d.x, vector3d.z) * (double)(180F / (float)Math.PI));
        this.rotationPitch = (float)(MathHelper.atan2(vector3d.y, (double)f) * (double)(180F / (float)Math.PI));
        this.prevRotationYaw = this.rotationYaw;
        this.prevRotationPitch = this.rotationPitch;
    }

    /********** User Interaction **********/

    @Override
    public ActionResultType processInitialInteract(PlayerEntity player, Hand hand) {
        if (!player.world.isRemote()) {
            ItemStack itemStack = this.getItem();
            if (player.canPickUpItem(itemStack)) {
                player.addItemStackToInventory(itemStack);
                this.setItem(ItemStack.EMPTY);
                this.remove();
                return ActionResultType.CONSUME;
            }
        }
        return ActionResultType.PASS;
    }

    @Override
    public boolean canBeCollidedWith() {
        return true;
    }

    /********** Data Managements **********/

    @Override
    protected void registerData() {
        this.getDataManager().register(ITEM, ItemStack.EMPTY);
    }

    @Override
    public void notifyDataManagerChange(DataParameter<?> key) {
        super.notifyDataManagerChange(key);
        if (ITEM.equals(key)) {
            this.getItem().setAttachedEntity(this);
        }
    }

    @Override
    protected void readAdditional(CompoundNBT compound) {
        if (compound.hasUniqueId("Owner")) this.ownerUUID = compound.getUniqueId("Owner");
        this.inGround = compound.getBoolean("inGround");
        this.age = compound.getInt("Age");
    }

    @Override
    protected void writeAdditional(CompoundNBT compound) {
        if (this.ownerUUID != null) compound.putUniqueId("Owner", ownerUUID);
        compound.putBoolean("inGround", inGround);
        compound.putInt("Age", this.age);
    }

    public void setOwnerUUID(UUID ownerUUID) {
        this.ownerUUID = ownerUUID;
    }

    @Override
    public IPacket<?> createSpawnPacket() {
        return NetworkHooks.getEntitySpawningPacket(this);
    }

    public ItemStack getItem() {
        return this.getDataManager().get(ITEM);
    }

    public void setItem(ItemStack stack) {
        this.getDataManager().set(ITEM, stack);
    }
}
