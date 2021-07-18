package io.github.lucunji.uusiaurinko.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lucunji.uusiaurinko.entity.RadiativeItemEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.util.ResourceLocation;

public class RadiativeItemEntityRenderer extends EntityRenderer<RadiativeItemEntity> {
    private final net.minecraft.client.renderer.entity.ItemRenderer itemRendererSuper;

    public RadiativeItemEntityRenderer(EntityRendererManager renderManagerIn, ItemRenderer itemRendererIn) {
        super(renderManagerIn);
        this.itemRendererSuper = new net.minecraft.client.renderer.entity.ItemRenderer(renderManagerIn, itemRendererIn);
    }

    @Override
    public void render(RadiativeItemEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        itemRendererSuper.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(RadiativeItemEntity entity) {
        return itemRendererSuper.getEntityTexture(entity);
    }
}
