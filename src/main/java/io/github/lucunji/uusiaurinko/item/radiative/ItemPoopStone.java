package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.fluid.ModFluids;
import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;

import java.util.Random;

public class ItemPoopStone extends ItemRadiative {
    public ItemPoopStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        excrementTransmutation(worldIn, entityIn);
        if (!worldIn.isRemote && entityIn instanceof LivingEntity) {
            Random random = worldIn.getRandom();
            LivingEntity creature = (LivingEntity) entityIn;
            if (random.nextFloat() < ServerConfigs.INSTANCE.POOP_STONE_DEBUFF_CHANCE.get()) {
                creature.addPotionEffect(new EffectInstance(Effects.POISON, 25, 0, true, true, true));
                creature.addPotionEffect(new EffectInstance(Effects.NAUSEA, 120, 0, true, true, true));
            }
            creature.addPotionEffect(new EffectInstance(Effects.HUNGER, 100, 0, true, false, true));
        }
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
        excrementTransmutation(itemEntity.world, itemEntity);
    }

    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return null;
    }

    @Override
    protected void makeParticles(ItemEntity entity) {
        Random random = entity.world.getRandom();
        if (random.nextFloat() < 0.07) {
            double posX = entity.getPosX() - 0.2 + random.nextFloat() * 0.4;
            double posY = entity.getPosY() + random.nextFloat() * 0.4;
            double posZ = entity.getPosZ() - 0.2 + random.nextFloat() * 0.4;

            int color = Effects.POISON.getLiquidColor();
            double r = (color >> 16 & 255) / 255.0D;
            double g = (color >> 8 & 255) / 255.0D;
            double b = (color & 255) / 255.0D;
            // only runs in client
            entity.world.addOptionalParticle(ParticleTypes.ENTITY_EFFECT, posX, posY, posZ, r, g, b);
        }
    }

    private static void excrementTransmutation(World worldIn, Entity source) {
        if (worldIn.isRemote()) return;

        int range = ServerConfigs.INSTANCE.POOP_STONE_TRANSMUTATION_RANGE.get();
        if (range <= 0) return;
        int rangeSq = range * range;
        ITag<Fluid> excrementTag = FluidTags.getCollection().getTagByID(ModFluids.EXCREMENT_FLUID_TAG_LOCATION);
        Vector3d centerPos = source.getPositionVec();
        BlockPos.getAllInBox(new AxisAlignedBB(
                centerPos.subtract(range, range, range),
                centerPos.add(range, range, range)
        ))
                .forEach(pos -> {
                    double distanceSq = centerPos.squareDistanceTo(pos.getX(), pos.getY(), pos.getZ());
                    if (distanceSq > rangeSq) return;

                    FluidState state = worldIn.getFluidState(pos);
                    // cannot only check fluid state since it might be a waterlogged block
                    if (state.getFluid().isSource(state) && state.getBlockState().matchesBlock(Blocks.WATER) &&
                            worldIn.getBiome(pos).getCategory() != Biome.Category.OCEAN) {
                        worldIn.setBlockState(pos, ModBlocks.EXCREMENT.get().getDefaultState());
                    }
                });

    }
}
