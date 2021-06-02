package io.github.lucunji.uusiaurinko.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;

public interface IConductivityChecker {
    boolean isConductor(Block block);
    boolean isConductor(Fluid fluid);
    boolean isConductor(BlockState blockState);
    boolean isConductor(FluidState fluidState);
}
