package io.github.lucunji.uusiaurinko.item.radiative;

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
                    1, 0, true, false));
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
        if (!worldIn.isRemote() && worldIn.getGameRules().getBoolean(GameRules.DO_FIRE_TICK)) {
            Random random = new Random();
            for (BlockPos pos : randomBlocksAround(self.getPosition(), 1, 1, 1, 3, -1, random)) {
                BlockState blockState = AbstractFireBlock.getFireForPlacement(worldIn, pos);
                int chance = 150 + (worldIn.isBlockinHighHumidity(pos) ? 50 : 0);
                if (worldIn.isAirBlock(pos) && blockState.isValidPosition(worldIn, pos) &&
                        (trySpreadFireTo(worldIn, pos, random, chance) || random.nextFloat() < 0.01)) {
                    worldIn.setBlockState(pos, blockState);
                }
            }
        }
    }

    private void igniteEntities(World worldIn, Entity self) {
        if (!worldIn.isRemote()) {
            worldIn.getEntitiesWithinAABB(Entity.class, self.getBoundingBox().grow(0.5), entity -> entity != self)
                    .forEach(entity -> {
                        if (Item.random.nextFloat() < 0.05)
                            entity.setFire(8);
                    });
        }
    }

    private boolean trySpreadFireTo(World worldIn, BlockPos blockPos, Random random, int chance) {
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
