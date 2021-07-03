package io.github.lucunji.uusiaurinko.util;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.server.ServerLifecycleHooks;

/**
 * @author DustW
 */
public class ServerUtil {
    public static MinecraftServer getServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    public static int getServerTick() {
        return getServer().getTickCounter();
    }
}
