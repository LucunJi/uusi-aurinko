package io.github.lucunji.uusiaurinko.config.loadlistening;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;

public class BlockFluidCompositeConfigWrapper {

    private final BlockTaggedListConfigValue blockConfig;
    private final FluidTaggedListConfigValue fluidConfig;

    public BlockFluidCompositeConfigWrapper(BlockTaggedListConfigValue blockConfig, FluidTaggedListConfigValue fluidConfig) {
        this.blockConfig = blockConfig;
        this.fluidConfig = fluidConfig;
    }

    /**
     * Do not check the corresponding fluid.
     */
    public boolean contains(Block block) {
        return blockConfig.contains(block);
    }

    /**
     * Do not check the corresponding block.
     */
    public boolean contains(Fluid fluid) {
        return fluidConfig.contains(fluid);
    }

    /**
     * Also check the corresponding fluid.
     */
    public boolean contains(BlockState blockState) {
        return blockConfig.contains(blockState) || fluidConfig.contains(blockState.getFluidState());
    }

    /**
     * Also check the corresponding block.
     */
    public boolean contains(FluidState fluidState) {
        return fluidConfig.contains(fluidState) || blockConfig.contains(fluidState.getBlockState());
    }
}
