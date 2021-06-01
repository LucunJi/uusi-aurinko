package io.github.lucunji.uusiaurinko.item.radiative;

import net.minecraft.block.Blocks;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.BlockParticleData;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;

public class ItemEarthStone extends ItemRadiative {
    public ItemEarthStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
    }

    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return new BlockParticleData(ParticleTypes.FALLING_DUST, Blocks.DIRT.getDefaultState());
    }
}
