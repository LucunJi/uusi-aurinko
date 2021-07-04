package io.github.lucunji.uusiaurinko.util;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3d;
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
        return pos.getY() >= 0 && pos.getY() < 256 && worldIn.isBlockLoaded(pos) && worldIn.getBlockState(pos).getMaterial().isFlammable();
    }

    public static boolean inSphereRange(double centerX, double centerY, double centerZ, double range, double x, double y, double z) {
        double f = centerX - x;
        double f1 = centerY - y;
        double f2 = centerZ - z;
        return (f * f + f1 * f1 + f2 * f2) < (range * range);
    }

    public static boolean inSphereRange(BlockPos center, double range, BlockPos target) {
        return inSphereRange(center.getX(), center.getY(), center.getZ(), range, target.getX(), target.getY(), target.getZ());
    }

    public static boolean inSphereRange(Vector3d center, double range, BlockPos target) {
        return inSphereRange(center.getX(), center.getY(), center.getZ(), range, target.getX(), target.getY(), target.getZ());
    }
}
