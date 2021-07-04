package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.util.SearchUtil;
import io.github.lucunji.uusiaurinko.util.ServerUtil;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import java.util.Map;

public class ItemSunSeed extends ItemRadiative {
    public ItemSunSeed(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        //TODO 看见遗忘者
        boom(worldIn, entityIn);
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        boom(itemEntity.world, itemEntity);
    }

    private void boom(World worldIn, Entity entityIn) {
        if (!worldIn.isRemote) {
            if (worldIn.getDayTime() % 2 == 0) {
                int searchRange = ServerConfigs.INSTANCE.SUN_SEED_TRANSMUTATION_RANGE.get();
                int boomChance = ServerConfigs.INSTANCE.SUN_SEED_EXPLOSION_CHANCE.get();
                float boomRange = ServerConfigs.INSTANCE.SUN_SEED_EXPLOSION_RANGE.get().floatValue();

                if (searchRange == 0 || boomChance == 0 || boomRange == 0) {
                    return;
                }

                Map<BlockPos, Block> blockList = SearchUtil.searchBlockWithAABB(worldIn, new AxisAlignedBB(
                        entityIn.getPosX() + searchRange,
                        entityIn.getPosY() + 2,
                        entityIn.getPosZ() + searchRange,
                        entityIn.getPosX() - searchRange,
                        entityIn.getPosY() - 1,
                        entityIn.getPosZ() - searchRange
                ), (block -> block instanceof FallingBlock), null);

                BlockPos randomPos = blockList.keySet().toArray(new BlockPos[0])[worldIn.getRandom().nextInt(blockList.size())];

                if (worldIn.getRandom().nextInt(100) <= boomChance) {
                    worldIn.setBlockState(randomPos, Blocks.AIR.getDefaultState());
                    worldIn.createExplosion(null, randomPos.getX(), randomPos.getY(), randomPos.getZ(), boomRange, Explosion.Mode.NONE);
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
}
