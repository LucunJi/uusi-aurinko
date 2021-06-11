package io.github.lucunji.uusiaurinko.config;

import com.google.common.collect.Lists;
import io.github.lucunji.uusiaurinko.config.taglist.BlockTagListConfigValue;
import io.github.lucunji.uusiaurinko.config.taglist.TagListConfigManagerAbstract;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfigs extends TagListConfigManagerAbstract {
    public static final ForgeConfigSpec CONFIG_SPEC;
    public static final ServerConfigs INSTANCE;

    static {
        Pair<ServerConfigs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ServerConfigs::new);
        INSTANCE = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ForgeConfigSpec.ConfigValue<Boolean> EARTH_STONE_TRANSMUTATION_ENABLED;
    public final ForgeConfigSpec.ConfigValue<Integer> EARTH_STONE_TRANSMUTATION_RANGE;
    public final BlockTagListConfigValue EARTH_STONE_TRANSMUTATION_BLACKLIST;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        EARTH_STONE_TRANSMUTATION_ENABLED = defineBoolean(builder,
                "Enables the transmutation feature of Tannerkivi, the earth stone.",
                "earth_stone_transmutation_enabled",
                true);
        EARTH_STONE_TRANSMUTATION_RANGE = defineInteger(builder,
                "Range in which the Tannerkivi, the earth stone, transmutes blocks into dirt.",
                "earth_stone_transmutation_range",
                4, 1, 64);
        EARTH_STONE_TRANSMUTATION_BLACKLIST = new BlockTagListConfigValue(defineList(builder,
                "Blocks in this blacklist won't be transmuted by Tannerkivi, the earth stone.",
                "earth_stone_transmutation_blacklist",
                Lists.newArrayList("#uusi-aurinko:transmutation_immune")
        ), this);
    }

    @Override
    protected ForgeConfigSpec getSpec() {
        return CONFIG_SPEC;
    }
}
