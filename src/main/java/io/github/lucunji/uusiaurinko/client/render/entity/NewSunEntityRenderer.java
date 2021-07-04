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

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class NewSunEntityRenderer extends EntityRenderer<NewSunEntity> {
    private final NewSunModel model;

    public NewSunEntityRenderer(EntityRendererManager renderManager) {
        super(renderManager);
        this.model = new NewSunModel();
    }

    @Override
    public void render(NewSunEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.push();
        float size = entityIn.getActualSize();
        matrixStackIn.scale(size, size, size);
        model.render(matrixStackIn, bufferIn.getBuffer(RenderType.getEntitySolid(this.getEntityTexture(entityIn))),
                packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        matrixStackIn.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(NewSunEntity entity) {
        // FIXME: make-do with moon texture, replace it with sun texture in the future
        return new ResourceLocation(MODID, "textures/entity/moon.png");
    }
}
