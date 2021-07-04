package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.util.SearchUtil;
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
import net.minecraftforge.event.ForgeEventFactory;

import java.util.Map;

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
            if (worldIn.getDayTime() % 20 == 0) {
                int searchRange = ServerConfigs.INSTANCE.SUN_STONE_FIRE_RANGE.get();
                int fireChance = ServerConfigs.INSTANCE.SUN_STONE_FIRE_CHANCE.get();

                if (searchRange == 0 || fireChance == 0) {
                    return;
                }

                Map<BlockPos, Block> blockList = SearchUtil.searchBlockWithAABB(worldIn, new AxisAlignedBB(
                        entityIn.getPosX() + searchRange,
                        entityIn.getPosY() + 2,
                        entityIn.getPosZ() + searchRange,
                        entityIn.getPosX() - searchRange,
                        entityIn.getPosY() - 1,
                        entityIn.getPosZ() - searchRange
                ), null, (pos -> SearchUtil.canBurn(worldIn, pos)));

                blockList.keySet().forEach(pos -> {
                    if (worldIn.isAirBlock(pos.up())) {
                        if (worldIn.getRandom().nextInt(100) <= fireChance) {
                            worldIn.setBlockState(pos.up(), Blocks.FIRE.getDefaultState());
                        }
                    }
                });
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
