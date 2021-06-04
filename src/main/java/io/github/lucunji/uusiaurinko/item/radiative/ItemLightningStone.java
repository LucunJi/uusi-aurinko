package io.github.lucunji.uusiaurinko.item.radiative;

import com.google.common.collect.ImmutableList;
import io.github.lucunji.uusiaurinko.effects.ModEffects;
import io.github.lucunji.uusiaurinko.particles.ModParticleTypes;
import io.github.lucunji.uusiaurinko.util.BlockFluidCombinedTag;
import io.github.lucunji.uusiaurinko.util.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.apache.commons.lang3.tuple.ImmutablePair;

import javax.annotation.Nullable;
import java.util.*;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ItemLightningStone extends ItemRadiative {
    private static final ResourceLocation CONDUCTOR_TAG_LOCATION = new ResourceLocation(MODID, "conductor");

    public ItemLightningStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        makeParticleEffects(worldIn, entityIn);

        if (entityIn instanceof LivingEntity) {
            LivingEntity creature = (LivingEntity) entityIn;
            creature.addPotionEffect(new EffectInstance(ModEffects.ELECTRICITY_RESISTANCE.get(),
                    2, 0, true, false, true));
        }
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        makeParticleEffects(itemEntity.world, itemEntity);
    }

    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return ModParticleTypes.SPARK.get();
    }

    private static void makeParticleEffects(World world, Entity source) {
        if (world.isRemote()
                && Minecraft.getInstance().gameSettings.particles != ParticleStatus.MINIMAL
                && (world.getGameTime() & 31) == 0) { // i & 31 == i % 32
            Random random = world.getRandom();
            BlockFluidCombinedTag checker = new BlockFluidCombinedTag(CONDUCTOR_TAG_LOCATION);
            ImmutableList<BlockPos> startSet = ImmutableList.<BlockPos>builder()
                    .addAll(BlockPos.getAllInBox(source.getBoundingBox().grow(0.5))
                            .filter(pos -> checker.contains(world.getBlockState(pos)))
                            .map(BlockPos::toImmutable)
                            .iterator()).build();
            List<ImmutablePair<BlockPos, Direction>> exposure = findExposedConductorsDFS(world, startSet, source.getPositionVec(), 16, checker);
            int size = exposure.size();
            if (size > 0)
                world.playSound(source.getPosX(), source.getPosY(), source.getPosZ(), ModSoundEvents.ENTITY_LIGHTNING_STONE_EMIT,
                        SoundCategory.BLOCKS, 1, 1.0F + (world.rand.nextFloat() * 0.1F), false);
            exposure.forEach(pair -> {
                if (size > 32 && random.nextFloat() < 0.6) return;
                spreadSpark(world, pair.left, pair.right, 10, random);
            });
        }
    }

    /**
     * Randomly distribute particles over a block.
     * <p>
     * {@code null} direction means the block is transparent so particles can be rendered inside.
     * Note that particles in semi-transparent blocks, such as water, are invisible for players out of water.
     *
     * @param number Number of particles to generate
     */
    @OnlyIn(Dist.CLIENT)
    private static void spreadSpark(World world, BlockPos blockPos, @Nullable Direction direction, int number, Random random) {
        int x = blockPos.getX();
        int y = blockPos.getY();
        int z = blockPos.getZ();
        BasicParticleType sparkType = ModParticleTypes.SPARK.get();
        if (direction == null) {
            float dx = random.nextFloat();
            float dy = random.nextFloat();
            float dz = random.nextFloat();
            world.addOptionalParticle(sparkType, x + dx, y + dy, z + dz, 0.04 * random.nextFloat(), 0.04 * random.nextFloat(), 0.04 * random.nextFloat());
            return;
        }
        switch (direction) {
            case UP:
                for (int i = 0; i < number; ++i) {
                    float dx = random.nextFloat();
                    float dy = 0.2F * random.nextFloat() - 0.1F;
                    float dz = random.nextFloat();
                    world.addOptionalParticle(sparkType, x + dx, y + 1 + dy, z + dz, 0, 0.04 * random.nextFloat(), 0);
                }
                break;
            case DOWN:
                for (int i = 0; i < number; ++i) {
                    float dx = random.nextFloat();
                    float dy = 0.2F * random.nextFloat() - 0.1F;
                    float dz = random.nextFloat();
                    world.addOptionalParticle(sparkType, x + dx, y + dy, z + dz, 0, -0.04 * random.nextFloat(), 0);
                }
                break;
            case NORTH:
                for (int i = 0; i < number; ++i) {
                    float dx = random.nextFloat();
                    float dy = random.nextFloat();
                    float dz = 0.2F * random.nextFloat() - 0.1F;
                    world.addOptionalParticle(sparkType, x + dx, y + dy, z + dz, 0, 0, -0.04 * random.nextFloat());
                }
                break;
            case SOUTH:
                for (int i = 0; i < number; ++i) {
                    float dx = random.nextFloat();
                    float dy = random.nextFloat();
                    float dz = 0.2F * random.nextFloat() - 0.1F;
                    world.addOptionalParticle(sparkType, x + dx, y + dy, z + 1 + dz, 0, 0, 0.04 * random.nextFloat());
                }
                break;
            case WEST:
                for (int i = 0; i < number; ++i) {
                    float dx = 0.2F * random.nextFloat() - 0.1F;
                    float dy = random.nextFloat();
                    float dz = random.nextFloat();
                    world.addOptionalParticle(sparkType, x + dx, y + dy, z + dz, -0.04 * random.nextFloat(), 0, 0);
                }
                break;
            case EAST:
                for (int i = 0; i < number; ++i) {
                    float dx = 0.2F * random.nextFloat() - 0.1F;
                    float dy = random.nextFloat();
                    float dz = random.nextFloat();
                    world.addOptionalParticle(sparkType, x + 1 + dx, y + dy, z + dz, 0.04 * random.nextFloat(), 0, 0);
                }
                break;
        }
    }

    /**
     * Find all conductors visible to players.
     *
     * @param startSet A set of blocks to begin with, usually conductors. It has to be immutable to prevent modification.
     * @param startPos The position of entity. It will be the center of search range.
     * @param range    The range of search. Any block outside is ignored.
     * @param checker  A checker for conductivity.
     * @return A list of Block-Direction pairs. Direction represents the visible face of block, might be {@code null}.
     * {@code null} represents that the block is transparent so particles can be rendered inside.
     */
    @OnlyIn(Dist.CLIENT)
    private static List<ImmutablePair<BlockPos, Direction>>
    findExposedConductorsDFS(World world, final ImmutableList<BlockPos> startSet, final Vector3d startPos, int range,
                             BlockFluidCombinedTag checker) {
        int rangeSq = range * range;
        List<ImmutablePair<BlockPos, Direction>> exposedList = new ArrayList<>(256);
        Set<BlockPos> openSet = new HashSet<>(startSet);
        Queue<BlockPos> openQueue = new LinkedList<>(startSet);
        HashSet<BlockPos> closedSet = new HashSet<>(256);
        while (!openSet.isEmpty()) {
            BlockPos currentPos = openQueue.remove();
            openSet.remove(currentPos);
            closedSet.add(currentPos);
            if (startPos.squareDistanceTo(currentPos.getX(), currentPos.getY(), currentPos.getZ()) > rangeSq) continue;
            BlockPos.Mutable neighborPosMut = new BlockPos.Mutable();
            for (Direction direction : Direction.values()) {
                neighborPosMut.setAndMove(currentPos, direction);
                if (closedSet.contains(neighborPosMut) || openSet.contains(neighborPosMut)) continue;

                BlockState neighborState = world.getBlockState(neighborPosMut);
                if (checker.contains(neighborState)) {
                    BlockPos immutable = neighborPosMut.toImmutable();
                    openSet.add(immutable);
                    openQueue.add(immutable);
                } else if (!neighborState.isOpaqueCube(world, neighborPosMut) ||
                        !neighborState.isSolidSide(world, neighborPosMut, direction.getOpposite())) {
                    exposedList.add(new ImmutablePair<>(currentPos, direction));
                }
                if (!world.getBlockState(currentPos).isOpaqueCube(world, currentPos)) {
                    exposedList.add(new ImmutablePair<>(currentPos, null));
                }
            }
        }
        return exposedList;
    }
}