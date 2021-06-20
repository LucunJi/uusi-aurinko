package io.github.lucunji.uusiaurinko.config.loadlistening;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * A wrapper for {@link ForgeConfigSpec.ConfigValue}.
 * It implements ILoadListeningConfigValue to allow certain operations to be done when it gets loaded/reloaded.
 * It is updated by an instance of {@link LoadListeningConfigManagerAbstract}.
 *
 * @param <T> the type of tag
 */
public abstract class WrappedConfigValue<T> implements ILoadListeningConfigValue {
    private final ForgeConfigSpec.ConfigValue<T> configValue;

    protected WrappedConfigValue(ForgeConfigSpec.ConfigValue<T> configValue, LoadListeningConfigManagerAbstract manager) {
        this.configValue = configValue;
        manager.register(this);
    }

    /*======================== Getters ========================*/

    public final ForgeConfigSpec.ConfigValue<T> getConfigValue() {
        return configValue;
    }

    public final T get() {
        return configValue.get();
    }
}
