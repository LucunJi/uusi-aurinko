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
        float scale = 1 / Math.max(Math.max(modelScale.getX(), modelScale.getY()), modelScale.getZ());
        matrixStackIn.scale(scale, scale, scale);


        matrixStackIn.translate(0, entityIn.getHeight() / 2, 0);
//        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(entityIn.rotationPitch));
//        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(entityYaw));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees((entityIn.world.getGameTime() + partialTicks) * -10));
        matrixStackIn.rotate(Vector3f.YP.rotationDegrees(50));
        itemRenderer.renderItem(itemStack, ItemCameraTransforms.TransformType.NONE, false,
                matrixStackIn, bufferIn, packedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);

        matrixStackIn.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(ThrownTabletEntity entity) {
        return null;
    }
}
