package io.github.lucunji.uusiaurinko.config.loadlistening;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
import java.util.function.Predicate;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

/**
 * The manager of {@link WrappedConfigValue}.
 * It updates each registered {@link WrappedConfigValue} when game loads or reloads the corresponding config file.
 */
public abstract class LoadListeningConfigManagerAbstract {
    private final List<ILoadListeningConfigValue> listeningConfigs = Lists.newArrayList();

    final <T extends ILoadListeningConfigValue> T register(T value) {
        listeningConfigs.add(value);
        return value;
    }

    /**
     * Getter of the {@link ForgeConfigSpec} instance corresponding to the current config,
     * used to identify config in {@code onConfigLoadOrReload()} event handler.
     *
     * @return the {@link ForgeConfigSpec} instance corresponding to the current config
     */
    protected abstract ForgeConfigSpec getSpec();

    /**
     * This method must be registered in {@code net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus.MOD}
     * to update the registered {@link ILoadListeningConfigValue} when game loads or reloads the config file
     */
    public final void onConfigLoadOrReload(ModConfig.ModConfigEvent configEvent) {
        if (configEvent.getConfig().getSpec() == this.getSpec()) {
            if (configEvent instanceof ModConfig.Loading)
                listeningConfigs.forEach(ILoadListeningConfigValue::onLoad);
            if (configEvent instanceof ModConfig.Reloading)
                listeningConfigs.forEach(ILoadListeningConfigValue::onReload);
        }
    }

    /*======================== Helper Methods ========================*/

    public static ForgeConfigSpec.ConfigValue<Boolean> defineBoolean(ForgeConfigSpec.Builder builder,
                                                                     String comment, String key, boolean defaultValue) {
        return builder.comment(comment).translation("config." + MODID + "." + key).define(key, defaultValue);
    }

    public static ForgeConfigSpec.ConfigValue<Integer> defineInteger(ForgeConfigSpec.Builder builder,
                                                                     String comment, String key, int defaultValue, int min, int max) {
        return builder.comment(comment).translation("config." + MODID + "." + key).defineInRange(key, defaultValue, min, max);
    }

    public static <T> ForgeConfigSpec.ConfigValue<List<? extends T>> defineList(ForgeConfigSpec.Builder builder,
                                                                                   String comment, String key,
                                                                                   List<? extends T> defaultValue, Predicate<Object> validator) {
        return builder.comment(comment).translation("config." + MODID + "." + key).defineList(key, defaultValue, validator);
    }

    public static <T> ForgeConfigSpec.ConfigValue<List<? extends T>> defineList(ForgeConfigSpec.Builder builder,
                                                                                   String comment, String key,
                                                                                   List<? extends T> defaultValue) {
        return defineList(builder, comment, key, defaultValue, o -> true);
    }
}
