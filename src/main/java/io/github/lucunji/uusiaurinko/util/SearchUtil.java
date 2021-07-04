package io.github.lucunji.uusiaurinko.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author DustW
 */
public class SearchUtil {
    public static Map<BlockPos, Block> searchBlockWithAABB(World world, AxisAlignedBB axisAlignedBB, Predicate<Block> filterBlock, Predicate<BlockPos> filterPos) {
        Map<BlockPos, Block> result = new HashMap<>();

        for (int i = (int) axisAlignedBB.minX; i <= axisAlignedBB.maxX; i++) {
            for (int j = (int) axisAlignedBB.minY; j <= axisAlignedBB.maxY; j++) {
                for (int k = (int) axisAlignedBB.minZ; k <= axisAlignedBB.maxZ; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    Block block = world.getBlockState(pos).getBlock();

                    if (filterBlock == null || filterBlock.test(block)) {
                        if (filterPos == null || filterPos.test(pos)) {
                            result.put(pos, block);
                        }
                    }
                }
            }
        }

        return result;
    }

    public static boolean canBurn(IWorldReader worldIn, BlockPos pos) {
        return (pos.getY() < 0 || pos.getY() >= 256 || worldIn.isBlockLoaded(pos)) && worldIn.getBlockState(pos).getMaterial().isFlammable();
    }
}
