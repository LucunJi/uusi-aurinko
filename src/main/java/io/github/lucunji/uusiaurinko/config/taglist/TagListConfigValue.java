package io.github.lucunji.uusiaurinko.config.taglist;

import com.google.common.collect.Sets;
import io.github.lucunji.uusiaurinko.util.IdentitySetCollector;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Stream;

/**
 * A wrapper for {@code ForgeConfigSpec.ConfigValue<List<? extends String>>}, allowing normal registry name and tags.
 * The registry names and tags are interpreted into a set for quick finding.
 * It is updated by an instance of {@link TagListConfigManagerAbstract}.
 *
 * @param <T> the type of tag
 */
public abstract class TagListConfigValue<T> {
    private final ForgeConfigSpec.ConfigValue<List<? extends String>> configValue;
    private Set<T> set;

    protected TagListConfigValue(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue, TagListConfigManagerAbstract manager) {
        this.configValue = configValue;
        this.set = Sets.newIdentityHashSet();
        manager.register(this);
    }

    /**
     * Parse the wrapped {@link net.minecraftforge.common.ForgeConfigSpec.ConfigValue} into a set to allow fast search.
     * The values are checked.
     */
    protected final void parseSet() {
        this.set = configValue
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

    public final ForgeConfigSpec.ConfigValue<List<? extends String>> getConfigValue() {
        return configValue;
    }

    public final List<? extends String> get() {
        return configValue.get();
    }
}
