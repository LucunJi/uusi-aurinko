package io.github.lucunji.uusiaurinko.config;

import io.github.lucunji.uusiaurinko.config.loadlistening.ChangeDetectingConfigValue;
import io.github.lucunji.uusiaurinko.config.loadlistening.LoadListeningConfigManagerAbstract;
import io.github.lucunji.uusiaurinko.fluid.ModFluids;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

import static net.minecraft.client.renderer.RenderType.getSolid;
import static net.minecraft.client.renderer.RenderType.getTranslucent;

public class ClientConfigs extends LoadListeningConfigManagerAbstract {
    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final ClientConfigs INSTANCE;

    static {
        Pair<ClientConfigs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfigs::new);
        INSTANCE = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ChangeDetectingConfigValue<Boolean> TRANSPARENT_FLUID;

    public ClientConfigs(ForgeConfigSpec.Builder builder) {
        TRANSPARENT_FLUID = new ChangeDetectingConfigValue<>(defineBoolean(builder,
                "If to render mod fluid blocks with transparency, from which minor glitches may arise. " +
                        "Press F3+T after changing this value to take effect.",
                "transparent_fluid",
                true), this, (lastVal, val) -> {
            ModFluids.FLUIDS.getEntries().forEach(registryObject ->
                    RenderTypeLookup.setRenderLayer(registryObject.get(), val ? getTranslucent() : getSolid()));
            if (Minecraft.getInstance().player != null)
                Minecraft.getInstance().player.sendStatusMessage(
                        new TranslationTextComponent("config.uusi-aurinko.change_fluid_rendering"), false);
        });
    }

    @Override
    protected ForgeConfigSpec getSpec() {
        return CONFIG_SPEC;
    }
}
