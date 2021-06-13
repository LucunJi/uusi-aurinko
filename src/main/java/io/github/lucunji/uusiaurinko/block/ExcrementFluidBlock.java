package io.github.lucunji.uusiaurinko.block;

import io.github.lucunji.uusiaurinko.fluid.ModFluids;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.fluid.Fluid;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.tags.FluidTags;
import net.minecraft.tags.ITag;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class ExcrementFluidBlock extends FlowingFluidBlock {
    public ExcrementFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isRemote || !(entityIn instanceof LivingEntity)) return;
        LivingEntity creature = (LivingEntity) entityIn;
        creature.addPotionEffect(new EffectInstance(Effects.HUNGER, 400, 0));

        ITag<Fluid> excrementTag = FluidTags.getCollection().getTagByID(ModFluids.EXCREMENT_FLUID_TAG_LOCATION);
        if (worldIn.getFluidState(new BlockPos(creature.getEyePosition(1)))
                .isTagged(excrementTag)) {
            creature.addPotionEffect(new EffectInstance(Effects.POISON, 200, 0));
            creature.addPotionEffect(new EffectInstance(Effects.NAUSEA, 200, 0));
        }
    }
}
