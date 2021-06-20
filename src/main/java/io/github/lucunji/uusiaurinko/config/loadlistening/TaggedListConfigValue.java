package io.github.lucunji.uusiaurinko.config.loadlistening;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Set;

/**
 * A wrapper for {@code ForgeConfigSpec.ConfigValue<List<? extends String>>},
 * allowing normal registry name and tags as entries of the string list.
 * The registry names and tags are interpreted into a set for quick finding.
 *
 * @param <T> the type of tag
 */
public abstract class TaggedListConfigValue<T> extends WrappedConfigValue<List<? extends String>> {
    private static final Logger LOGGER = LogManager.getLogger(BlockTaggedListConfigValue.class);

    private final Set<T> singletons;
    private final List<ResourceLocation> tags;

    protected TaggedListConfigValue(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue, LoadListeningConfigManagerAbstract manager) {
        super(configValue, manager);
        this.singletons = Sets.newIdentityHashSet();
        this.tags = Lists.newArrayList();
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
        singletons.clear();
        tags.clear();
        for (String raw : this.getConfigValue().get()) {
            if (raw.startsWith("#")) {
                tags.add(new ResourceLocation(raw.substring(1)));
            } else {
                T singleton = this.string2SingletonValue(raw);
                if (singleton != null) singletons.add(singleton);
            }
        }
    }

    /**
     * Parse a raw config string into a singleton instance of the target type.
     * Any discrepancy should be reported and replaced with a proper value (usually {@code null}).
     */
    protected abstract T string2SingletonValue(String raw);

    /**
     * @return {@code true} if the input instance is listed as a single entry in the list or an entry in one of the listed tags.
     */
    public abstract boolean contains(T val);

    public Set<T> getSingletons() {
        return singletons;
    }

    public List<ResourceLocation> getTags() {
        return tags;
    }
}
