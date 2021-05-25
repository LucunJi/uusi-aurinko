package io.github.lucunji.uusiaurinko.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemStack;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tags.FluidTags;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

import javax.annotation.Nullable;
import java.util.Random;

/**
 * A MagmaBlock melting like a FrostedIce without interactions with bubble columns.
 * Codes are borrowed from both classes.
 */
public class SemisolidLavaBlock extends Block {
    public static final IntegerProperty AGE = BlockStateProperties.AGE_0_3;

    /**
     * The world event type for playing {@code SoundEvents.BLOCK_LAVA_EXTINGUISH} and
     * {@code ParticleTypes.LARGE_SMOKE}. Also used when lava fluid block solidifies.
     */
    private static final int LAVA_EXTINGUISH_WORLD_EVENT = 1501;

    public SemisolidLavaBlock(Properties properties) {
        super(properties);
        this.setDefaultState(this.getStateContainer().getBaseState().with(AGE, 0));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(AGE);
    }

    /**
     * Turns into lava without silk touch.
     * Even if there is silk touch, the loot table fives no block drop.
     */
    @Override
    public void harvestBlock(World worldIn, PlayerEntity player, BlockPos pos, BlockState state, @Nullable TileEntity te, ItemStack stack) {
        super.harvestBlock(worldIn, player, pos, state, te, stack);
        if (EnchantmentHelper.getEnchantmentLevel(Enchantments.SILK_TOUCH, stack) == 0) {
            worldIn.setBlockState(pos, Blocks.LAVA.getDefaultState());
        }
    }

    /**
     * Same as the method in {@link net.minecraft.block.MagmaBlock}.
     */
    @Override
    public void onEntityWalk(World worldIn, BlockPos pos, Entity entityIn) {
        if (!entityIn.isImmuneToFire() && entityIn instanceof LivingEntity && !EnchantmentHelper.hasFrostWalker((LivingEntity) entityIn)) {
            entityIn.attackEntityFrom(DamageSource.HOT_FLOOR, 1.0F);
        }
        super.onEntityWalk(worldIn, pos, entityIn);
    }

    //********** Melting Logic Starts *********

    @SuppressWarnings("deprecation")
    @Override
    public void randomTick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        tick(state, worldIn, pos, random);
    }

    /**
     * If the block itself melts into lava, schedule all neighbor solidified lava blocks.
     * Otherwise, schedule itself to melt again later.
     */
    @SuppressWarnings("deprecation")
    @Override
    public void tick(BlockState state, ServerWorld worldIn, BlockPos pos, Random random) {
        if ((random.nextInt(3) == 0 || this.nearHotLiquidIsolatedEnough(worldIn, pos, 4))
                && this.furtherMelt(worldIn, state, pos)) {
            BlockPos.Mutable mutable = new BlockPos.Mutable();
            for (Direction direction : Direction.values()) {
                mutable.setAndMove(pos, direction);
                BlockState neighborState = worldIn.getBlockState(mutable);
                if (neighborState.matchesBlock(this) && !furtherMelt(worldIn, neighborState, mutable)) {
                    worldIn.getPendingBlockTicks().scheduleTick(mutable, this, getScheduleTickInterval(worldIn, random));
                }
            }
        } else {
            worldIn.getPendingBlockTicks().scheduleTick(pos, this, getScheduleTickInterval(worldIn, random));
        }
    }

    private int getScheduleTickInterval(World world, Random random) {
        if (world.getDimensionType().isUltrawarm())
            return MathHelper.nextInt(random, 10, 20);
        return MathHelper.nextInt(random, 20, 40);
    }

    /**
     * Melts the solidified block further.
     * If the block does not melt into a lava fluid, it changes without block updates.
     * If it melt into a lava fluid, it updates.
     * Client is always synced in both case.
     *
     * @return {@code false} if the block melts into lava, or {@code true} otherwise
     */
    private boolean furtherMelt(ServerWorld world, BlockState blockState, BlockPos blockPos) {
        int age = blockState.get(AGE);
        if (age < 3) {
            world.setBlockState(blockPos, blockState.with(AGE, age + 1), 2);
            return false;
        } else {
            becomeLava(blockState, world, blockPos);
            return true;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void neighborChanged(BlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
        if (checkWater(worldIn, pos)
                && blockIn == this && this.nearHotLiquidIsolatedEnough(worldIn, pos, 1)) {
            becomeLava(state, worldIn, pos);
        }

        super.neighborChanged(state, worldIn, pos, blockIn, fromPos, isMoving);
    }

    /**
     * A solidified lava block further melts if it touches a liquid block hot enough(such as lava)
     * and is isolated from other solidified lava blocks.
     */
    private boolean nearHotLiquidIsolatedEnough(World world, BlockPos blockPos, int maxSolidifiedBlocks) {
        int lavaTemp = Fluids.LAVA.getAttributes().getTemperature();
        BlockPos.Mutable neighborPos = new BlockPos.Mutable();
        int solidifiedLavaCount = 0;
        boolean nearHotLiquid = false;
        for (Direction direction : Direction.values()) {
            neighborPos.setAndMove(blockPos, direction);
            BlockState neighborState = world.getBlockState(neighborPos);
            if (!nearHotLiquid && neighborState.getFluidState().getFluid().getAttributes().getTemperature() >= lavaTemp)
                nearHotLiquid = true;
            if (neighborState.matchesBlock(this)
                    && ++solidifiedLavaCount >= maxSolidifiedBlocks)
                return false;
        }
        return true;
    }

    private void becomeLava(BlockState blockState, World world, BlockPos blockPos) {
        world.setBlockState(blockPos, Blocks.LAVA.getDefaultState());
        world.neighborChanged(blockPos, Blocks.LAVA, blockPos);
    }

    //********** Melting Logic Ends *********

    @SuppressWarnings("deprecation")
    @Override
    public void onBlockAdded(BlockState state, World worldIn, BlockPos pos, BlockState oldState, boolean isMoving) {
        checkWater(worldIn, pos);
    }

    /**
     * @return {@code true} if the block survives, or {@code false} if turns into an obsidian.
     */
    private boolean checkWater(World worldIn, BlockPos pos) {
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        for (Direction direction : Direction.values()) {
            mutable.setAndMove(pos, direction);
            if (worldIn.getBlockState(mutable).getFluidState().isTagged(FluidTags.WATER)) {
                worldIn.playEvent(LAVA_EXTINGUISH_WORLD_EVENT, pos, 0);
                worldIn.setBlockState(pos, Blocks.OBSIDIAN.getDefaultState());
                return false;
            }
        }
        return true;
    }

    /**
     * Gives nothing when the player presses pick-block button towards this block.
     */
    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
        return ItemStack.EMPTY;
    }
}
