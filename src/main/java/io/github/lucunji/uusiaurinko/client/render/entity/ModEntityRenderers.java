package io.github.lucunji.uusiaurinko.client.render.entity;

import io.github.lucunji.uusiaurinko.entity.ModEntityTypes;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModEntityRenderers {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onEntityRenderersRegistry(final FMLClientSetupEvent event) {
        LOGGER.info("Register entity renderer");
        RenderingRegistry.registerEntityRenderingHandler(ModEntityTypes.RADIATIVE_ITEM.get(),
                manager -> new RadiativeItemEntityRenderer(manager, Minecraft.getInstance().getItemRenderer()));
    }
}
