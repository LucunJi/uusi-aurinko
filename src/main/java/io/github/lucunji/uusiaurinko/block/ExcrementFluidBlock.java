package io.github.lucunji.uusiaurinko.block;

import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import net.minecraft.block.BlockState;
import net.minecraft.block.FlowingFluidBlock;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.fluid.FlowingFluid;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

import java.util.function.Supplier;

public class ExcrementFluidBlock extends FlowingFluidBlock {
    public ExcrementFluidBlock(Supplier<? extends FlowingFluid> supplier, Properties properties) {
        super(supplier, properties);
    }

    /**
     * Decrease light level by 5 each block.
     */
    @SuppressWarnings("deprecation")
    @Override
    public int getOpacity(BlockState state, IBlockReader worldIn, BlockPos pos) {
        return 5;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onEntityCollision(BlockState state, World worldIn, BlockPos pos, Entity entityIn) {
        if (worldIn.isRemote || !(entityIn instanceof LivingEntity)) return;
        int duration = ServerConfigs.INSTANCE.EXCREMENT_DEBUFF_DURATION.get();
        if (duration <= 0) return;

        LivingEntity creature = (LivingEntity) entityIn;
        creature.addPotionEffect(new EffectInstance(Effects.HUNGER, duration, 0));

        if (((int) entityIn.getPosYEye()) == pos.getY()) {
            creature.addPotionEffect(new EffectInstance(Effects.POISON, duration, 0));
            creature.addPotionEffect(new EffectInstance(Effects.NAUSEA, duration, 0));
        }
    }
}
