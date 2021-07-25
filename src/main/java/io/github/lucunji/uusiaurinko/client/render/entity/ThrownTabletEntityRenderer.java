package io.github.lucunji.uusiaurinko.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lucunji.uusiaurinko.entity.ThrownTabletEntity;
import io.github.lucunji.uusiaurinko.item.ModItems;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.vector.Vector3f;

public class ThrownTabletEntityRenderer extends EntityRenderer<ThrownTabletEntity> {
    private final ItemRenderer itemRenderer;

    public ThrownTabletEntityRenderer(EntityRendererManager renderManager, ItemRenderer itemRenderer) {
        super(renderManager);
        this.itemRenderer = itemRenderer;
    }

    @Override
    public void render(ThrownTabletEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
        matrixStackIn.push();

        ItemStack itemStack = ModItems.EMERALD_TABLET.get().getDefaultInstance();
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemStack, entityIn.world, null);
        //noinspection deprecation
        Vector3f modelScale = ibakedmodel.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.copy();
        float scale = 0.8F / Math.max(Math.max(modelScale.getX(), modelScale.getY()), modelScale.getZ());
        matrixStackIn.scale(scale, scale, scale);


        matrixStackIn.translate(0, entityIn.getHeight() / 2, 0);

        Vector3f rollAxis = new Vector3f(entityIn.getMotion());
        rollAxis.cross(Vector3f.YP);
        rollAxis.normalize(); // normalize([0, 1, 0] x motion), calculate the rotational axis
        matrixStackIn.rotate(rollAxis.rotationDegrees(entityIn.getPitch(partialTicks)));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(entityIn.getYaw(partialTicks)));
        itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.NONE, false,
                matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);

        matrixStackIn.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(ThrownTabletEntity entity) {
        return null;
    }
}
