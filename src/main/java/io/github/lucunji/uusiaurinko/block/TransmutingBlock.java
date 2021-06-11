package io.github.lucunji.uusiaurinko.block;

import io.github.lucunji.uusiaurinko.tileentity.TransmutingTileEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.client.particle.DiggingParticle;
import net.minecraft.client.particle.ParticleManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootContext;
import net.minecraft.loot.LootParameters;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.IWorldReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;

/**
 * Similar to {@link net.minecraft.block.MovingPistonBlock}, this block is used when one block
 * gets transmuted into another block and need some fade-in and fade-out transition effects.
 */
public class TransmutingBlock extends Block {
    public TransmutingBlock(Properties properties) {
        super(properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.INVISIBLE;
    }

    @Override
    public float getExplosionResistance(BlockState state, IBlockReader world, BlockPos pos, Explosion explosion) {
        BlockState blockState = getBlockStateFrom(world, pos);
        return blockState != null ? blockState.getExplosionResistance(world, pos, explosion) : 0.5F;
    }

    @SuppressWarnings("deprecation")
    @Override
    public float getPlayerRelativeBlockHardness(BlockState state, PlayerEntity player, IBlockReader worldIn, BlockPos pos) {
        BlockState blockState = getBlockStateFrom(worldIn, pos);
        return blockState != null ? blockState.getPlayerRelativeBlockHardness(player, worldIn, pos) : 0.5F;
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        BlockState blockState = getBlockStateFrom(worldIn, pos);
        return blockState != null ? blockState.getShape(worldIn, pos, context) : VoxelShapes.empty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        BlockState blockState = getBlockStateFrom(worldIn, pos);
        return blockState != null ? blockState.getCollisionShape(worldIn, pos) : VoxelShapes.empty();
    }

    @SuppressWarnings("deprecation")
    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder) {
        BlockState blockState = getBlockStateFrom(builder.getWorld(), new BlockPos(builder.assertPresent(LootParameters.ORIGIN)));
        return blockState != null ? blockState.getDrops(builder) : Collections.emptyList();
    }

    @Override
    public boolean hasTileEntity(BlockState state) {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) {
        return new TransmutingTileEntity();
    }

    @Override
    public SoundType getSoundType(BlockState state, IWorldReader world, BlockPos pos, @Nullable Entity entity) {
        BlockState fromState = getBlockStateFrom(world, pos);
        if (fromState == null) return super.getSoundType(state, world, pos, entity);
        return fromState.getSoundType(world, pos, entity);
    }

    @Override
    public boolean addLandingEffects(BlockState state1, ServerWorld serverWorld, BlockPos pos, BlockState state2,
                                     LivingEntity entity, int numberOfParticles) {
        BlockState fromState = getBlockStateFrom(entity.world, pos);
        if (fromState == null) return false;

        serverWorld.spawnParticle(new BlockParticleData(ParticleTypes.BLOCK, fromState).setPos(pos),
                pos.getX(), pos.getY(), pos.getZ(), numberOfParticles, 0, 0, 0, 0.15);
        return true;
    }

    @Override
    public boolean addRunningEffects(BlockState state, World world, BlockPos pos, Entity entity) {
        Vector3d vector3d = entity.getMotion();
        BlockState fromState = getBlockStateFrom(world, pos);
        if (fromState == null) return false;

        world.addParticle(new BlockParticleData(ParticleTypes.BLOCK, fromState).setPos(pos),
                entity.getPosX() + (Math.random() - 0.5) * entity.getWidth(),
                entity.getPosY() + 0.1D, entity.getPosZ() + (Math.random() - 0.5) * entity.getWidth(),
                vector3d.x * -4, 1.5, vector3d.z * -4);
        return true;
    }

    /**
     * Code borrowed from {@code ParticleManager.addBlockHitEffects()}
     */
    @Override
    public boolean addHitEffects(BlockState state, World world, RayTraceResult target, ParticleManager manager) {
        if (!world.isRemote) return true;
        BlockRayTraceResult result = (BlockRayTraceResult) target;
        BlockPos pos = result.getPos();
        BlockState blockstate = getBlockStateFrom(world, pos);
        if (blockstate == null) return false;

        Direction side = result.getFace();
        if (blockstate.getRenderType() != BlockRenderType.INVISIBLE) {
            int i = pos.getX();
            int j = pos.getY();
            int k = pos.getZ();
            float f = 0.1F;
            AxisAlignedBB axisalignedbb = blockstate.getShape(world, pos).getBoundingBox();
            double d0 = i + Math.random() * (axisalignedbb.maxX - axisalignedbb.minX - 0.2) + 0.1 + axisalignedbb.minX;
            double d1 = j + Math.random() * (axisalignedbb.maxY - axisalignedbb.minY - 0.2) + 0.1 + axisalignedbb.minY;
            double d2 = k + Math.random() * (axisalignedbb.maxZ - axisalignedbb.minZ - 0.2) + 0.1 + axisalignedbb.minZ;
            switch (side) {
                case DOWN:
                    d1 = (double) j + axisalignedbb.minY - (double) 0.1F;
                    break;
                case UP:
                    d1 = (double) j + axisalignedbb.maxY + (double) 0.1F;
                    break;
                case NORTH:
                    d2 = (double) k + axisalignedbb.minZ - (double) 0.1F;
                    break;
                case SOUTH:
                    d2 = (double) k + axisalignedbb.maxZ + (double) 0.1F;
                    break;
                case WEST:
                    d0 = (double) i + axisalignedbb.minX - (double) 0.1F;
                    break;
                case EAST:
                    d0 = (double) i + axisalignedbb.maxX + (double) 0.1F;
                    break;
            }

            manager.addEffect(
                    new DiggingParticle((ClientWorld) world, d0, d1, d2, 0, 0, 0, blockstate)
                            .setBlockPos(pos).multiplyVelocity(0.2F).multiplyParticleScaleBy(0.6F));
        }
        return true;
    }

    /**
     * Code borrowed from {@code ParticleManager.addBlockDestroyEffects()}
     */
    @Override
    public boolean addDestroyEffects(BlockState state, World world, BlockPos pos, ParticleManager manager) {
        if (!world.isRemote) return true;

        BlockState blockstate = getBlockStateFrom(world, pos);
        if (blockstate == null) return false;

        VoxelShape voxelshape = blockstate.getShape(world, pos);
        double d0 = 0.25;
        voxelshape.forEachBox((x1, y1, z1, x2, y2, z2) -> {
            double d1 = Math.min(1, x2 - x1);
            double d2 = Math.min(1, y2 - y1);
            double d3 = Math.min(1, z2 - z1);
            int i = Math.max(2, MathHelper.ceil(d1 / 0.25));
            int j = Math.max(2, MathHelper.ceil(d2 / 0.25));
            int k = Math.max(2, MathHelper.ceil(d3 / 0.25));

            for (int l = 0; l < i; ++l) {
                for (int i1 = 0; i1 < j; ++i1) {
                    for (int j1 = 0; j1 < k; ++j1) {
                        double d4 = ((double) l + 0.5) / (double) i;
                        double d5 = ((double) i1 + 0.5) / (double) j;
                        double d6 = ((double) j1 + 0.5) / (double) k;
                        double d7 = d4 * d1 + x1;
                        double d8 = d5 * d2 + y1;
                        double d9 = d6 * d3 + z1;
                        manager.addEffect(
                                new DiggingParticle((ClientWorld) world, pos.getX() + d7, pos.getY() + d8, pos.getZ() + d9,
                                        d4 - 0.5D, d5 - 0.5D, d6 - 0.5D, blockstate)
                                        .setBlockPos(pos));
                    }
                }
            }

        });
        return true;
    }

    @Nullable
    private static TransmutingTileEntity getTransmutingTileEntity(World world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof TransmutingTileEntity ? (TransmutingTileEntity) tileEntity : null;
    }

    @Nullable
    private static BlockState getBlockStateFrom(IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof TransmutingTileEntity ? ((TransmutingTileEntity) tileEntity).getBlockStateFrom() : null;
    }

    @Nullable
    private static BlockState getBlockStateTo(IBlockReader world, BlockPos pos) {
        TileEntity tileEntity = world.getTileEntity(pos);
        return tileEntity instanceof TransmutingTileEntity ? ((TransmutingTileEntity) tileEntity).getBlockStateTo() : null;
    }
}
