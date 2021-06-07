package io.github.lucunji.uusiaurinko.item.radiative;

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
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class ItemEarthStone extends ItemRadiative {
    public ItemEarthStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        soilTransmutation(worldIn, entityIn, 4);
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        soilTransmutation(itemEntity.world, itemEntity, 4);
    }

    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return new BlockParticleData(ParticleTypes.FALLING_DUST, Blocks.DIRT.getDefaultState());
    }

    private static void soilTransmutation(World worldIn, Entity source, int range) {
        if (!worldIn.isRemote()) {
            Vector3d centerPos = source.getPositionVec();
            BlockPos.getAllInBox(new AxisAlignedBB(centerPos.subtract(range, range, range), centerPos.add(range, range, range)))
                    .forEach(pos -> {
                        if (centerPos.squareDistanceTo(pos.getX(), pos.getY(), pos.getZ()) > range * range) return;
                        BlockState state = worldIn.getBlockState(pos);
                        if (!state.getFluidState().isEmpty() || worldIn.isAirBlock(pos) ||
                                state.getCollisionShape(worldIn, pos, ISelectionContext.dummy()).isEmpty()) return;
                        worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());
                    });
        }
    }
}
