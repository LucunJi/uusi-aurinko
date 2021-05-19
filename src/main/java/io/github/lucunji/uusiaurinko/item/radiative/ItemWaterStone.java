package io.github.lucunji.uusiaurinko.item.radiative;

import io.github.lucunji.uusiaurinko.util.EffectHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class ItemWaterStone extends ItemRadiative {
    public ItemWaterStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {

    }

    /**
     * Give the non-beacon effects extra decrements faster. The code is copied from {@code LivingEntity.updatePotionEffects}.
     */
    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        if (entityIn instanceof LivingEntity) {
            EffectHelper.fadePotionEffects((LivingEntity) entityIn);
        }
    }

    @Nullable
    @Override
    public IParticleData inWorldParticleType(ItemEntity itemEntity) {
        return itemEntity.isOnGround() ? ParticleTypes.DRIPPING_WATER : ParticleTypes.FALLING_WATER;
    }
}
