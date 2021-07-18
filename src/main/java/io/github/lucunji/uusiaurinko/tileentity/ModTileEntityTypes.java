package io.github.lucunji.uusiaurinko.tileentity;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.client.render.tileentity.PedestalTileEntityRenderer;
import io.github.lucunji.uusiaurinko.client.render.tileentity.TransmutingTileEntityRenderer;
import net.minecraft.client.Minecraft;
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

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@SuppressWarnings("ConstantConditions")
public class ModTileEntityTypes {
    public final static DeferredRegister<TileEntityType<?>> TILE_ENTITY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);

    public final static RegistryObject<TileEntityType<TransmutingTileEntity>> TRANSMUTING_BLOCK = TILE_ENTITY.register("transmuting_block",
            () -> TileEntityType.Builder.create(TransmutingTileEntity::new, ModBlocks.TRANSMUTING_BLOCK.get()).build(null));

    public final static RegistryObject<TileEntityType<PedestalTileEntity>> ITEM_PEDESTAL = TILE_ENTITY.register("item_pedestal",
            () -> TileEntityType.Builder.create(PedestalTileEntity::new, ModBlocks.ITEM_PEDESTAL.get()).build(null));

    public static class ClientRenderer {
        public static final List<Runnable> RENDERER_BINDERS = new ArrayList<>();

        static {
            registerRenderer(TRANSMUTING_BLOCK, TransmutingTileEntityRenderer::new);
            registerRenderer(ITEM_PEDESTAL, dispatcher -> new PedestalTileEntityRenderer(
                    dispatcher, Minecraft.getInstance().getItemRenderer()));
        }

        public static <T extends TileEntity> void registerRenderer(RegistryObject<TileEntityType<T>> registryObject,
                                                                   Function<? super TileEntityRendererDispatcher, TileEntityRenderer<? super T>> rendererFactory) {
            RENDERER_BINDERS.add(() -> ClientRegistry.bindTileEntityRenderer(registryObject.get(), rendererFactory));
        }
    }
}
