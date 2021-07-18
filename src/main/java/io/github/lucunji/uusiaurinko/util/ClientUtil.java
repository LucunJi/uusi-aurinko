package io.github.lucunji.uusiaurinko.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.util.InputMappings;
import org.lwjgl.glfw.GLFW;

/**
 * @author DustW
 */
public class ClientUtil {
    public static boolean isShiftDown() {
        long windowHandle = Minecraft.getInstance().getMainWindow().getHandle();
        return InputMappings.isKeyDown(windowHandle, GLFW.GLFW_KEY_LEFT_SHIFT) ||
                InputMappings.isKeyDown(windowHandle, GLFW.GLFW_KEY_RIGHT_SHIFT);
    }
}
