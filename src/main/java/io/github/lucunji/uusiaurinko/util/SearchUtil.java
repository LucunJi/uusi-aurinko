package io.github.lucunji.uusiaurinko.util;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

/**
 * @author DustW
 */
public class SearchUtil {
    public static Map<BlockPos, Block> searchBlockWithAABB(World world, AxisAlignedBB axisAlignedBB, Predicate<Block> filter) {
        Map<BlockPos, Block> result = new HashMap<>();

        for (int i = (int) axisAlignedBB.minX; i <= axisAlignedBB.maxX; i++) {
            for (int j = (int) axisAlignedBB.minY; j <= axisAlignedBB.maxY; j++) {
                for (int k = (int) axisAlignedBB.minZ; k <= axisAlignedBB.maxZ; k++) {
                    BlockPos pos = new BlockPos(i, j, k);
                    Block block = world.getBlockState(pos).getBlock();

                    if (filter.test(block)) {
                        result.put(pos, block);
                    }
                }
            }
        }

        return result;
    }
}
