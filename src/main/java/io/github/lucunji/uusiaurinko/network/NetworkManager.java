package io.github.lucunji.uusiaurinko.network;

import io.github.lucunji.uusiaurinko.UusiAurinko;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

/**
 * @author DustW
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class NetworkManager {
    private static final String PROTOCOL_VERSION = "1.0";
    private static int id = 0;

    public static SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel (
            new ResourceLocation(UusiAurinko.MODID, "network_channel"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private NetworkManager() {
        throw new UnsupportedOperationException("No instance");
    }

    public static <T extends IServerMessage> void serverSendToPlayer(T packet, ServerPlayerEntity player) {
        INSTANCE.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void registerPackets() {
        INSTANCE.registerMessage(id++,
                ServerPedestalBlockSync.class,
                ServerPedestalBlockSync::encode,
                ServerPedestalBlockSync::decode,
                ServerPedestalBlockSync::handle);
    }

    @SubscribeEvent
    public static void onCommonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(NetworkManager::registerPackets);
    }
}
