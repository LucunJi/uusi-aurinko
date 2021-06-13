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

    public final BlockTagListConfigValue EARTH_STONE_TRANSMUTATION_BLACKLIST;
    public final ForgeConfigSpec.ConfigValue<Integer> EARTH_STONE_TRANSMUTATION_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> EARTH_STONE_EARTHQUAKE_RANGE;
    public final BlockTagListConfigValue EARTH_STONE_EARTHQUAKE_BLACKLIST;
    public final ForgeConfigSpec.ConfigValue<Integer> EARTH_STONE_EARTHQUAKE_PARTICLE_AMOUNT;

    public final ForgeConfigSpec.ConfigValue<Integer> POOP_STONE_TRANSMUTATION_RANGE;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        EARTH_STONE_TRANSMUTATION_RANGE = defineInteger(builder,
                "Range in which the Tannerkivi, the earth stone, transmutes blocks into dirt. Set to 0 to disable.",
                "earth_stone_transmutation_range",
                4, 0, 64);
        EARTH_STONE_TRANSMUTATION_BLACKLIST = new BlockTagListConfigValue(defineList(builder,
                "Blocks in this blacklist won't be transmuted by Tannerkivi, the earth stone.",
                "earth_stone_transmutation_blacklist",
                Lists.newArrayList("#uusi-aurinko:transmutation_immune")
        ), this);
        EARTH_STONE_EARTHQUAKE_RANGE = defineInteger(builder,
                "Range in which the earthquake of Tannerkivi, the earth stone, shakes blocks off. Set to 0 to disable.",
                "earth_stone_earthquake_range",
                8, 0, 64);
        EARTH_STONE_EARTHQUAKE_BLACKLIST = new BlockTagListConfigValue(defineList(builder,
                "Blocks in this blacklist won't be affect by the earthquake of Tannerkivi, the earth stone.",
                "earth_stone_earthquake_blacklist",
                Lists.newArrayList("#uusi-aurinko:earthquake_immune")
        ), this);
        EARTH_STONE_EARTHQUAKE_PARTICLE_AMOUNT = defineInteger(builder,
                "Per block amount of particle made by the earthquake of Tannerkivi, the earth stone. Set to 0 to disable particles.",
                "earth_stone_earthquake_particle_amount",
                7, 0, 64);


        POOP_STONE_TRANSMUTATION_RANGE = defineInteger(builder,
                "Range in which the Kakkakikkare, the poop stone, transmutes fluids into excrement.json. Set to 0 to disable.",
                "earth_stone_transmutation_range",
                4, 0, 64);
    }

    @Override
    protected ForgeConfigSpec getSpec() {
        return CONFIG_SPEC;
    }
}
