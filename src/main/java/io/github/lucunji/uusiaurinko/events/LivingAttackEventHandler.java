package io.github.lucunji.uusiaurinko.events;

import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.github.lucunji.uusiaurinko.effects.Effects.FIRE_RESISTANCE_LIMITED;
import static io.github.lucunji.uusiaurinko.effects.LimitedFireResistanceEffect.BLOCKED_SOURCES;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.FORGE)
public class LivingAttackEventHandler {
    @SubscribeEvent
    public static void onLivingAttacked(final LivingAttackEvent livingAttackEvent) {
        if (livingAttackEvent.getEntityLiving().isPotionActive(FIRE_RESISTANCE_LIMITED))
        for (DamageSource src : BLOCKED_SOURCES) {
            if (livingAttackEvent.getSource() == src) {
                livingAttackEvent.setCanceled(true);
            }
        }
    }
}
