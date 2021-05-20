package io.github.lucunji.uusiaurinko.events;

import io.github.lucunji.uusiaurinko.item.radiative.ItemWaterStone;
import net.minecraft.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber
public class LivingUpdateEventHandler {
    @SubscribeEvent
    public static void onLivingUpdate(LivingEvent.LivingUpdateEvent livingUpdateEvent) {
        LivingEntity livingEntity = livingUpdateEvent.getEntityLiving();
        // fireproof logic for ItemWaterStone
        if (livingEntity.getHeldItemMainhand().getItem() instanceof ItemWaterStone ||
                livingEntity.getHeldItemOffhand().getItem() instanceof ItemWaterStone) {
            livingEntity.forceFireTicks(0);
        }
    }
}
