package io.github.lucunji.uusiaurinko.block;

import io.github.lucunji.uusiaurinko.tileentity.PedestalTileEntity;
import io.github.lucunji.uusiaurinko.util.MathUtil;
import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class PedestalBlock extends ContainerBlock {
    public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
    public static final BooleanProperty SPECIAL_ITEM = BooleanProperty.create("special_item");
    public static final DirectionProperty FACING = HorizontalBlock.HORIZONTAL_FACING;
    private static final VoxelShape SHAPE_UP = Block.makeCuboidShape(2, 14, 2, 14, 16, 14);
    private static final VoxelShape SHAPE_MID = Block.makeCuboidShape(4, 3, 4, 12, 14, 12);
    private static final VoxelShape SHAPE_BELOW = Block.makeCuboidShape(2, 0, 2, 14, 3, 14);

    public PedestalBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getDefaultState().with(POWERED, false).with(FACING, Direction.NORTH));
    }

    /**
     * If the player is holding something, try to place it on pedestal.
     * Otherwise, try to get item from the pedestal.
     */
    @SuppressWarnings("deprecation")
    @Override
    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        // must click on the upper surface
        if (hit.getFace() != Direction.UP || !MathUtil.containsInclusive(
                SHAPE_UP.getBoundingBox(),
                hit.getHitVec().subtract(pos.getX(), pos.getY(), pos.getZ())
        )) {
            return ActionResultType.PASS;
        }

        if (!worldIn.isRemote) {
            ItemStack heldItem = player.getHeldItem(handIn);
            TileEntity te = worldIn.getTileEntity(pos);
            if (te instanceof PedestalTileEntity) {
                PedestalTileEntity tileEntity = (PedestalTileEntity) te;
                ItemStack pedestalStack = tileEntity.getStackInSlot(0);
                BlockState newState = null;
                if (pedestalStack.isEmpty() && !heldItem.isEmpty()) {
                    tileEntity.setInventorySlotContents(0, heldItem.copy());
                    player.setHeldItem(handIn, ItemStack.EMPTY);
                } else if (!pedestalStack.isEmpty() && heldItem.isEmpty()) {
                    player.setHeldItem(handIn, pedestalStack.copy());
                    tileEntity.setInventorySlotContents(0, ItemStack.EMPTY);
                }
            }
        }
        return ActionResultType.SUCCESS;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean hasComparatorInputOverride(BlockState state) {
        return true;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getComparatorInputOverride(BlockState blockState, World worldIn, BlockPos pos) {
        return Container.calcRedstoneFromInventory((IInventory) worldIn.getTileEntity(pos));
    }

    @SuppressWarnings("deprecation")
    @Override
    public int getStrongPower(BlockState blockState, IBlockReader blockAccess, BlockPos pos, Direction side) {
        if (blockState.get(POWERED) && side == Direction.UP) {
            return blockState.get(SPECIAL_ITEM) ? 15 : 7;
        }
        return 0;
    }

    /**
     * Drops out item when broken.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void onReplaced(BlockState state, World worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
        if (!state.matchesBlock(newState.getBlock())) {
            TileEntity tileentity = worldIn.getTileEntity(pos);
            if (tileentity instanceof IInventory) {
                InventoryHelper.dropInventoryItems(worldIn, pos, (IInventory) tileentity);
            }

            super.onReplaced(state, worldIn, pos, newState, isMoving);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(POWERED, SPECIAL_ITEM, FACING);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        return this.getDefaultState().with(FACING, context.getPlacementHorizontalFacing().rotateY());
    }

    @SuppressWarnings("deprecation")
    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        return VoxelShapes.or(SHAPE_UP, SHAPE_MID, SHAPE_BELOW);
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

    private void updateNeighbors(World world, BlockPos pos) {
        world.notifyNeighborsOfStateChange(pos, this);
        world.notifyNeighborsOfStateChange(pos.offset(Direction.DOWN), this);
    }
}
