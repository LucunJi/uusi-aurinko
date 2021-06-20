package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import net.minecraft.block.AbstractFireBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.GameRules;
import net.minecraft.world.World;

import java.util.Random;

/**
 * This stone:
 * gives holder fire immunity, ignites surrounding entities, and place fire randomly in the world.
 * Fire blocks tend to appear near flammable blocks.
 * Fire immunity only appears when someone holds the stone.
 */
public class ItemFireStone extends ItemRadiative {
    public ItemFireStone(Item.Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        igniteBlocks(worldIn, entityIn);
        igniteEntities(worldIn, entityIn);

        // grant fire immunity
        if (entityIn instanceof LivingEntity) {
            LivingEntity creature = (LivingEntity) entityIn;
            creature.addPotionEffect(new EffectInstance(Effects.FIRE_RESISTANCE,
                    2, 0, true, false, true));
        }
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        igniteBlocks(itemEntity.world, itemEntity);
        if (!itemEntity.cannotPickup()) // use pickup delay to prevent item cast effects on player immediately
            igniteEntities(itemEntity.world, itemEntity);
    }

    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return ParticleTypes.FLAME;
    }

    private void igniteBlocks(World worldIn, Entity self) {
        if (worldIn.isRemote() || !worldIn.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) return;
        int range = ServerConfigs.INSTANCE.FIRE_STONE_IGNITE_BLOCK_RANGE.get();
        if (range <= 0) return;
        Random random = worldIn.getRandom();
        BlockPos centerPos = self.getPosition();
        int improbability = ServerConfigs.INSTANCE.FIRE_STONE_IGNITE_FLAMMABLE_IMPROBABILITY.get();
        double baseChange = ServerConfigs.INSTANCE.FIRE_STONE_IGNITE_BLOCK_BASE_CHANCE.get();
        for (BlockPos pos : BlockPos.getRandomPositions(random, 1,
                centerPos.getX() - 1, centerPos.getY() - 1, centerPos.getZ() - 1,
                centerPos.getX() + 1, centerPos.getY() + 3, centerPos.getZ() + 1)) {
            BlockState blockState = AbstractFireBlock.getFireForPlacement(worldIn, pos);
            if (worldIn.isAirBlock(pos) && blockState.isValidPosition(worldIn, pos) &&
                    (random.nextFloat() < baseChange || tryIgniteFlammable(worldIn, pos, random,
                            improbability + (worldIn.isBlockinHighHumidity(pos) ? 50 : 0)))
            ) {
                worldIn.setBlockState(pos, blockState);
            }
        }
    }

    private void igniteEntities(World worldIn, Entity self) {
        if (worldIn.isRemote()) return;
        double range = ServerConfigs.INSTANCE.FIRE_STONE_IGNITE_ENTITY_RANGE.get();
        if (range <= 0) return;
        worldIn.getEntitiesWithinAABB(Entity.class, self.getBoundingBox().grow(range), entity -> entity != self)
                .forEach(entity -> {
                    if (Item.random.nextFloat() < 0.1)
                        entity.setFire(8);
                });
    }

    private boolean tryIgniteFlammable(World worldIn, BlockPos blockPos, Random random, int chance) {
        for (Direction direction : Direction.values()) {
            BlockPos posOffset = blockPos.offset(direction);
            int flammability = worldIn.getBlockState(posOffset).getFlammability(worldIn, posOffset, direction.getOpposite());
            if (random.nextInt(chance) < flammability) { // prob. = flammability / chance
                return true;
            }
        }
        return false;
    }
}
