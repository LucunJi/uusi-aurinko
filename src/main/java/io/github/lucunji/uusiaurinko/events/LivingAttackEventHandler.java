package io.github.lucunji.uusiaurinko.events;

import io.github.lucunji.uusiaurinko.effects.LimitedFireResistanceEffect;
import io.github.lucunji.uusiaurinko.effects.ModEffects;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LivingAttackEventHandler {
    @SubscribeEvent
    public static void onLivingAttacked(final LivingAttackEvent livingAttackEvent) {
        LivingEntity livingEntity = livingAttackEvent.getEntityLiving();

        // main logic for LimitedFireResistanceEffect
        if (livingEntity.isPotionActive(ModEffects.FIRE_RESISTANCE_LIMITED.get())) {
            for (DamageSource src : LimitedFireResistanceEffect.BLOCKED_SOURCES) {
                if (livingAttackEvent.getSource() == src) {
                    livingAttackEvent.setCanceled(true);
                }
            }
        }

        // fireproof logic for ItemWaterStone
//        if (livingAttackEvent.getSource().isFireDamage() &&
//                (livingEntity.getHeldItemMainhand().getItem() instanceof ItemWaterStone ||
//                livingEntity.getHeldItemOffhand().getItem() instanceof ItemWaterStone)) {
//            livingAttackEvent.setCanceled(true);
//        }
    }
}
