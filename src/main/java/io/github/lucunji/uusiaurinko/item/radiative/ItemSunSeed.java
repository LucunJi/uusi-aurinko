package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class ItemSunSeed extends ItemRadiative {
    public ItemSunSeed(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        doExplosion(worldIn, entityIn);
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        doExplosion(itemEntity.world, itemEntity);
    }

    private void doExplosion(World worldIn, Entity entityIn) {
        if (!worldIn.isRemote) {
            int interval = ServerConfigs.INSTANCE.SUN_SEED_EXPLOSION_INTERVAL.get();

            if (worldIn.getGameTime() % interval == 0) {
                int searchRange = ServerConfigs.INSTANCE.SUN_SEED_EXPLOSION_RANGE.get();
                double explosionChance = ServerConfigs.INSTANCE.SUN_SEED_EXPLOSION_CHANCE.get();

                if (searchRange == 0) return;

                BlockPos randomPos = findRandomPowderyBlock(worldIn, entityIn.getPositionVec(), searchRange);

                if (randomPos != null && worldIn.getRandom().nextFloat() < explosionChance) {
                    worldIn.setBlockState(randomPos, Blocks.AIR.getDefaultState());
                    worldIn.createExplosion(null, randomPos.getX(), randomPos.getY(), randomPos.getZ(),
                            0.25f, Explosion.Mode.DESTROY);
                }
            }
        }
    }

    @Override
    public boolean isImmuneToExplosions() {
        return true;
    }

    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return null;
    }

    @Nullable
    static BlockPos findRandomPowderyBlock(World world, Vector3d center, int searchRange) {
        List<BlockPos> blockList = BlockPos.getAllInBox(new AxisAlignedBB(
                center.subtract(searchRange, searchRange, searchRange),
                center.add(searchRange, searchRange, searchRange)
        ))
                .filter(pos -> pos.withinDistance(center, searchRange))
                .filter(pos -> ServerConfigs.INSTANCE.POWDERY_BLOCK.contains(world.getBlockState(pos).getBlock()))
                .map(BlockPos::toImmutable)
                .collect(Collectors.toList());
        if (blockList.isEmpty()) return null;
        return blockList.get(world.getRandom().nextInt(blockList.size()));
    }
}
