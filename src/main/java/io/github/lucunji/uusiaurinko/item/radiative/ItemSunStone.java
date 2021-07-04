package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import static io.github.lucunji.uusiaurinko.item.radiative.ItemSunSeed.findRandomPowderyBlock;

//TODO 凋零效果
public class ItemSunStone extends ItemRadiative {
    public ItemSunStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        fire(worldIn, entityIn);
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        fire(itemEntity.world, itemEntity);
    }

    private void fire(World worldIn, Entity entityIn) {
        if (!worldIn.isRemote) {
            int interval = ServerConfigs.INSTANCE.SUN_STONE_FIRE_INTERVAL.get();

            if (worldIn.getGameTime() % interval == 0) {
                int searchRange = ServerConfigs.INSTANCE.SUN_STONE_FIRE_RANGE.get();
                double fireChance = ServerConfigs.INSTANCE.SUN_STONE_FIRE_CHANCE.get();

                if (searchRange == 0 || fireChance == 0) return;

                BlockPos randomPos = findRandomPowderyBlock(worldIn, entityIn.getPositionVec(), searchRange);

                if (randomPos != null && worldIn.getRandom().nextFloat() < fireChance) {
                    worldIn.setBlockState(randomPos, Blocks.FIRE.getDefaultState());
                }
            }
        }
    }

    @Override
    public boolean isImmuneToExplosions() {
        return false;
    }

    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return null;
    }
}
