package io.github.lucunji.uusiaurinko.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@ObjectHolder(MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityTypes {
    private static final Logger LOGGER = LogManager.getLogger();

    @ObjectHolder("thrown_rock")
    public static final EntityType<ThrownRockEntity> THROWN_ROCK = null;

    @SubscribeEvent
    public static void onEntityTypesRegistry(final RegistryEvent.Register<EntityType<?>> entityTypeRegistryEvent) {
        LOGGER.info("Register entity types");
        entityTypeRegistryEvent.getRegistry().registerAll(
                EntityType.Builder
                        .create(ThrownRockEntity::new, EntityClassification.MISC)
                        .size(12/16F, 3/16F)
                        .immuneToFire()
                        .setTrackingRange(6).updateInterval(10)
                        .build("thrown_rock").setRegistryName(MODID, "thrown_rock")
        );
    }
}
