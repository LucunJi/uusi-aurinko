package io.github.lucunji.uusiaurinko.item.radiative;

import com.google.common.collect.ImmutableList;
import io.github.lucunji.uusiaurinko.config.ClientConfigs;
import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.effects.ModEffects;
import io.github.lucunji.uusiaurinko.particles.ModParticleTypes;
import io.github.lucunji.uusiaurinko.util.CollectionHelper;
import io.github.lucunji.uusiaurinko.util.ModDamageSource;
import io.github.lucunji.uusiaurinko.util.ModSoundEvents;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.ParticleStatus;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.particles.IParticleData;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.Direction;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.MutablePair;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.util.*;

public class ItemLightningStone extends ItemRadiative {

    public ItemLightningStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        if (entityIn instanceof LivingEntity) {
            LivingEntity creature = (LivingEntity) entityIn;
            creature.addPotionEffect(new EffectInstance(ModEffects.ELECTRICITY_RESISTANCE.get(),
                    2, 0, true, false, true));
        }


        if (worldIn.getGameTime() % ServerConfigs.INSTANCE.LIGHTNING_STONE_ELECTRICITY_INTERVAL.get() != 0) return;
        makeParticleEffects(worldIn, entityIn);
        causeElectricDamage(worldIn, entityIn);
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        World world = itemEntity.world;
        if (world.getGameTime() % ServerConfigs.INSTANCE.LIGHTNING_STONE_ELECTRICITY_INTERVAL.get() != 0) return;
        makeParticleEffects(world, itemEntity);
        causeElectricDamage(world, itemEntity);
    }

    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return ModParticleTypes.SPARK.get();
    }

    /**
     * Make more particles than the super method.
     */
    @Override
    public void makeParticles(ItemEntity entity) {
        super.makeParticles(entity);
        super.makeParticles(entity);
        super.makeParticles(entity);
        super.makeParticles(entity);
        super.makeParticles(entity);
    }

    private void makeParticleEffects(World world, Entity source) {
        if (!world.isRemote()) return;

        int range = ServerConfigs.INSTANCE.LIGHTNING_STONE_ELECTRICITY_RANGE.get();
        if (range <= 0) return;

        ImmutableList<BlockPos> startSet = findNearbyConductors(world, source, 0.5);

        if (startSet.isEmpty()) return;

        // volume is the multiplier of spread distance: max distance = 16 * volume
        world.playSound(source.getPosX(), source.getPosY(), source.getPosZ(), ModSoundEvents.ENTITY_LIGHTNING_STONE_DISCHARGE.get(),
                SoundCategory.BLOCKS, 1.5F, 1.0F + (world.rand.nextFloat() * 0.1F), false);

        double chance = ClientConfigs.INSTANCE.LIGHTNING_STONE_SPARK_PARTICLE_AMOUNT.get();
        if (chance <= 0) return;

        List<ImmutablePair<BlockPos, Direction>> exposure = findExposedConductorsDFS(world, startSet, source.getPositionVec(), range);
        if (exposure.isEmpty()) return;

        Random random = world.getRandom();
        if (Minecraft.getInstance().gameSettings.particles == ParticleStatus.MINIMAL) return;
//        if (exposure.size() <= 32) {
//            exposure.forEach(pair -> spreadSpark(world, pair.left, pair.right, 10, random));
//        } else {
            exposure.forEach(pair -> {
                if (random.nextFloat() < chance) spreadSpark(world, pair.left, pair.right, 10, random);
            });
//        }
    }

    /**
     * Randomly distribute particles over a block.
     * <p>
     * {@code null} direction means the block is transparent so particles can be rendered inside.
     * Note that particles in semi-transparent blocks, such as water, are invisible for players out of water.
     *
     * @param number Number of particles to generate
     */
    private void spreadSpark(World world, BlockPos blockPos, @Nullable Direction direction, int number, Random random) {
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
     * @return A list of Block-Direction pairs. Direction represents the visible face of block, might be {@code null}.
     * {@code null} represents that the block is transparent so particles can be rendered inside.
     */
    private List<ImmutablePair<BlockPos, Direction>>
    findExposedConductorsDFS(World world, final ImmutableList<BlockPos> startSet, final Vector3d startPos, int range) {
        int rangeSq = range * range;
        List<ImmutablePair<BlockPos, Direction>> exposedList = new ArrayList<>(256);
        Queue<BlockPos> frontierQueue = new LinkedList<>(startSet);
        HashSet<BlockPos> discoveredSet = new HashSet<>(startSet);
        while (!frontierQueue.isEmpty()) {
            BlockPos currentPos = frontierQueue.remove();
            BlockPos.Mutable neighborPosMut = new BlockPos.Mutable();
            for (Direction direction : Direction.values()) {
                neighborPosMut.setAndMove(currentPos, direction);
                if (startPos.squareDistanceTo(neighborPosMut.getX(), neighborPosMut.getY(), neighborPosMut.getZ()) > rangeSq) continue;
                if (discoveredSet.contains(neighborPosMut)) continue;

                BlockState neighborState = world.getBlockState(neighborPosMut);
                if (ServerConfigs.INSTANCE.CONDUCTORS.contains(neighborState)) {
                    BlockPos immutable = neighborPosMut.toImmutable();
                    frontierQueue.add(immutable);
                    discoveredSet.add(immutable);
                } else if (!neighborState.isOpaqueCube(world, neighborPosMut)) {
                    exposedList.add(new ImmutablePair<>(currentPos, direction));
                }
                if (!world.getBlockState(currentPos).isOpaqueCube(world, currentPos)) {
                    exposedList.add(new ImmutablePair<>(currentPos, null));
                }
            }
        }
        return exposedList;
    }

    private ImmutableList<BlockPos> findNearbyConductors(World world, Entity entity, double growAmount) {
        return ImmutableList.<BlockPos>builder()
                .addAll(BlockPos.getAllInBox(entity.getBoundingBox().grow(growAmount))
                        .filter(pos -> ServerConfigs.INSTANCE.CONDUCTORS.contains(world.getBlockState(pos)))
                        .map(BlockPos::toImmutable)
                        .iterator()).build();
    }

    private void causeElectricDamage(World world, Entity source) {
        if (source.world.isRemote()) return;

        double shookChance = ServerConfigs.INSTANCE.LIGHTNING_STONE_ELECTRICITY_SHOOK_CHANCE.get();
        int range = ServerConfigs.INSTANCE.LIGHTNING_STONE_ELECTRICITY_RANGE.get();
        if (shookChance <= 0 || range <= 0) return;

        int rangeSq = range * range;

        Vector3d sourcePos = source.getPositionVec();
        ServerWorld serverWorld = (ServerWorld) source.world;

        // prepare blocks to start with
        ImmutableList<BlockPos> startSet = findNearbyConductors(serverWorld, source, 0.5);
        if (startSet.isEmpty()) return;

        // prepare entities to shock
        List<Entity> targets = serverWorld.getEntitiesWithinAABB(LivingEntity.class,
                new AxisAlignedBB(sourcePos.subtract(range, range, range), sourcePos.add(range, range, range)),
                entity -> !entity.isSpectator() &&
                        Math.random() < shookChance &&
                        !(entity instanceof PlayerEntity && ((PlayerEntity) entity).isCreative()) &&
                        !ServerConfigs.INSTANCE.LIGHTNING_STONE_ELECTRICITY_IMMUNE_ENTITY_TYPES.contains(entity.getType()) &&
                        entity.getPositionVec().squareDistanceTo(sourcePos) <= rangeSq);
        if (targets.isEmpty()) return;

        float shockDamage = ServerConfigs.INSTANCE.LIGHTNING_STONE_ELECTRICITY_SHOOK_DAMAGE.get().floatValue();

        // main logic for A*
        // can be further improved by rewriting a more suitable priority queue, but the improvement is too limited so I refuse to do this now
        PriorityQueue<MutablePair<BlockPos, Integer>> frontierQueue = new PriorityQueue<>(Comparator.comparing(Pair::getRight));
        for (BlockPos blockPos : startSet) frontierQueue.add(new MutablePair<>(blockPos, 0));
        HashSet<BlockPos> discoveredSet = new HashSet<>(startSet);

        for (Entity target : targets) {
            List<BlockPos> goals = findNearbyConductors(serverWorld, target, 0.5);
            if (goals.isEmpty()) continue;

            // reuse the already discovered connection conductors in the last iteration
            // so that we do not need to reset the sets to run algorithm from the ground-up
            if (CollectionHelper.hasAnyOf(discoveredSet, goals)) {
                shockEntity(target, shockDamage);
                continue;
            }

            // since we want to reuse the results in the last iteration
            // just re-sort the PQ with new distances
            PriorityQueue<MutablePair<BlockPos, Integer>> newPQ = new PriorityQueue<>(Comparator.comparing(Pair::getRight));
            for (MutablePair<BlockPos, Integer> pair : frontierQueue) {
                pair.setRight(minDistance(pair.left, goals));
                newPQ.add(pair);
            }
            frontierQueue = newPQ;

            // main loop of A* searching
            while (!frontierQueue.isEmpty()) {
                BlockPos currentPos = frontierQueue.remove().left;

                if (goals.contains(currentPos)) {
                    shockEntity(target, shockDamage);
                    break;
                }

                BlockPos.Mutable neighborPosMut = new BlockPos.Mutable();
                for (Direction direction : Direction.values()) {
                    neighborPosMut.setAndMove(currentPos, direction);
                    if (sourcePos.squareDistanceTo(neighborPosMut.getX(), neighborPosMut.getY(), neighborPosMut.getZ()) > rangeSq) continue;
                    if (discoveredSet.contains(neighborPosMut)) continue;

                    BlockState neighborState = world.getBlockState(neighborPosMut);
                    if (ServerConfigs.INSTANCE.CONDUCTORS.contains(neighborState)) {
                        BlockPos immutable = neighborPosMut.toImmutable();
                        discoveredSet.add(immutable);
                        frontierQueue.add(new MutablePair<>(immutable, minDistance(immutable, goals)));
                    }
                }
            }
        }
    }

    private int minDistance(BlockPos blockPos, List<BlockPos> targets) {
        if (targets.isEmpty()) throw new IllegalArgumentException("the second argument cannot be empty");
        int min = Integer.MAX_VALUE;
        for (BlockPos pos : targets) {
            int i0 = blockPos.getX() - pos.getX();
            int i1 = blockPos.getY() - pos.getY();
            int i2 = blockPos.getZ() - pos.getZ();
            int temp = i0 * i0 + i1 * i1 + i2 * i2;
            if (temp < min) min = temp;
        }
        return min;
    }

    /**
     * Shock the entity, giving it some damage and potion effect.
     *
     * It is only a make-do version. Wait to be improved with custom paralysis effect.
     */
    private void shockEntity(Entity entity, float damage) {
        if (entity.attackEntityFrom(ModDamageSource.ELECTRICITY, damage) && entity instanceof LivingEntity) {
            ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.SLOWNESS, 30, 127));
            ((LivingEntity) entity).addPotionEffect(new EffectInstance(Effects.MINING_FATIGUE, 30, 127));
        }
    }
}