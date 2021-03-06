package io.github.lucunji.uusiaurinko.events;

import io.github.lucunji.uusiaurinko.effects.ModEffects;
import io.github.lucunji.uusiaurinko.entity.ThrownTabletEntity;
import io.github.lucunji.uusiaurinko.util.ModDamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LootingLevelEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public class CombatEventHandler {
    /**
     * Main logic of electricity resistance potion effect.
     * This handler has the highest priority since it is this mod's new trait.
     */
    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public static void onEntityHurt(LivingAttackEvent event) {
        if (event.getSource() == ModDamageSource.ELECTRICITY &&
                event.getEntityLiving().isPotionActive(ModEffects.ELECTRICITY_RESISTANCE.get())) {
            event.setCanceled(true);
        }
    }

    /**
     * Gives emerald tablet a lvl 4 looting effect.
     */
    @SubscribeEvent
    public static void onGetLootLevel(LootingLevelEvent event) {
        if (event.getDamageSource().getImmediateSource() instanceof ThrownTabletEntity) {
            event.setLootingLevel(event.getLootingLevel() + 4);
        }
    }
}
