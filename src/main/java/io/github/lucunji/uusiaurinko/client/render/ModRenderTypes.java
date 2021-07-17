package io.github.lucunji.uusiaurinko.client.render;

import net.minecraft.client.renderer.RenderState;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;
import static net.minecraft.client.renderer.RenderType.makeType;

/**
 * Extends {@link RenderState} only for accessing protected fields.
 */
public class ModRenderTypes extends RenderState {
    public ModRenderTypes(String nameIn, Runnable setupTaskIn, Runnable clearTaskIn) {
        super(nameIn, setupTaskIn, clearTaskIn);
        throw new UnsupportedOperationException("The constructor is only for completion!");
    }

    public static RenderType getSun(ResourceLocation locationIn) {
        RenderType.State state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false))
                .cull(CULL_DISABLED).fog(NO_FOG).build(false);
        return makeType(MODID + ":sun", DefaultVertexFormats.BLOCK, 7, 256, false, true, state);
    }

    public static RenderType getHalo(ResourceLocation locationIn) {
        RenderType.State state = RenderType.State.getBuilder().texture(new RenderState.TextureState(locationIn, false, false))
                .transparency(TRANSLUCENT_TRANSPARENCY).writeMask(COLOR_WRITE).fog(NO_FOG).build(false);
        return makeType(MODID + ":halo", DefaultVertexFormats.BLOCK, 7, 256, false, true, state);
    }
}
