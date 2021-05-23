package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This stone:
 * grants holder fire immunity and water breathing, temporarily solidify lava and hurts surrounding water-sensitive mobs.
 * Fire immunity only appears when someone holds the stone. It also distinguishes any fire on body.
 * Fire on body will be distinguished when player goes out of fire source(lava or fire block).
 * Water breathing only appears when someone holds the stone and stand in water.
 * The solidified lava will melt after some time like frosted ice.
 * Water-sensitive mobs includes blazemen and endermen.
 */
public class ItemWaterStone extends ItemRadiative {
    public ItemWaterStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        chillLava(itemEntity.world, new Random(), itemEntity, 2);
        hurtsFireSensitiveCreatures(itemEntity.world, itemEntity, 1);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        chillLava(worldIn, new Random(), entityIn, 2);
        hurtsFireSensitiveCreatures(entityIn.world, entityIn, 1);

        entityIn.forceFireTicks(0);
        if (entityIn instanceof LivingEntity) {
            LivingEntity creature = (LivingEntity) entityIn;
            creature.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE,
                    1, 0, true, false));

            if (creature.isInWater()) {
                creature.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING,
                        1, 0, true, false));
            }
        }
    }

    /**
     * Code borrowed from {@code FrostWalkerEnchantment.freezeNearby()}.
     * It performs less checks than the original code and thus more dangerous.
     * <p>
     * Only runs in server side.
     */
    private void chillLava(World worldIn, Random random, Entity placer, int range) {
        if (worldIn.isRemote()) return;

        BlockState blockstate = ModBlocks.SEMISOLID_LAVA.get().getDefaultState();
        int rangeAdjusted = Math.min(16, range);
        BlockPos placerBlockPos = placer.getPosition();
        Vector3d placerDoublePos = placer.getPositionVec();
        for (BlockPos mutableInBox : BlockPos.getAllInBoxMutable(
                placerBlockPos.add(-rangeAdjusted, -rangeAdjusted, -rangeAdjusted),
                placerBlockPos.add(rangeAdjusted, rangeAdjusted, rangeAdjusted))) {
            if (mutableInBox.withinDistance(placerDoublePos, rangeAdjusted)) {
                BlockState blockStateInBox = worldIn.getBlockState(mutableInBox);
                boolean isLavaSource = blockStateInBox.getFluidState().isTagged(FluidTags.LAVA)
                        && blockStateInBox.getFluidState().isSource();
                if (isLavaSource &&
                        blockstate.isValidPosition(worldIn, mutableInBox) &&
                        !ForgeEventFactory.onBlockPlace(placer, BlockSnapshot.create(worldIn.getDimensionKey(), worldIn, mutableInBox), Direction.UP)) {
                    worldIn.setBlockState(mutableInBox, blockstate);
                    worldIn.getPendingBlockTicks().scheduleTick(mutableInBox, ModBlocks.SEMISOLID_LAVA.get(), MathHelper.nextInt(random, 60, 120));
                }
            }
        }
    }

    private void hurtsFireSensitiveCreatures(World worldIn, Entity emitter, double range) {
        if (worldIn.isRemote()) return;
        worldIn.getEntitiesWithinAABB(LivingEntity.class, emitter.getBoundingBox().grow(range))
                .forEach(entity -> {
                    if (entity.isWaterSensitive())
                        entity.attackEntityFrom(DamageSource.DROWN, 1.0F);
                });
    }

    @Nullable
    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return itemEntity.isOnGround() ? ParticleTypes.DRIPPING_WATER : ParticleTypes.FALLING_WATER;
    }
}
