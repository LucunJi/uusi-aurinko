package io.github.lucunji.uusiaurinko.tileentity;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.client.render.entity.RadiativeItemEntityRenderer;
import io.github.lucunji.uusiaurinko.client.render.tileentity.TransmutingTileEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@SuppressWarnings("ConstantConditions")
public class ModTileEntityTypes {
    public final static DeferredRegister<TileEntityType<?>> TILE_ENTITY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);

    public final static RegistryObject<TileEntityType<TransmutingTileEntity>> TRANSMUTING_BLOCK = register("transmuting_block",
            () -> TileEntityType.Builder.create(TransmutingTileEntity::new, ModBlocks.TRANSMUTING_BLOCK.get()).build(null));

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(
            final String name, final Supplier<TileEntityType<T>> sup) {

        RegistryObject<TileEntityType<T>> registryObject = TILE_ENTITY.register(name, sup);
        return registryObject;
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientRenderer {
        public static final List<Runnable> RENDERER_BINDERS = new ArrayList<>();

        static {
            register(TRANSMUTING_BLOCK, TransmutingTileEntityRenderer::new);
        }

        public static <T extends TileEntity> void register(RegistryObject<TileEntityType<T>> registryObject,
                                                       Function<? super TileEntityRendererDispatcher, TileEntityRenderer<? super T>> rendererFactory) {
            RENDERER_BINDERS.add(() -> ClientRegistry.bindTileEntityRenderer(registryObject.get(), rendererFactory));
        }
    }
}
