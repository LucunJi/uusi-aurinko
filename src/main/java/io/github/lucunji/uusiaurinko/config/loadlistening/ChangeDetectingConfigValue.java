package io.github.lucunji.uusiaurinko.config.loadlistening;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.function.BiConsumer;

/**
 * It detects any change in the wrapped {@link net.minecraftforge.common.ForgeConfigSpec.ConfigValue}.
 * It only focus on changes, rather than loading/reloading events.
 */
public class ChangeDetectingConfigValue<T> extends WrappedConfigValue<T> {
    private T lastValue;
    private final BiConsumer<T, T> changeAction;
    public ChangeDetectingConfigValue(ForgeConfigSpec.ConfigValue<T> configValue, LoadListeningConfigManagerAbstract manager,
                                         BiConsumer<T, T> changeAction) {
        super(configValue, manager);
        lastValue = null;
        this.changeAction = changeAction;
    }

    @Override
    public final void onLoad() {
        this.onReload();
    }

    @Override
    public final void onReload() {
        T value = this.get();
        if (lastValue != null) {
            if (!lastValue.equals(value)) {
                this.changeAction.accept(lastValue, value);
            }
        }
        lastValue = value;
    }
}
