package io.github.lucunji.uusiaurinko.config.loadlistening;

import com.google.common.collect.Sets;
import io.github.lucunji.uusiaurinko.util.IdentitySetCollector;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A wrapper for {@code ForgeConfigSpec.ConfigValue<List<? extends String>>},
 * allowing normal registry name and tags as entries of the string list.
 * The registry names and tags are interpreted into a set for quick finding.
 *
 * @param <T> the type of tag
 */
public abstract class TagListConfigValue<T> extends WrappedConfigValue<List<? extends String>> {
    private Set<T> set;

    protected TagListConfigValue(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue, LoadListeningConfigManagerAbstract manager) {
        super(configValue, manager);
        this.set = Sets.newIdentityHashSet();
    }

    @Override
    public final void onLoad() {
        this.onLoadOrReload();
    }

    @Override
    public final void onReload() {
        this.onLoadOrReload();
    }

    /**
     * Parse the wrapped {@link ForgeConfigSpec.ConfigValue} into a set to allow fast search.
     * The values are checked.
     */
    private void onLoadOrReload() {
        this.set = this.getConfigValue()
                .get()
                .stream()
                .flatMap(this::flatMapString2StreamAndValidate)
                .filter(Objects::nonNull)
                .collect(new IdentitySetCollector<>());
    }

    /**
     * Parse a raw input string into a {@link Stream} of the target type.
     * Any discrepancy should be reported and replaced with a proper value (usually {@code null}).
     */
    protected abstract Stream<T> flatMapString2StreamAndValidate(final String raw);

    /*======================== Getters ========================*/

    public final boolean contains(T val) {
        return set.contains(val);
    }
}
