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
        matrixStackIn.translate(0, 0.5, 0);
        float t = (entityIn.world.getGameTime() + partialTicks) / 2;
        matrixStackIn.rotate(Vector3f.XP.rotationDegrees(t));
        matrixStackIn.rotate(Vector3f.ZP.rotationDegrees(t + 71));
        matrixStackIn.translate(0, -0.5, 0);
        // DefaultVertexFormats.BLOCK, NO_FOG, other options are all default
        RenderType renderType = RenderType.getBeaconBeam(this.getEntityTexture(entityIn), false);
        model.render(matrixStackIn, bufferIn.getBuffer(renderType),
                packedLightIn, OverlayTexture.NO_OVERLAY, 1, 1, 1, 1);
        matrixStackIn.pop();
    }

    @Override
    public ResourceLocation getEntityTexture(NewSunEntity entity) {
        switch (entity.getSunState()) {
            case NEW_BORN:
                return new ResourceLocation(MODID, "textures/entity/sun_yellow.png");
            case GROWING:
                switch (entity.getLastConsumedStone()) {
                    case WATER:
                        return new ResourceLocation(MODID, "textures/entity/sun_purple.png");
                    case FIRE:
                        return new ResourceLocation(MODID, "textures/entity/sun_red.png");
                    case EARTH:
                        return new ResourceLocation(MODID, "textures/entity/sun_green.png");
                    case LIGHTNING:
                        return new ResourceLocation(MODID, "textures/entity/sun_blue.png");
                    case POOP:
                    case NONE:
                    default:
                        return new ResourceLocation(MODID, "textures/entity/sun_white.png");
                }
            case FULL_YELLOW:
                return new ResourceLocation(MODID, "textures/entity/sun_white.png");
            case FULL_BLACK:
            default:
                return new ResourceLocation(MODID, "textures/entity/sun_black.png");
        }
    }
}
