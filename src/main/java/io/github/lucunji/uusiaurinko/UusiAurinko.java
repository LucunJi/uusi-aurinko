package io.github.lucunji.uusiaurinko;

import io.github.lucunji.uusiaurinko.advancements.ModCriteriaTriggers;
import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.config.ClientConfigs;
import io.github.lucunji.uusiaurinko.config.ServerConfigs;
import io.github.lucunji.uusiaurinko.effects.ModEffects;
import io.github.lucunji.uusiaurinko.entity.ModEntityTypes;
import io.github.lucunji.uusiaurinko.fluid.ModFluids;
import io.github.lucunji.uusiaurinko.item.ModItems;
import io.github.lucunji.uusiaurinko.network.ModDataSerializers;
import io.github.lucunji.uusiaurinko.particles.ModParticleTypes;
import io.github.lucunji.uusiaurinko.tileentity.ModTileEntityTypes;
import io.github.lucunji.uusiaurinko.util.ModSoundEvents;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.network.datasync.DataSerializers;
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
        ModFluids.FLUIDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModItems.ITEMS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEffects.EFFECTS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModSoundEvents.SOUNDS.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModEntityTypes.ENTITY_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModTileEntityTypes.TILE_ENTITY.register(FMLJavaModLoadingContext.get().getModEventBus());
        ModParticleTypes.PARTICLE_TYPES.register(FMLJavaModLoadingContext.get().getModEventBus());

        FMLJavaModLoadingContext.get().getModEventBus().addListener(ServerConfigs.INSTANCE::onConfigLoadOrReload);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(ClientConfigs.INSTANCE::onConfigLoadOrReload);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.INSTANCE.getSpec());
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigs.INSTANCE.getSpec());

        ModDataSerializers.REGISTRY.forEach(DataSerializers::registerSerializer);
        ModCriteriaTriggers.REGISTRY.forEach(CriteriaTriggers::register);
    }
}
