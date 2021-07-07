package io.github.lucunji.uusiaurinko.client.particle;

import io.github.lucunji.uusiaurinko.particles.ModParticleTypes;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ParticleFactoryRegisterEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientParticleFactories {
    private static final Logger LOGGER = LogManager.getLogger();

    @SubscribeEvent
    public static void onEntityRenderersRegistry(final ParticleFactoryRegisterEvent event) {
        LOGGER.debug("Register particle factories");
        Minecraft.getInstance().particles.registerFactory(ModParticleTypes.SPARK.get(), SparkParticle.Factory::new);
    }
}
