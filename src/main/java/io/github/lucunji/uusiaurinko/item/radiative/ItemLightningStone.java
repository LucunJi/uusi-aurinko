package io.github.lucunji.uusiaurinko.item.radiative;

import com.google.common.collect.Sets;
import io.github.lucunji.uusiaurinko.particles.ModParticleTypes;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.ImmutablePair;

import java.util.*;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ItemLightningStone extends ItemRadiative {
    private static final ResourceLocation CONDUCTOR_TAG_LOCATION = new ResourceLocation(MODID, "conductor");

    public ItemLightningStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
//        if (worldIn.isRemote()
//                && Minecraft.getInstance().gameSettings.particles != ParticleStatus.MINIMAL
//                && worldIn.getGameTime() % 30 == 0) {
//            findAllExposedConductorsDFS(worldIn, entityIn.getPosition(), 16)
//                    .forEach(pos -> worldIn.addOptionalParticle(ParticleTypes.FLAME,
//                            pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, 0, 0, 0));
//        }
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        World world = itemEntity.world;
        if (world.isRemote()
                && Minecraft.getInstance().gameSettings.particles != ParticleStatus.MINIMAL
                && world.getGameTime() % 30 == 0) {
            Random random = world.getRandom();
            findAllExposedConductorsDFS(world, itemEntity.getPosition(), 16)
                    .forEach(pair -> genSparkParticles(world, pair.left, pair.right, 5, random));
        }
    }

    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return ModParticleTypes.SPARK.get();
    }

    /**
     * Randomly distribute particles over a block.
     * @param number Number of particles to generate
     */
    @OnlyIn(Dist.CLIENT)
    private static void genSparkParticles(World world, BlockPos blockPos, Direction direction, int number, Random random) {
        if (random.nextFloat() < 0.6) return;
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        BasicParticleType sparkType = ModParticleTypes.SPARK.get();
        switch (direction) {
            case UP:
                for (int i = 0; i < number; ++i) {
                    float dx = random.nextFloat();
                    float dy = 0.2F * random.nextFloat() - 0.1F;
                    float dz = random.nextFloat();
                    world.addOptionalParticle(sparkType, x + dx, y + 1 + dy, z + dz, 0,  0.04 * random.nextFloat(), 0);
                }
                break;
            case DOWN:
                for (int i = 0; i < number; ++i) {
                    float dx = random.nextFloat();
                    float dy = 0.2F * random.nextFloat() - 0.1F;
                    float dz = random.nextFloat();
                    world.addOptionalParticle(sparkType, x + dx, y + dy, z + dz, 0,  -0.04 * random.nextFloat(), 0);
                }
                break;
            case NORTH:
                for (int i = 0; i < number; ++i) {
                    float dx = random.nextFloat();
                    float dy = random.nextFloat();
                    float dz = 0.2F * random.nextFloat() - 0.1F;
                    world.addOptionalParticle(sparkType, x + dx, y + dy, z + dz, 0,  0, -0.04 * random.nextFloat());
                }
                break;
            case SOUTH:
                for (int i = 0; i < number; ++i) {
                    float dx = random.nextFloat();
                    float dy = random.nextFloat();
                    float dz = 0.2F * random.nextFloat() - 0.1F;
                    world.addOptionalParticle(sparkType, x + dx, y + dy, z + 1 + dz, 0,  0, 0.04 * random.nextFloat());
                }
                break;
            case WEST:
                for (int i = 0; i < number; ++i) {
                    float dx = 0.2F * random.nextFloat() - 0.1F;
                    float dy = random.nextFloat();
                    float dz = random.nextFloat();
                    world.addOptionalParticle(sparkType, x + dx, y + dy, z + dz, -0.04 * random.nextFloat(),  0, 0);
                }
                break;
            case EAST:
                for (int i = 0; i < number; ++i) {
                    float dx = 0.2F * random.nextFloat() - 0.1F;
                    float dy = random.nextFloat();
                    float dz = random.nextFloat();
                    world.addOptionalParticle(sparkType, x + 1 + dx, y + dy, z + dz, 0.04 * random.nextFloat(),  0, 0);
                }
                break;
        }
    }

    @OnlyIn(Dist.CLIENT)
    private static List<ImmutablePair<BlockPos, Direction>> findAllExposedConductorsDFS(World world, BlockPos startPos, int range) {
        Set<Block> conductorBlocks = Sets.newIdentityHashSet();
        Optional.ofNullable(BlockTags.getCollection().get(CONDUCTOR_TAG_LOCATION))
                .map(ITag::getAllElements).ifPresent(conductorBlocks::addAll);
        Set<Fluid> conductorFluids = Sets.newIdentityHashSet();
        Optional.ofNullable(FluidTags.getCollection().get(CONDUCTOR_TAG_LOCATION))
                .map(ITag::getAllElements).ifPresent(conductorFluids::addAll);

        int rangeSq = range * range;
        List<ImmutablePair<BlockPos, Direction>> exposedList = new ArrayList<>(256);
        Set<BlockPos> openSet = new HashSet<>();
        Queue<BlockPos> openQueue = new LinkedList<>();
        HashSet<BlockPos> closedSet = new HashSet<>(256);
        openSet.add(startPos);
        openQueue.add(startPos);
        while (!openSet.isEmpty()) {
            BlockPos currentPos = openQueue.remove();
            openSet.remove(currentPos);
            closedSet.add(currentPos);
            if (currentPos.distanceSq(startPos) > rangeSq) continue;
            BlockPos.Mutable neighborPosMut = new BlockPos.Mutable();
            for (Direction direction : Direction.values()) {
                neighborPosMut.setAndMove(currentPos, direction);
                if (closedSet.contains(neighborPosMut) || openSet.contains(neighborPosMut)) continue;
                if (conductorBlocks.contains(world.getBlockState(neighborPosMut).getBlock()) ||
                    conductorFluids.contains(world.getFluidState(neighborPosMut).getFluid())) {
                    BlockPos immutable = neighborPosMut.toImmutable();
                    openSet.add(immutable);
                    openQueue.add(immutable);
                } else if (world.isAirBlock(neighborPosMut)) {
                    exposedList.add(new ImmutablePair<>(currentPos, direction));
                }
            }
        }
        return exposedList;
    }
}