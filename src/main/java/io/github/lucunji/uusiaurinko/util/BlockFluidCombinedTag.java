package io.github.lucunji.uusiaurinko.util;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.ResourceLocation;

public class BlockFluidCombinedTag {

    private final ITag<Block> blockTag;
    private final ITag<Fluid> fluidTag;

    public BlockFluidCombinedTag(final ResourceLocation location) {
        blockTag = BlockTags.getCollection().getTagByID(location);
        fluidTag = FluidTags.getCollection().getTagByID(location);
    }

    /**
     * Do not check the corresponding fluid.
     */
    public boolean contains(Block block) {
        return block.isIn(blockTag);
    }

    /**
     * Do not check the corresponding block.
     */
    public boolean contains(Fluid fluid) {
        return fluid.isIn(fluidTag);
    }

    /**
     * Also check the corresponding fluid.
     */
    public boolean contains(BlockState blockState) {
        return blockState.isIn(blockTag) || blockState.getFluidState().isTagged(fluidTag);
    }

    /**
     * Also check the corresponding block.
     */
    public boolean contains(FluidState fluidState) {
        return fluidState.isTagged(fluidTag) || fluidState.getBlockState().isIn(blockTag);
    }
}
