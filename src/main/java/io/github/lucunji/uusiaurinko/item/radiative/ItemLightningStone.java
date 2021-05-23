package io.github.lucunji.uusiaurinko.item.radiative;

import net.minecraft.block.Blocks;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.*;

public class ItemLightningStone extends ItemRadiative {
    public ItemLightningStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        if (worldIn.isRemote()
                && Minecraft.getInstance().gameSettings.particles != ParticleStatus.MINIMAL
                && worldIn.getGameTime() % 30 == 0) {
            findAllExposedConductorsDFS(worldIn, entityIn.getPosition())
                    .forEach(pos -> worldIn.addOptionalParticle(ParticleTypes.FLAME,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0));
        }
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        World world = itemEntity.world;
        if (world.isRemote()
                && Minecraft.getInstance().gameSettings.particles != ParticleStatus.MINIMAL
                && world.getGameTime() % 30 == 0) {
            findAllExposedConductorsDFS(world, itemEntity.getPosition())
                    .forEach(pos -> world.addOptionalParticle(ParticleTypes.FLAME,
                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0));
        }
    }

    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return null;
    }

    private static List<BlockPos> findAllExposedConductorsDFS(World world, BlockPos startPos) {
        Set<BlockPos> exposedSet = new HashSet<>(256);
        Set<BlockPos> openSet = new HashSet<>();
        Queue<BlockPos> openQueue = new LinkedList<>();
        HashSet<BlockPos> closedSet = new HashSet<>(256);
        openSet.add(startPos);
        openQueue.add(startPos);
        while (!openSet.isEmpty()) {
            BlockPos currentPos = openQueue.poll();
            openSet.remove(currentPos);
            closedSet.add(currentPos);
            if (currentPos.distanceSq(startPos) > 256) continue;
            BlockPos.Mutable neighborPosMut = new BlockPos.Mutable();
            for (int[] offset : NEIGHBORS) {
                neighborPosMut.setAndOffset(currentPos, offset[0], offset[1], offset[2]);
                if (closedSet.contains(neighborPosMut) || openSet.contains(neighborPosMut)) continue;
                if (world.getBlockState(neighborPosMut).matchesBlock(Blocks.WATER)) {
                    BlockPos immutable = neighborPosMut.toImmutable();
                    openSet.add(immutable);
                    openQueue.add(immutable);
                } else if ((offset[0] == 0 && offset[1] == 0 ||
                        offset[1] == 0 && offset[2] == 0 ||
                        offset[0] == 0 && offset[2] == 0) &&
                        world.isAirBlock(neighborPosMut)) {
                    exposedSet.add(neighborPosMut.toImmutable());
                }
            }
        }
        return new ArrayList<>(exposedSet);
    }

    private static int[][] NEIGHBORS = BlockPos.getAllInBox(new BlockPos(-1, -1, -1), new BlockPos(1, 1, 1))
            .map(b -> new int[]{b.getX(), b.getY(), b.getZ()})
            .filter(b -> b[0] != 0 || b[1] != 0 || b[2] != 0)
            .toArray(int[][]::new);
}