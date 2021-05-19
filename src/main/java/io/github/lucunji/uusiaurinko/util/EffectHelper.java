package io.github.lucunji.uusiaurinko.util;

import net.minecraft.entity.LivingEntity;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectInstance;
import net.minecraftforge.event.entity.living.PotionEvent;

import java.util.ConcurrentModificationException;
import java.util.Iterator;
import java.util.Map;

import static net.minecraftforge.common.MinecraftForge.EVENT_BUS;

/**
 * Static helper methods for {@link net.minecraft.potion.EffectInstance}
 */
public class EffectHelper {
    /**
     * The code is borrowed from {@code LivingEntity.updatePotionEffects}.
     * Some parts are stripped off, including code for particles.
     * Also, the effects from ambient, such as beacon-granted effects, are untouched.
     *
     * @param livingEntity the target entity
     */
    public static void fadePotionEffects(LivingEntity livingEntity) {
        Map<Effect, EffectInstance> activePotionMap = livingEntity.getActivePotionMap();
        Iterator<Effect> iterator = activePotionMap.keySet().iterator();

        try {
            while (iterator.hasNext()) {
                Effect effect = iterator.next();
                EffectInstance effectinstance = activePotionMap.get(effect);
                if (effectinstance.isAmbient()) continue;

                if (!tickInstanceNoEffect(effectinstance, livingEntity, () ->
                        livingEntity.onChangedPotionEffect(effectinstance, true) // TODO: Use Mixin here to access private method
                )) {
                    if (!livingEntity.world.isRemote && !EVENT_BUS.post(new PotionEvent.PotionExpiryEvent(livingEntity, effectinstance))) {
                        iterator.remove();
                        livingEntity.onFinishedPotionEffect(effectinstance); // TODO: Use Mixin here to access private method
                    }
                } else if (effectinstance.getDuration() % 600 == 0) {
                    livingEntity.onChangedPotionEffect(effectinstance, false);
                }
            }
        } catch (ConcurrentModificationException ignored) {
        }

        if (livingEntity.potionsNeedUpdate) { // TODO: Use Mixin here to access private fields
            if (!livingEntity.world.isRemote) {
                livingEntity.updatePotionMetadata(); // TODO: Use Mixin here to access private method
            }

            livingEntity.potionsNeedUpdate = false;
        }
    }

    /**
     * The code is borrowed from EffectInstance.tick(), without performing potion effects.
     */
    private static boolean tickInstanceNoEffect(EffectInstance instance, LivingEntity entityIn, Runnable runnable) {
        if (instance.getDuration() > 0) {
            instance.deincrementDuration(); // TODO: Use Mixin here to access private method

            if (instance.getDuration() == 0 && instance.hiddenEffects != null) {  // TODO: Use Mixin here to access private fields
                // pop the "effects stack": a hidden effect with the highest amplifier replace the current effect
                instance.func_230117_a_(instance.hiddenEffects); // TODO: Use Mixin here to access private method

                instance.hiddenEffects = instance.hiddenEffects.hiddenEffects;
                runnable.run();
            }
        }

        return instance.getDuration() > 0;
    }
}
