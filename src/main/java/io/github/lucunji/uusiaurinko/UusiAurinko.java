package io.github.lucunji.uusiaurinko;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.effects.ModEffects;
import io.github.lucunji.uusiaurinko.entity.ModEntityTypes;
import io.github.lucunji.uusiaurinko.item.ModItems;
import io.github.lucunji.uusiaurinko.particles.ModParticleTypes;
import io.github.lucunji.uusiaurinko.tileentity.ModTileEntityTypes;
import io.github.lucunji.uusiaurinko.util.ModSoundEvents;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(UusiAurinko.MODID)
public class UusiAurinko {
    // The value here should match entries in the META-INF/mods.toml file
    public static final String MODID = "uusi-aurinko";
    public static final String NAME = "Uusi Aurinko";
    public static final String VERSION = "0.1.0";

    public UusiAurinko() {
        ModBlocks.BLOCKS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEffects.EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModSoundEvents.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEntityTypes.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModTileEntityTypes.TILE_ENTITY.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModParticleTypes.PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ServerConfigs.INSTANCE::onConfigLoadOrReload);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.CONFIG_SPEC);
    }
}
