package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.block.CampfireBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.common.util.BlockSnapshot;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.event.ForgeEventFactory;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * This stone:
 * grants holder fire immunity and water breathing, temporarily solidify lava and hurts surrounding water-sensitive mobs.
 * Fire immunity only appears when someone holds the stone. It also distinguishes any fire on body.
 * Fire on body will be distinguished when player goes out of fire source(lava or fire block).
 * Water breathing only appears when someone holds the stone and stand in water.
 * The semisolid lava will melt after some time like frosted ice.
 * Water-sensitive mobs includes blazemen and endermen.
 */
public class ItemWaterStone extends ItemRadiative {
    public ItemWaterStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        chillLava(itemEntity.world, itemEntity.world.getRandom(), itemEntity);
        hurtsFireSensitiveCreatures(itemEntity.world, itemEntity);
        extinguishFire(itemEntity.world, itemEntity);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        chillLava(worldIn, worldIn.getRandom(), entityIn);
        hurtsFireSensitiveCreatures(entityIn.world, entityIn);
        extinguishFire(worldIn, entityIn);

        entityIn.forceFireTicks(0);
        if (entityIn instanceof LivingEntity) {
            LivingEntity creature = (LivingEntity) entityIn;
            creature.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE,
                    2, 0, true, false, true));

            if (creature.isInWater()) {
                creature.addPotionEffect(new EffectInstance(Effects.WATER_BREATHING,
                        2, 0, true, false, true));
            }
        }
    }

    /**
     * Code borrowed from {@code FrostWalkerEnchantment.freezeNearby()}.
     * It performs less checks than the original code and thus more dangerous.
     * <p>
     * Only runs in server side.
     */
    private void chillLava(World worldIn, Random random, Entity placer) {
        if (worldIn.isRemote()) return;

        int range = ServerConfigs.INSTANCE.WATER_STONE_SOLIDIFY_LAVA_RANGE.get();
        if (range <= 0) return;

        BlockState blockstate = ModBlocks.SEMISOLID_LAVA.get().getDefaultState();
        BlockPos placerBlockPos = placer.getPosition();
        Vector3d placerDoublePos = placer.getPositionVec();
        for (BlockPos mutableInBox : BlockPos.getAllInBoxMutable(
                placerBlockPos.add(-range, -range, -range),
                placerBlockPos.add(range, range, range))) {
            if (mutableInBox.withinDistance(placerDoublePos, range)) {
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

    private void extinguishFire(World worldIn, Entity placer) {
        if (worldIn.isRemote()) return;

        int range = ServerConfigs.INSTANCE.WATER_STONE_EXTINGUISH_FIRE_RANGE.get();
        if (range <= 0) return;

        BlockPos placerBlockPos = placer.getPosition();
        Vector3d placerDoublePos = placer.getPositionVec();
        for (BlockPos mutableInBox : BlockPos.getAllInBoxMutable(
                placerBlockPos.add(-range, -range, -range),
                placerBlockPos.add(range, range, range))) {
            if (!mutableInBox.withinDistance(placerDoublePos, range)) continue;
            BlockState state = worldIn.getBlockState(mutableInBox);
            if (state.isIn(BlockTags.FIRE)) {
                worldIn.playEvent(Constants.WorldEvents.FIRE_EXTINGUISH_SOUND, mutableInBox, 0);
                worldIn.removeBlock(mutableInBox, false);
            } else if (CampfireBlock.isLit(state)) {
                worldIn.playEvent(Constants.WorldEvents.FIRE_EXTINGUISH_SOUND, mutableInBox, 0);
                CampfireBlock.extinguish(worldIn, mutableInBox, state); // only distinguishes the tile entity
                worldIn.setBlockState(mutableInBox, state.with(CampfireBlock.LIT, false));
            }

        }
    }

    private void hurtsFireSensitiveCreatures(World worldIn, Entity emitter) {
        if (worldIn.isRemote()) return;
        double range = ServerConfigs.INSTANCE.WATER_STONE_DAMAGE_RANGE.get();
        if (range <= 0) return;

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
