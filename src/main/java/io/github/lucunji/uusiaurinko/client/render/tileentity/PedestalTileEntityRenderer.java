package io.github.lucunji.uusiaurinko.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
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
import net.minecraft.world.World;

import java.util.Random;

public class PedestalTileEntityRenderer extends TileEntityRenderer<PedestalTileEntity> {
    private static final float DEPTH_OFFSET_PER_NONBLOCK_MODEL = 0.09375F;
    private static final float RESCALE_SIZE_ITEM = 0.575F;
    private static final float RESCALE_SIZE_BLOCK = 0.2875F;

    private final ItemRenderer itemRenderer;
    private final Random random;
    private final float hoverStart;

    public PedestalTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn, ItemRenderer itemRenderer) {
        super(rendererDispatcherIn);
        this.itemRenderer = itemRenderer;
        this.random = new Random();
        this.hoverStart = (float)(Math.random() * Math.PI * 2.0D);
    }

    /**
     * Code borrowed from {@link net.minecraft.client.renderer.entity.ItemRenderer#render}
     */
    @Override
    public void render(PedestalTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        ItemStack itemstack = tileEntityIn.getStackInSlot(0);

        World world = tileEntityIn.getWorld();

        if (itemstack.isEmpty() || world == null) return;

        float theta = (world.getGameTime() + partialTicks) / 10F + this.hoverStart;

        matrixStackIn.push();

        this.random.setSeed(Item.getIdFromItem(itemstack.getItem()) + itemstack.getDamage());
        IBakedModel ibakedmodel = this.itemRenderer.getItemModelWithOverrides(itemstack, world, null);

        boolean gui3d = ibakedmodel.isGui3d(); // a gui3d model usually means a block item model

        float bobbingHeight = MathHelper.sin(theta) * 0.1F + 0.1F;
        //noinspection deprecation
        Vector3f modelScale = ibakedmodel.getItemCameraTransforms()
                .getTransform(ItemCameraTransforms.TransformType.GROUND).scale.copy();
        // also transform in y direction by y scale of item model to avoid render scaled item inside ground
        matrixStackIn.translate(0.5, 1 + bobbingHeight + 0.25 * modelScale.getY(), 0.5);

        // rotation effect
        matrixStackIn.rotate(Vector3f.YP.rotation(theta));

        int modelCount = this.getModelCount(itemstack);

        // correct the deviation in z-axis when there are multiple non-block item models
        if (!gui3d)
            matrixStackIn.translate(0, 0, -0.5 * DEPTH_OFFSET_PER_NONBLOCK_MODEL * (modelCount - 1));

        // scale the item model to make it fit the pedestal size
        float maxScale = Math.max(modelScale.getX(), Math.max(modelScale.getY(), modelScale.getZ()));
        if (!gui3d && maxScale < RESCALE_SIZE_ITEM) {
            float scale = RESCALE_SIZE_ITEM / maxScale;
            matrixStackIn.scale(scale, scale, scale);
        } else if (gui3d && maxScale < RESCALE_SIZE_BLOCK) {
            float scale = RESCALE_SIZE_BLOCK / maxScale;
            matrixStackIn.scale(scale, scale, scale);
        }

        for(int i = 0; i < modelCount; ++i) {
            matrixStackIn.push();
            // give each model a random offset
            if (i > 0) {
                if (gui3d) {
                    // for block item models, offsets are in all 3 dimensions
                    float dx = (this.random.nextFloat() * 2F - 1F) * 0.15F;
                    float dy = (this.random.nextFloat() * 2F - 1F) * 0.15F;
                    float dz = (this.random.nextFloat() * 2F - 1F) * 0.15F;
                    matrixStackIn.translate(dx, dy, dz);
                } else {
                    // for non-block item models, only give offsets in x and y
                    float dx = (this.random.nextFloat() * 2F - 1F) * 0.15F * 0.5F;
                    float dy = (this.random.nextFloat() * 2F - 1F) * 0.15F * 0.5F;
                    matrixStackIn.translate(dx, dy, 0);
                }
            }
            this.itemRenderer.renderItem(itemstack, ItemCameraTransforms.TransformType.GROUND, false,
                    matrixStackIn, bufferIn, combinedLightIn, OverlayTexture.NO_OVERLAY, ibakedmodel);

            matrixStackIn.pop();

            if (!gui3d) {
                // for non-block item models, give translation along z axis to each model
                // to avoid rendering them in one plane
                matrixStackIn.translate(0, 0, DEPTH_OFFSET_PER_NONBLOCK_MODEL);
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
