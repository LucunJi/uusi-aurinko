package io.github.lucunji.uusiaurinko.tileentity;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@SuppressWarnings("ConstantConditions")
public class ModTileEntityTypes {
    public final static DeferredRegister<TileEntityType<?>> TILE_ENTITY = DeferredRegister.create(ForgeRegistries.TILE_ENTITIES, MODID);

    public final static RegistryObject<TileEntityType<TransmutingTileEntity>> TRANSMUTING_BLOCK = TILE_ENTITY.register("transmuting_block",
            () -> TileEntityType.Builder.create(TransmutingTileEntity::new, ModBlocks.TRANSMUTING_BLOCK.get()).build(null));
}
