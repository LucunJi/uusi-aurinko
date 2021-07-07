package io.github.lucunji.uusiaurinko.events;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import io.github.lucunji.uusiaurinko.fluid.ModFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.client.event.RenderBlockOverlayEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class InFluidRenderingEventHandler {
    private static final ResourceLocation TEXTURE_UNDER_FLUID = new ResourceLocation(MODID, "textures/misc/underfluid.png");

    /**
     * Override vanilla rendering strategy of screen overlay when player is submerged in fluid.
     * <p>
     * Opaque texture and the fluid's alpha value are used in the new strategy,
     * which allowing player to set all related opacity in one place ({@link net.minecraftforge.fluids.FluidAttributes}),
     * and the rendered opacity can go beyond vanilla water's opacity.
     */
    @SubscribeEvent
    public static void onRenderBlockOverlay(final RenderBlockOverlayEvent renderBlockOverlayEvent) {
        Minecraft mc = Minecraft.getInstance();
        PlayerEntity playerEntity = renderBlockOverlayEvent.getPlayer();
        BlockPos pos = new BlockPos(playerEntity.getPosX(), playerEntity.getPosYEye(), playerEntity.getPosZ());
        //noinspection ConstantConditions
        if (renderBlockOverlayEvent.getOverlayType() == RenderBlockOverlayEvent.OverlayType.WATER &&
                mc.world.getFluidState(pos).getFluid().getRegistryName().getNamespace().equals(MODID)) {
            renderUnderFluid(ModFluids.EXCREMENT.get(), mc, renderBlockOverlayEvent.getMatrixStack());
            renderBlockOverlayEvent.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onGetFogColor(final EntityViewRenderEvent.FogColors fogColorEvent) {
        // TODO: make fog color change based on fluid attributes
    }

    /**
     * Borrowed from {@code OverlayRenderer.renderUnderwater}
     */
    private static void renderUnderFluid(Fluid fluid, Minecraft minecraftIn, MatrixStack matrixStackIn) {
        RenderSystem.enableTexture();
        minecraftIn.getTextureManager().bindTexture(TEXTURE_UNDER_FLUID);
        BufferBuilder bufferbuilder = Tessellator.getInstance().getBuffer();
        //noinspection ConstantConditions
        float brightness = minecraftIn.player.getBrightness();
        int color = fluid.getAttributes().getColor();
        float alpha = (color >> 24 & 255) / 255F;
        alpha = MathHelper.clamp(alpha - 0.02F, 0F, 1F); // a slight offset probably makes things better?
        float r = (color >> 16 & 255) / 255F;
        float g = (color >> 8 & 255) / 255F;
        float b = (color & 255) / 255F;
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        float uOffset = -minecraftIn.player.rotationYaw / 64;
        float vOffset = minecraftIn.player.rotationPitch / 64;
        Matrix4f matrix4f = matrixStackIn.getLast().getMatrix();
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION_COLOR_TEX);
        bufferbuilder.pos(matrix4f, -1, -1, -0.5F).color(r * brightness, g * brightness, b * brightness, alpha).tex(4 + uOffset, 4 + vOffset).endVertex();
        bufferbuilder.pos(matrix4f, 1, -1, -0.5F).color(r * brightness, g * brightness, b * brightness, alpha).tex(0 + uOffset, 4 + vOffset).endVertex();
        bufferbuilder.pos(matrix4f, 1, 1, -0.5F).color(r * brightness, g * brightness, b * brightness, alpha).tex(0 + uOffset, 0 + vOffset).endVertex();
        bufferbuilder.pos(matrix4f, -1, 1, -0.5F).color(r * brightness, g * brightness, b * brightness, alpha).tex(4 + uOffset, 0 + vOffset).endVertex();
        bufferbuilder.finishDrawing();
        WorldVertexBufferUploader.draw(bufferbuilder);
        RenderSystem.disableBlend();
    }
}
