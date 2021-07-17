package io.github.lucunji.uusiaurinko.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lucunji.uusiaurinko.client.render.entity.model.NewSunModel;
import io.github.lucunji.uusiaurinko.entity.NewSunEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class NewSunEntityRenderer extends EntityRenderer<NewSunEntity> {
    /* A scratch for setting sun's entity data:
    /data merge entity @e[type=uusi-aurinko:new_sun, limit=1] {}
    /data get entity @e[type=uusi-aurinko:new_sun, limit=1]
     */
    private final NewSunModel model;

    public NewSunEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        this.model = new NewSunModel();
    }

    @Override
    public void render(NewSunEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.push();

        float size = entityIn.getRenderingSize();
        float hitboxSize = entityIn.getBoundingBoxSize();
        matrixStackIn.translate(0, hitboxSize / 2D, 0);
        matrixStackIn.scale(size, size, size);

        float t = (entityIn.world.getGameTime() + partialTicks) / 2;
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(t));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(t + 71));

        final NewSunEntity.SunState sunState = entityIn.getSunState();
        RenderType renderType = RenderType.getBeaconBeam(this.getEntityTexture(entityIn), false); // disable fog and alpha
        model.render(matrixStackIn, bufferIn.getBuffer(renderType), // render the sun itself
                packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        matrixStackIn.scale(-1, 1, 1); // negate the size to use the feature of culling: only show the faces on the back
        model.render(matrixStackIn, bufferIn.getBuffer(renderType), // render again to make the player cannot see outside when inside the sun
                packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);

        // render the sun's halo
        renderType = RenderType.getBeaconBeam(sunState.texture, true); // disable fog and enable alpha
        final int iters = sunState.haloIters; // iteration increase with the sun's size to keep transition smooth when the sun grows
        final float it = iters / 10F;
        for (int i = 0; i < iters; i++) {
            matrixStackIn.push();
            // maps i ∈ [0, iters) to iit ∈ [0, 1)
            float iit = (float) i / iters;
            // maps i ∈ [0, iters) to scale ∈ [1, 2)
            float scale = 1 + iit;
            matrixStackIn.scale(scale, scale, scale);
            // alpha = (0.24 - 0.225 * iit) / (iters / 10)
            // maps i ∈ [0, iters) to ɑ ∈ [0.24/iters, 0.015/iters)
            // the integration of ɑ over the range [0, iter) is approximately constant
            float alpha = (2.4F - 2.25F * iit) / iters;
            model.render(matrixStackIn, bufferIn.getBuffer(renderType),
                    packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, alpha);
            matrixStackIn.pop();
        }
        matrixStackIn.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(NewSunEntity entity) {
        return entity.getSunState() == NewSunEntity.SunState.GROWING ? entity.getLastConsumedStone().texture : entity.getSunState().texture;
    }
}
