package io.github.lucunji.uusiaurinko.client.render.entity;

import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class EntityRenderers {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onEntityRenderersRegistry(final FMLClientSetupEvent event) {
//        LOGGER.info("Register entity renderer");
//        RenderingRegistry.registerEntityRenderingHandler();
    }
}
