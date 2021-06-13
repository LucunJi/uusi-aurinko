package io.github.lucunji.uusiaurinko.item.radiative;

import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.IParticleData;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.potion.EffectInstance;
import net.minecraft.potion.Effects;
import net.minecraft.world.World;

import java.util.Random;

public class ItemPopStone extends ItemRadiative {
    public ItemPopStone(Properties properties) {
        super(properties);
    }

    @Override
    public void radiationInHand(ItemStack stack, World worldIn, Entity entityIn, boolean isMainHand) {
        if (!worldIn.isRemote && entityIn instanceof LivingEntity) {
            Random random = worldIn.getRandom();
            LivingEntity creature = (LivingEntity) entityIn;
            if (random.nextFloat() < 0.01) {
                creature.addPotionEffect(new EffectInstance(Effects.POISON, 25, 0, true, true, true));
                creature.addPotionEffect(new EffectInstance(Effects.NAUSEA, 120, 0, true, true, true));
            }
            creature.addPotionEffect(new EffectInstance(Effects.HUNGER, 100, 0, true, false, true));
        }
    }

    @Override
    public void radiationInWorld(ItemStack stack, ItemEntity itemEntity) {
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
}
