package io.github.lucunji.uusiaurinko.client.render.entity.model;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.model.ModelRenderer;

public class NewSunModel extends Model {
    private final ModelRenderer renderer;

    public NewSunModel() {
        super(RenderType::getEntitySolid);
        this.renderer = new ModelRenderer(this);
        this.renderer.setTextureOffset(0, 0);
        this.renderer.addBox(-5, -5, -5, 5, 5, 5);
    }

    @Override
    public void render(MatrixStack matrixStackIn, IVertexBuilder bufferIn, int packedLightIn, int packedOverlayIn, float red, float green, float blue, float alpha) {
        renderer.render(matrixStackIn, bufferIn, packedLightIn, packedOverlayIn, red, green, blue, alpha);
    }


}
