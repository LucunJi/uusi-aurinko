package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.item.ModItems;
import io.github.lucunji.uusiaurinko.util.ModSoundEvents;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ItemParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MutableBoundingBox;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.feature.structure.StructureStart;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.List;
import java.util.stream.Collectors;

public class ItemSunSeed extends ItemRadiative {
    public ItemSunSeed(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        doExplosion(worldIn, entityIn);
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        doExplosion(itemEntity.world, itemEntity);
    }

    /**
     * Changes into sun stone on the blue floor in desert temple.
     * @return {@code true} if any other things in the same tick should be cancelled, including motion and life span.
     */
    @Override
    public boolean onEntityItemUpdate(ItemStack stack, ItemEntity entity) {
        super.onEntityItemUpdate(stack, entity);
        World world = entity.world;
        long dayTime = world.getDayTime();
        BlockPos pos = entity.getPosition();
        double x = entity.getPosX();
        double y = entity.getPosY();
        double z = entity.getPosZ();
        if (!world.isRemote && dayTime >= 5300 && dayTime <= 6700 && world.getLightFor(LightType.SKY, pos) == 15) {
            StructureStart<?> start = ((ServerWorld) world).getStructureManager()
                    .getStructureStart(pos, false, Structure.DESERT_PYRAMID);
            if (start != StructureStart.DUMMY) {
                MutableBoundingBox boundingBox = start.getBoundingBox();
                if (pos.getX() == boundingBox.minX + 10 && pos.getZ() == boundingBox.minZ + 10) {
                    ItemStack newStack = new ItemStack(ModItems.SUN_STONE.get());
                    entity.setItem(newStack);
                    // FIXME: make the particle prettier
                    ((ServerWorld) world).spawnParticle(new ItemParticleData(ParticleTypes.ITEM, newStack),
                            x, y, z, 25, 1, 1, 1, 0);
                    world.playSound(null, x, y, z, ModSoundEvents.ITEM_SUN_STONE_AMBIENT.get(),
                            SoundCategory.BLOCKS, 1F, 1.25F);
                }
            }
        }
        return false;
    }

    private void doExplosion(World worldIn, Entity entityIn) {
        if (!worldIn.isRemote) {
            int interval = ServerConfigs.INSTANCE.SUN_SEED_EXPLOSION_INTERVAL.get();

            if (worldIn.getGameTime() % interval == 0) {
                int searchRange = ServerConfigs.INSTANCE.SUN_SEED_EXPLOSION_RANGE.get();
                double explosionChance = ServerConfigs.INSTANCE.SUN_SEED_EXPLOSION_CHANCE.get();

                if (searchRange == 0) return;

                BlockPos randomPos = findRandomPowderyBlock(worldIn, entityIn.getPositionVec(), searchRange);

                if (randomPos != null && worldIn.getRandom().nextFloat() < explosionChance) {
                    worldIn.setBlockState(randomPos, Blocks.AIR.getDefaultState());
                    worldIn.createExplosion(null, randomPos.getX(), randomPos.getY(), randomPos.getZ(),
                            0.25f, Explosion.Mode.DESTROY);
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

    @Nullable
    static BlockPos findRandomPowderyBlock(World world, Vector3d center, int searchRange) {
        List<BlockPos> blockList = BlockPos.getAllInBox(new AxisAlignedBB(
                center.subtract(searchRange, searchRange, searchRange),
                center.add(searchRange, searchRange, searchRange)
        ))
                .filter(pos -> pos.withinDistance(center, searchRange))
                .filter(pos -> ServerConfigs.INSTANCE.POWDERY_BLOCK.contains(world.getBlockState(pos).getBlock()))
                .map(BlockPos::toImmutable)
                .collect(Collectors.toList());
        if (blockList.isEmpty()) return null;
        return blockList.get(world.getRandom().nextInt(blockList.size()));
    }
}
