package io.github.lucunji.uusiaurinko.client.render.tileentity;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import io.github.lucunji.uusiaurinko.tileentity.TransmutingTileEntity;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.model.data.EmptyModelData;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.Random;

import static net.minecraft.client.renderer.RenderType.makeType;

public class TransmutingTileEntityRenderer extends TileEntityRenderer<TransmutingTileEntity> {
    private static final BlockRendererDispatcher BLOCK_RENDERER_DISPATCHER = Minecraft.getInstance().getBlockRendererDispatcher();
    private static RenderType transparentBox = null;
    private static final float Z_FIGHTING_SCALE = 0.999f;
    private static final float Z_FIGHTING_TRANSLATION = (1 - Z_FIGHTING_SCALE) / 2;

    public TransmutingTileEntityRenderer(TileEntityRendererDispatcher rendererDispatcherIn) {
        super(rendererDispatcherIn);
    }

    @Override
    public void render(TransmutingTileEntity tileEntityIn, float partialTicks, MatrixStack matrixStackIn, IRenderTypeBuffer bufferIn, int combinedLightIn, int combinedOverlayIn) {
        BlockPos pos = tileEntityIn.getPos();
        float progress = MathHelper.clamp(tileEntityIn.getProgress() + 0.1f * partialTicks, 0, 1);
        BlockState renderState = progress < 0.5 ? tileEntityIn.getBlockStateFrom() : tileEntityIn.getBlockStateTo();

        matrixStackIn.push();
        matrixStackIn.scale(Z_FIGHTING_SCALE, Z_FIGHTING_SCALE, Z_FIGHTING_SCALE);
        matrixStackIn.translate(Z_FIGHTING_TRANSLATION, Z_FIGHTING_TRANSLATION, Z_FIGHTING_TRANSLATION);
        for (RenderType type : RenderType.getBlockRenderTypes()) {
            if (RenderTypeLookup.canRenderInLayer(renderState, type)) {
                ForgeHooksClient.setRenderLayer(type);
                BLOCK_RENDERER_DISPATCHER.getBlockModelRenderer().renderModel(
                        this.renderDispatcher.world, BLOCK_RENDERER_DISPATCHER.getModelForState(renderState), renderState,
                        pos, matrixStackIn, bufferIn.getBuffer(type), false,
                        new Random(), renderState.getPositionRandom(pos), combinedOverlayIn, EmptyModelData.INSTANCE
                );
            }
        }
        matrixStackIn.pop();

        matrixStackIn.push();
        if (transparentBox != null || canGetTransparencyState()) {
            // alpha = (progress * (1 - progress) * 4)^4 = progress^4 * (1 - progress)^4 * 256
            // increase the exponent to make slow-in slow-out more distinct
            float alpha = progress * (1 - progress) * 4;
            alpha *= alpha;
            alpha *= alpha;
            addChainedFilledBoxVertices(matrixStackIn, bufferIn.getBuffer(transparentBox),
                    0, 0, 0, 1, 1, 1,
                    1, 1, 1, alpha);
        }
        matrixStackIn.pop();
    }

    /**
     * Copied and modified from class {@link WorldRenderer}
     */
    @SuppressWarnings("DuplicatedCode")
    public static void addChainedFilledBoxVertices(MatrixStack matrixStackIn, IVertexBuilder builder, float x1, float y1, float z1, float x2, float y2, float z2, float red, float green, float blue, float alpha) {
        Matrix4f matrix = matrixStackIn.getLast().getMatrix();
        builder.pos(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y1, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y1, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x1, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y2, z1).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
        builder.pos(matrix, x2, y2, z2).color(red, green, blue, alpha).endVertex();
    }

    private static boolean canGetTransparencyState() {
        RenderState.TransparencyState transparencyState;
        try {
            transparencyState = ObfuscationReflectionHelper.getPrivateValue(RenderState.class, null, "TRANSLUCENT_TRANSPARENCY");
            assert transparencyState != null;
        } catch (ObfuscationReflectionHelper.UnableToFindFieldException |
                ObfuscationReflectionHelper.UnableToAccessFieldException |
                AssertionError e) {
            e.printStackTrace();
            return false;
        }
        transparentBox = makeType("lines", DefaultVertexFormats.POSITION_COLOR, 5, 256,
                RenderType.State.getBuilder().transparency(transparencyState).build(false));
        return true;
    }
}
