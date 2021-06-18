package io.github.lucunji.uusiaurinko.tileentity;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.client.render.tileentity.TransmutingTileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRenderer;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@SuppressWarnings("ConstantConditions")
public class ModTileEntityTypes {
    public final static List<Runnable> RENDERER_BINDERS = new ArrayList<>();

    public final static DeferredRegister<TileEntityType<?>> TILE_ENTITY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);

    public final static RegistryObject<TileEntityType<TransmutingTileEntity>> TRANSMUTING_BLOCK = register("transmuting_block",
            () -> TileEntityType.Builder.create(TransmutingTileEntity::new, ModBlocks.TRANSMUTING_BLOCK.get()).build(null),
            TransmutingTileEntityRenderer::new);

    private static <T extends TileEntity> RegistryObject<TileEntityType<T>> register(
            final String name, final Supplier<TileEntityType<T>> sup,
            Function<? super TileEntityRendererDispatcher, TileEntityRenderer<? super T>> rendererFactory) {

        RegistryObject<TileEntityType<T>> registryObject = TILE_ENTITY.register(name, sup);
        RENDERER_BINDERS.add(() -> ClientRegistry.bindTileEntityRenderer(registryObject.get(), rendererFactory));
        return registryObject;
    }
}
