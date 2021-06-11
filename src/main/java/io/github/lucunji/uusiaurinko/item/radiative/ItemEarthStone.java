package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.tileentity.TransmutingTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ItemEarthStone extends ItemRadiative {
    public ItemEarthStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        soilTransmutation(worldIn, entityIn);
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        soilTransmutation(itemEntity.world, itemEntity);
    }

    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return new BlockParticleData(ParticleTypes.FALLING_DUST, Blocks.DIRT.getDefaultState());
    }

    private static void soilTransmutation(World worldIn, Entity source) {
        if (!worldIn.isRemote() && ServerConfigs.INSTANCE.EARTH_STONE_TRANSMUTATION_ENABLED.get()) {

            int range = ServerConfigs.INSTANCE.EARTH_STONE_TRANSMUTATION_RANGE.get();
            Vector3d centerPos = source.getPositionVec();
            BlockPos.getAllInBox(new AxisAlignedBB(centerPos.subtract(range, range, range), centerPos.add(range, range, range)))
                    .forEach(pos -> {
                        double distanceSq = centerPos.squareDistanceTo(pos.getX(), pos.getY(), pos.getZ());
                        if (distanceSq > range * range) return;

                        BlockState state = worldIn.getBlockState(pos);
                        if (ServerConfigs.INSTANCE.EARTH_STONE_TRANSMUTATION_BLACKLIST.contains(state.getBlock()) ||
                                worldIn.isAirBlock(pos) || state.matchesBlock(Blocks.DIRT) ||
                                state.hasTileEntity() || !state.getFluidState().isEmpty() ||
                                state.getCollisionShape(worldIn, pos, ISelectionContext.dummy()).isEmpty()) return;


                        worldIn.setBlockState(pos, ModBlocks.TRANSMUTING_BLOCK.get().getDefaultState(), 0b10010);
                        worldIn.setTileEntity(pos, new TransmutingTileEntity(state, Blocks.DIRT.getDefaultState(), -MathHelper.sqrt(distanceSq) / 4));
                    });
        }
    }
}
