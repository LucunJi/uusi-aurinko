package io.github.lucunji.uusiaurinko.client;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.config.ClientConfigs;
import io.github.lucunji.uusiaurinko.entity.ModEntityTypes;
import io.github.lucunji.uusiaurinko.fluid.ModFluids;
import io.github.lucunji.uusiaurinko.tileentity.ModTileEntityTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientSetup {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onClientSetup(final FMLClientSetupEvent event) {
        LOGGER.debug("Register renderers");
        ModEntityTypes.ClientRenderer.RENDERER_BINDERS.forEach(Runnable::run);
        ModTileEntityTypes.ClientRenderer.RENDERER_BINDERS.forEach(Runnable::run);

        if (ClientConfigs.INSTANCE.TRANSPARENT_FLUID.get()) {
            ModFluids.FLUIDS.getEntries().forEach(registryObject ->
                    RenderTypeLookup.setRenderLayer(registryObject.get(), RenderType.getTranslucent()));
        }

        RenderTypeLookup.setRenderLayer(ModBlocks.ITEM_PEDESTAL_COVER.get(), RenderType.getCutout());
    }
}
