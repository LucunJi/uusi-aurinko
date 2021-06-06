package io.github.lucunji.uusiaurinko.client.events;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.MainWindow;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@Mod.EventBusSubscriber(Dist.CLIENT)
public class RenderGameOverlayEventHandler {
    private static final ResourceLocation ELECTRIC_TEX_PATH = new ResourceLocation(MODID, "textures/misc/electric.png");

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onPostRendering(final RenderGameOverlayEvent.Post postEvent) {
        if (postEvent.getType() == RenderGameOverlayEvent.ElementType.PORTAL) {
//            renderElectricOverlay(postEvent.getWindow());
        }
    }

    @SuppressWarnings("deprecation")
    private static void renderElectricOverlay(MainWindow mainWindow) {
        Minecraft mc = Minecraft.getInstance();
        ClientWorld world = mc.world;
        if (world == null) return;
        double scaledHeight = mainWindow.getScaledHeight();
        double scaledWidth = mainWindow.getScaledWidth();

        RenderSystem.disableDepthTest();
        RenderSystem.depthMask(false);
        RenderSystem.defaultBlendFunc();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, (world.getGameTime() % 10) / 10f);
        RenderSystem.disableAlphaTest();
        mc.getTextureManager().bindTexture(ELECTRIC_TEX_PATH);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(0.0D, scaledHeight, -90.0D).tex(0.0F, 1.0F).endVertex();
        bufferbuilder.pos(scaledWidth, scaledHeight, -90.0D).tex(1.0F, 1.0F).endVertex();
        bufferbuilder.pos(scaledWidth, 0.0D, -90.0D).tex(1.0F, 0.0F).endVertex();
        bufferbuilder.pos(0.0D, 0.0D, -90.0D).tex(0.0F, 0.0F).endVertex();
        tessellator.draw();
        RenderSystem.depthMask(true);
        RenderSystem.enableDepthTest();
        RenderSystem.enableAlphaTest();
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);
    }
}
