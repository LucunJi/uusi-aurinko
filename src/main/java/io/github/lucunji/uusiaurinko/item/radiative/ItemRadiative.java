package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.entity.RadiativeItemEntity;
import io.github.lucunji.uusiaurinko.item.ItemBase;
import io.github.lucunji.uusiaurinko.util.ModSoundEvents;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Field;
import java.util.Random;

import static net.minecraft.entity.Entity.horizontalMag;

/**
 * Items which has special effects when held in hands or thrown.
 */
public abstract class ItemRadiative extends ItemBase {
    private static final Logger LOGGER = LogManager.getLogger();

    private static Field ageFieldCache = null;

    public ItemRadiative(Properties properties) {
        super(properties);
    }

    public abstract void radiationInWorld(ItemStack stack, ItemEntity itemEntity);

    public abstract void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand);

    public abstract IParticleData inWorldParticleType(ItemEntity itemEntity);

    @Override
    public void inventoryTick(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected) {
        if (!entityIn.isSpectator() && entityIn instanceof LivingEntity) {
            LivingEntity livingEntity = (LivingEntity) entityIn;
            boolean inMainHand = livingEntity.getHeldItemMainhand() == stack;
            if ((inMainHand && isSelected || livingEntity.getHeldItemOffhand() == stack) && !isInactive(stack)) {
                this.radiationInHand(stack, worldIn, entityIn, inMainHand);
            }
        }
    }

    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        // make particles
        if (entity.world.isRemote()) {
            this.makeParticles(entity);
        }

        if (!isInactive(stack)) this.radiationInWorld(stack, entity);
        return false;
    }

    /**
     * Returns true only to trigger {@code createEntity} method
     *
     * @return true
     */
    @Override
    public boolean hasCustomEntity(ItemStack stack) {
        return true;
    }

    /**
     * Make the item entity immortal.
     * Returns {@code null} to keep using the modified the {@code oldEntity}.
     *
     * @return null
     */
    @Override
    public Entity createEntity(World world, Entity oldEntity, ItemStack itemstack) {
        if (oldEntity instanceof ItemEntity) {
            ItemEntity oldItemEntity = (ItemEntity) oldEntity;
            RadiativeItemEntity newItemEntity = new RadiativeItemEntity(oldItemEntity);
            newItemEntity.setPickupDelay(40);
            trySetAge(newItemEntity, -32768);
            return newItemEntity;
        }
        return null;
    }

    /**
     * Throw the item with greater speed than simply drop the item when click right mouse button.
     */
    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        ItemStack itemStack = playerIn.getHeldItem(handIn);
        worldIn.playSound(null, playerIn.getPosX(), playerIn.getPosY(), playerIn.getPosZ(), ModSoundEvents.ENTITY_RADIATIVE_STONE_THROW.get(), SoundCategory.NEUTRAL, 0.5F, 0.4F / (random.nextFloat() * 0.4F + 0.8F));
        if (!worldIn.isRemote()) {
            Vector3d userPosVec = playerIn.getPositionVec();
            ItemEntity itemEntity = new ItemEntity(worldIn, userPosVec.x, playerIn.getPosYEye() - 0.3F, userPosVec.z, itemStack.copy());
            itemEntity.setPickupDelay(40);
            itemEntity.setThrowerId(playerIn.getUniqueID());
            setDirectionAndMovement(itemEntity, playerIn, playerIn.rotationPitch, playerIn.rotationYaw,
                    0.0F, 1.2F, 1.0F);
            worldIn.addEntity(itemEntity);
        }
        if (!playerIn.abilities.isCreativeMode) {
            itemStack.shrink(1);
        }
        return ActionResult.resultSuccess(itemStack);
    }

    public void setInactive(ItemStack stack, boolean inactive) {
        stack.getOrCreateTag().putBoolean("Inactive", inactive);
    }

    @SuppressWarnings({"ConstantConditions", "BooleanMethodIsAlwaysInverted"})
    public boolean isInactive(ItemStack itemStack) {
        return itemStack.hasTag() && itemStack.getTag().getBoolean("Inactive");
    }

    protected void makeParticles(ItemEntity entity) {
        IParticleData particleData = inWorldParticleType(entity);
        if (particleData != null) {
            Random random = entity.world.getRandom();
            if (random.nextFloat() < 0.07) {
                double posX = entity.getPosX() - 0.2 + random.nextFloat() * 0.4;
                double posY = entity.getPosY() + random.nextFloat() * 0.4;
                double posZ = entity.getPosZ() - 0.2 + random.nextFloat() * 0.4;
                double xSpeed = random.nextFloat() * 0.02 - 0.01;
                double ySpeed = random.nextFloat() * 0.02 + 0.02;
                double zSpeed = random.nextFloat() * 0.02 - 0.01;

                // only runs in client
                entity.world.addOptionalParticle(particleData, posX, posY, posZ, xSpeed, ySpeed, zSpeed);
            }
        }
    }

    private static void setDirectionAndMovement(Entity thrown, Entity thrower, float pitch, float yaw, float rotation, float velocity, float inaccuracy) {
        float f = -MathHelper.sin(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
        float f1 = -MathHelper.sin((pitch + rotation) * ((float) Math.PI / 180F));
        float f2 = MathHelper.cos(yaw * ((float) Math.PI / 180F)) * MathHelper.cos(pitch * ((float) Math.PI / 180F));
        shoot(thrown, f, f1, f2, velocity, inaccuracy);
        Vector3d vector3d = thrower.getMotion();
        thrown.setMotion(thrown.getMotion().add(vector3d.x, thrower.isOnGround() ? 0.0D : vector3d.y, vector3d.z));
    }

    private static void shoot(Entity thrown, double x, double y, double z, float velocity, float inaccuracy) {
        Random random = new Random();
        Vector3d vector3d = (new Vector3d(x, y, z)).normalize()
                .add(random.nextGaussian() * (double) 0.0075F * (double) inaccuracy,
                        random.nextGaussian() * (double) 0.0075F * (double) inaccuracy,
                        random.nextGaussian() * (double) 0.0075F * (double) inaccuracy)
                .scale(velocity);
        thrown.setMotion(vector3d);
        float f = MathHelper.sqrt(horizontalMag(vector3d));
        thrown.rotationYaw = (float) (MathHelper.atan2(vector3d.x, vector3d.z) * (double) (180F / (float) Math.PI));
        thrown.rotationPitch = (float) (MathHelper.atan2(vector3d.y, f) * (double) (180F / (float) Math.PI));
        thrown.prevRotationYaw = thrown.rotationYaw;
        thrown.prevRotationPitch = thrown.rotationPitch;
    }

    /**
     * Attempts to set age of item entity with reflection.
     * The reflective field will be cached after the first successful operation.
     */
    private void trySetAge(ItemEntity instance, int age) {
        try {
            if (ageFieldCache == null) {
                // age
                Field temp = ObfuscationReflectionHelper.findField(ItemEntity.class, "field_70292_b");
                temp.setAccessible(true);
                ageFieldCache = temp;
            }
            ageFieldCache.set(instance, age);
        } catch (ObfuscationReflectionHelper.UnableToFindFieldException e) {
            LOGGER.error("Could not find the obfuscated field 'field_70292_b'(age) in class 'ItemEntity'", e);
        } catch (SecurityException e) {
            LOGGER.error("Could not make the obfuscated field 'field_70292_b'(age) in class 'ItemEntity' accessible", e);
        } catch (IllegalAccessException e) {
            LOGGER.error("Could not give new value to the obfuscated field 'field_70292_b'(age) in class 'ItemEntity'", e);
        }
    }
}
