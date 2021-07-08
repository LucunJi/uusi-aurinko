package io.github.lucunji.uusiaurinko.block;

import io.github.lucunji.uusiaurinko.tileentity.PedestalTileEntity;
import net.minecraft.block.*;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;

import javax.annotation.Nullable;

public class PedestalBlock extends ContainerBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;

    public PedestalBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(POWERED, false).with(FACING, Direction.NORTH));
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED);
        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().rotateY());
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.or(
                Block.makeCuboidShape(2, 0, 2, 14, 3, 4),
                Block.makeCuboidShape(4, 3, 4, 12, 14, 12),
                Block.makeCuboidShape(2, 14, 2, 14, 16, 14)
        );
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState rotate(BlockState state, Rotation rot) {
        return state.with(FACING, rot.rotate(state.get(FACING)));
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(IBlockReader worldIn) {
        return new PedestalTileEntity();
    }
}
