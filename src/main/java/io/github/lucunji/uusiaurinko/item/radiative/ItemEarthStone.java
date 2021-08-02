package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.tileentity.TransmutingTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.FallingBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.FallingBlockEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.server.ServerWorld;

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

    @Override
    public ActionResult<ItemStack> onItemRightClick(World worldIn, PlayerEntity playerIn, Hand handIn) {
        if (!worldIn.isRemote() && playerIn.isSneaking()) {
            if (playerIn.getCooledAttackStrength(0.5F) >= 1.0F) {
                earthquake(worldIn, playerIn);
                playerIn.addStat(Stats.ITEM_USED.get(this), 1);
                playerIn.getCooldownTracker().setCooldown(this, 60);
            }
            return ActionResult.resultSuccess(playerIn.getHeldItem(handIn));
        } else {
            return super.onItemRightClick(worldIn, playerIn, handIn);
        }
    }

    private static void earthquake(World world, PlayerEntity player) {
        int range = ServerConfigs.INSTANCE.EARTH_STONE_EARTHQUAKE_RANGE.get();
        if (range <= 0) return;
        int rangeSq = range * range;
        int particleAmount = ServerConfigs.INSTANCE.EARTH_STONE_EARTHQUAKE_PARTICLE_AMOUNT.get();
        Vector3d centerPos = player.getPositionVec();
        BlockPos minPos = player.getPosition().add(-range, -range, -range);
        BlockPos maxPos = player.getPosition().add(range, range, range);
        BlockPos.Mutable mutable = new BlockPos.Mutable();
        BlockPos.Mutable mutableBelow = new BlockPos.Mutable();
        int soundCount = 0;
        for (int x = minPos.getX(); x <= maxPos.getX(); ++x) {
            for (int z = minPos.getZ(); z <= maxPos.getZ(); ++z) {
                boolean foundSpaceFallThrough = false;
                for (int y = minPos.getY(); y <= maxPos.getY(); ++y) {
                    if (centerPos.squareDistanceTo(x, y, z) > rangeSq) continue;
                    mutable.setPos(x, y, z);
                    mutableBelow.setPos(x, y - 1, z);
                    BlockState state = world.getBlockState(mutable);

                    if (FallingBlock.canFallThrough(world.getBlockState(mutableBelow)))
                        foundSpaceFallThrough = true;

                    // negation of all conditions - none of them should be true
                    if (foundSpaceFallThrough &&
                            !(ServerConfigs.INSTANCE.EARTH_STONE_EARTHQUAKE_BLACKLIST.contains(state.getBlock()) ||
                                    world.isAirBlock(mutable) || state.hasTileEntity() || !state.getFluidState().isEmpty())) {
                        FallingBlockEntity fallingblockentity = new FallingBlockEntity(world, x + 0.5, y, z + 0.5, state);
                        fallingblockentity.setHurtEntities(true);
                        world.addEntity(fallingblockentity);
                        if (++soundCount <= 50) {
                            world.playSound(null, x, y, z, state.getSoundType().getBreakSound(), SoundCategory.BLOCKS,
                                    (state.getSoundType().getVolume() + 1.0F) / 2.0F, state.getSoundType().getPitch() * 0.8F);
                        }
                        if (particleAmount > 0) {
                            ((ServerWorld) world).spawnParticle(new BlockParticleData(ParticleTypes.FALLING_DUST, state),
                                    x + 0.5, y + 0.5, z + 0.5, particleAmount, 0.6, 0.6, 0.6, 0);
                        }
                    }
                }
            }
        }
    }

    private static void soilTransmutation(World worldIn, Entity source) {
        if (worldIn.isRemote()) return;

        int range = ServerConfigs.INSTANCE.EARTH_STONE_TRANSMUTATION_RANGE.get();
        if (range <= 0) return;
        int rangeSq = range * range;
        Vector3d centerPos = source.getPositionVec();
        BlockPos.getAllInBox(new AxisAlignedBB(
                centerPos.subtract(range, range, range),
                centerPos.add(range, range, range)
        ))
                .forEach(pos -> {
                    double distanceSq = centerPos.squareDistanceTo(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
                    if (distanceSq > rangeSq) return;

                    BlockState state = worldIn.getBlockState(pos);
                    if (ServerConfigs.INSTANCE.EARTH_STONE_TRANSMUTATION_BLACKLIST.contains(state.getBlock()) ||
                            worldIn.isAirBlock(pos) || state.matchesBlock(Blocks.DIRT) ||
                            state.hasTileEntity() || !state.getFluidState().isEmpty() ||
                            state.getCollisionShape(worldIn, pos, ISelectionContext.dummy()).isEmpty()) return;


                    worldIn.setBlockState(pos, ModBlocks.TRANSMUTING_BLOCK.get().getDefaultState(), 0b10010);
                    worldIn.setTileEntity(pos, new TransmutingTileEntity(state, Blocks.DIRT.getDefaultState(),
                            Math.min(0, -(MathHelper.sqrt(distanceSq) + 1F)/8F)));
                });

    }
}
