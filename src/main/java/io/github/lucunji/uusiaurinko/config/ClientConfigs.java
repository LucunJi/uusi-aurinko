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
    private static final ForgeConfigSpec CONFIG_SPEC;
    public static final ClientConfigs INSTANCE;

    static {
        Pair<ClientConfigs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ClientConfigs::new);
        INSTANCE = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ForgeConfigSpec.ConfigValue<Double> LIGHTNING_STONE_SPARK_PARTICLE_AMOUNT;
    public final ChangeDetectingConfigValue<Boolean> TRANSPARENT_FLUID;
    public final ForgeConfigSpec.ConfigValue<Integer> SUN_HALO_ITERATIONS;

    public ClientConfigs(ForgeConfigSpec.Builder builder) {
        LIGHTNING_STONE_SPARK_PARTICLE_AMOUNT = defineDouble(builder,
                "The probability that spark particles may appear on a surface of electrified block.\n" +
                        "All particles will be rendered if there are less than 20 exposed surfaces when this value is greater than 0.\n" +
                        "Set this value to 0 to disable particles.",
        "lightning_stone_spark_particle_amount", 0.4, 0, 1);

        TRANSPARENT_FLUID = new ChangeDetectingConfigValue<>(defineBoolean(builder,
                "If to render mod fluid blocks with transparency, from which minor glitches may arise.\n" +
                        "Press F3+T after changing this value to take effect.",
                "transparent_fluid",
                true), this, (lastVal, val) -> {
            ModFluids.FLUIDS.getEntries().forEach(registryObject ->
                    RenderTypeLookup.setRenderLayer(registryObject.get(), val ? getTranslucent() : getSolid()));
            if (Minecraft.getInstance().player != null)
                Minecraft.getInstance().player.sendStatusMessage(
                        new TranslationTextComponent("config.uusi-aurinko.change_fluid_rendering"), false);
        });

        SUN_HALO_ITERATIONS = defineInteger(builder,
                "Higher value gives a more smooth transition to the sun's halo,\n" +
                        "while a lower value may enhance your FPS.\n" +
                        "Set this value to 0 to disable halo.",
                "sun_halo_iterations", 60, 0, 128);
    }

    @Override
    public ForgeConfigSpec getSpec() {
        return CONFIG_SPEC;
    }
}
