package io.github.lucunji.uusiaurinko.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lucunji.uusiaurinko.item.ModItems;
import io.github.lucunji.uusiaurinko.tileentity.PedestalTileEntity;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;

import java.util.Random;

public class PedestalTileEntityRenderer extends TileEntityRenderer<PedestalTileEntity> {
    private final ItemRenderer itemRenderer;
    private final Random random;
    private float hoverStart;

    public PedestalTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn, ItemRenderer itemRenderer) {
        super(rendererDispatcherIn);
        this.itemRenderer = itemRenderer;
        this.random = new Random();
        this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
    }

    /**
     * Code borrowed from {@code net.minecraft.client.renderer.entity.ItemRenderer.render()}
     */
    @Override
    public void render(PedestalTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        matrixStackIn.push();
        ItemStack itemstack = ModItems.SUN_STONE.get().getDefaultInstance();
        int i = itemstack.isEmpty() ? 187 : Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage();
        this.random.setSeed(i);
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, tileEntityIn.getWorld(), null);
        boolean flag = ibakedmodel.isGui3d();
        int j = this.getModelCount(itemstack);
        float f = 0.25F;
        float f1 = MathHelper.sin(((float)tileEntityIn.getWorld().getGameTime() + partialTicks) / 10.0F + this.hoverStart) * 0.1F + 0.1F;
        // bobbing effect
        //noinspection deprecation
        float f2 = ibakedmodel.getItemCameraTransforms().getTransform(ItemCameraTransforms.TransformType.GROUND).scale.getY();

        matrixStackIn.translate(0.5, 1 + f1 + 0.25 * f2, 0.5);
        float f3 = ((float)tileEntityIn.getWorld().getGameTime() + partialTicks) / 10.0F + this.hoverStart;
        matrixStackIn.rotate(Vector3f.YP.rotation(f3));
        if (!flag) {
            float f7 = -0.0F * (float)(j - 1) * 0.5F;
            float f8 = -0.0F * (float)(j - 1) * 0.5F;
            float f9 = -0.09375F * (float)(j - 1) * 0.5F;
            matrixStackIn.translate(f7, f8, f9);
        }

        for(int k = 0; k < j; ++k) {
            matrixStackIn.push();
            if (k > 0) {
                if (flag) {
                    float f11 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f13 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    float f10 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F;
                    matrixStackIn.translate(f11, f13, f10);
                } else {
                    float f12 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    float f14 = (this.random.nextFloat() * 2.0F - 1.0F) * 0.15F * 0.5F;
                    matrixStackIn.translate(f12, f14, 0.0D);
                }
            }

            this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, false,
                    matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);
            matrixStackIn.pop();
            if (!flag) {
                matrixStackIn.translate(0.0, 0.0, 0.09375F);
            }
        }
        matrixStackIn.pop();
    }

    private int getModelCount(ItemStack stack) {
        int i = 1;
        if (stack.getCount() > 48) {
            i = 5;
        } else if (stack.getCount() > 32) {
            i = 4;
        } else if (stack.getCount() > 16) {
            i = 3;
        } else if (stack.getCount() > 1) {
            i = 2;
        }

        return i;
    }
}
