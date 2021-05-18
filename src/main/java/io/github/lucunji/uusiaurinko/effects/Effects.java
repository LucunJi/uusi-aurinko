package io.github.lucunji.uusiaurinko.effects;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@ObjectHolder(MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Effects {
    private static final Logger LOGGER = LogManager.getLogger();

    @ObjectHolder("fire_resistance_limited")
    public static final LimitedFireResistanceEffect FIRE_RESISTANCE_LIMITED = null;

    @SubscribeEvent
    public static void onEffectsRegistry(final RegistryEvent.Register<Effect> effectRegistryEvent) {
        LOGGER.info("Register effects");
        effectRegistryEvent.getRegistry().registerAll(
                new LimitedFireResistanceEffect(EffectType.BENEFICIAL, 0xD9E43A)
                        .setRegistryName(MODID, "fire_resistance_limited")
        );
    }
}
