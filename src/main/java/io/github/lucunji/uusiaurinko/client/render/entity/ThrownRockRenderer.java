package io.github.lucunji.uusiaurinko.client.render.entity;

import com.mojang.blaze3d.matrix.MatrixStack;
import io.github.lucunji.uusiaurinko.entity.ThrownRockEntity;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.inventory.container.PlayerContainer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import static io.github.lucunji.uusiaurinko.item.Items.FIRE_STONE;

@OnlyIn(Dist.CLIENT)
public class ThrownRockRenderer extends EntityRenderer<ThrownRockEntity> {
    protected ThrownRockRenderer(EntityRendererManager renderManager) {
        super(renderManager);
    }

    @Override
    public void render(ThrownRockEntity entityIn, float entityYaw, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int packedLightIn) {
        Minecraft.getInstance().getItemRenderer().renderItem(new ItemStack(FIRE_STONE), ItemCameraTransforms.TransformType.FIXED, packedLightIn, OverlayTexture.NO_OVERLAY, matrixStackIn, bufferIn);
        super.render(entityIn, entityYaw, partialTicks, matrixStackIn, bufferIn, packedLightIn);
    }

    @Override
    public ResourceLocation getEntityTexture(ThrownRockEntity entity) {
        // AtlasTexture.LOCATION_BLOCKS_TEXTURE is depreciated
        return PlayerContainer.LOCATION_BLOCKS_TEXTURE;
    }
}
