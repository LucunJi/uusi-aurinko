package io.github.lucunji.uusiaurinko.util;

import com.google.common.collect.Sets;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

import java.util.Optional;
import java.util.Set;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ConductivityChecker implements IConductivityChecker {
    private static final ResourceLocation CONDUCTOR_TAG_LOCATION = new ResourceLocation(MODID, "conductor");

    private final Set<Block> conductorBlocks ;
    private final Set<Fluid> conductorFluids;

    public ConductivityChecker() {
        conductorBlocks = Sets.newIdentityHashSet();
        conductorFluids = Sets.newIdentityHashSet();
        Optional.ofNullable(BlockTags.getCollection().get(CONDUCTOR_TAG_LOCATION))
                .map(ITag::getAllElements).ifPresent(conductorBlocks::addAll);
        Optional.ofNullable(FluidTags.getCollection().get(CONDUCTOR_TAG_LOCATION))
                .map(ITag::getAllElements).ifPresent(conductorFluids::addAll);
    }

    @Override
    public boolean isConductor(Block block) {
        return conductorBlocks.contains(block);
    }

    @Override
    public boolean isConductor(Fluid fluid) {
        return conductorFluids.contains(fluid);
    }

    @Override
    public boolean isConductor(BlockState blockState) {
        return isConductor(blockState.getBlock()) || isConductor(blockState.getFluidState().getFluid());
    }

    @Override
    public boolean isConductor(FluidState fluidState) {
        return isConductor(fluidState.getFluid()) || isConductor(fluidState.getBlockState().getBlock());
    }
}
