package io.github.lucunji.uusiaurinko.config;

import com.google.common.collect.Lists;
import io.github.lucunji.uusiaurinko.config.loadlistening.BlockTaggedListConfigValue;
import io.github.lucunji.uusiaurinko.config.loadlistening.EntityTypeTaggedListConfigValue;
import io.github.lucunji.uusiaurinko.config.loadlistening.LoadListeningConfigManagerAbstract;
import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ServerConfigs extends LoadListeningConfigManagerAbstract {
    private static final ForgeConfigSpec CONFIG_SPEC;
    public static final ServerConfigs INSTANCE;

    static {
        Pair<ServerConfigs, ForgeConfigSpec> pair = new ForgeConfigSpec.Builder().configure(ServerConfigs::new);
        INSTANCE = pair.getLeft();
        CONFIG_SPEC = pair.getRight();
    }

    public final ForgeConfigSpec.ConfigValue<Integer> FIRE_STONE_IGNITE_BLOCK_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> FIRE_STONE_IGNITE_FLAMMABLE_IMPROBABILITY;
    public final ForgeConfigSpec.ConfigValue<Double> FIRE_STONE_IGNITE_BLOCK_BASE_CHANCE;
    public final ForgeConfigSpec.ConfigValue<Double> FIRE_STONE_IGNITE_ENTITY_RANGE;

    public final ForgeConfigSpec.ConfigValue<Integer> WATER_STONE_SOLIDIFY_LAVA_RANGE;
    public final ForgeConfigSpec.ConfigValue<Double> WATER_STONE_DAMAGE_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> WATER_STONE_EXTINGUISH_FIRE_RANGE;

    public final ForgeConfigSpec.ConfigValue<Integer> LIGHTNING_STONE_ELECTRICITY_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> LIGHTNING_STONE_ELECTRICITY_INTERVAL;
    public final ForgeConfigSpec.ConfigValue<Double> LIGHTNING_STONE_ELECTRICITY_SHOOK_CHANCE;
    public final EntityTypeTaggedListConfigValue LIGHTNING_STONE_ELECTRICITY_IMMUNE_ENTITY_TYPES;

    public final BlockTaggedListConfigValue EARTH_STONE_TRANSMUTATION_BLACKLIST;
    public final ForgeConfigSpec.ConfigValue<Integer> EARTH_STONE_TRANSMUTATION_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> EARTH_STONE_EARTHQUAKE_RANGE;
    public final BlockTaggedListConfigValue EARTH_STONE_EARTHQUAKE_BLACKLIST;
    public final ForgeConfigSpec.ConfigValue<Integer> EARTH_STONE_EARTHQUAKE_PARTICLE_AMOUNT;

    public final ForgeConfigSpec.ConfigValue<Integer> POOP_STONE_TRANSMUTATION_RANGE;
    public final ForgeConfigSpec.ConfigValue<Double> POOP_STONE_DEBUFF_CHANCE;

    public final ForgeConfigSpec.ConfigValue<Integer> EXCREMENT_DEBUFF_DURATION;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        // TODO: TEST ALL OF THEM!!!
        FIRE_STONE_IGNITE_BLOCK_RANGE = defineInteger(builder,
                "The size of the cuboid which goes from (-range, -range, -range) to (range, range + 2, range) " +
                        "in which Kiuaskivi may ignite blocks. Set this value to 0 to disable.",
                        "fire_stone_ignite_block_range", 1, 0, 64);
        FIRE_STONE_IGNITE_BLOCK_BASE_CHANCE = defineDouble(builder,
                "The base probability that any block in range may be ignited by Kiuaskivi in every tick." +
                        "Set this value to 0 to prevent inflammable blocks from fire.",
                "fire_stone_ignite_block_base_chance", 0.01, 0, 1);
        FIRE_STONE_IGNITE_FLAMMABLE_IMPROBABILITY = defineInteger(builder,
                "How unlikely a flammable block in range may be ignited by Kiuaskivi in every tick." +
                        "How unlikely a flammable block in range may be ignited by Kiuaskivi in every tick." +
                        "The ignition chance per tick is (flammability / improbability + base chance). " +
                        "Flammability is 0 for inflammable blocks, with a maximum of 300, and 20 for a typical wooden block.",
                "fire_stone_ignite_flammable_improbability", 50, 1, Integer.MAX_VALUE);
        FIRE_STONE_IGNITE_ENTITY_RANGE = defineDouble(builder,
                "The distance by which the hitbox of Kiuaskivi's holder/item entity will grow in each direction. " +
                        "All entities in this expanded box will be ignited." +
                        "Set this value to 0 to disable.",
                "fire_stone_ignite_entity_range", 0.5, 0, 64);


        WATER_STONE_SOLIDIFY_LAVA_RANGE = defineInteger(builder,
                "The radius in which Vuoksikivi solidifies lava source blocks into semisolid lava. " +
                        "Set this value to 0 to disable.",
                "water_stone_solidify_lava_range", 2, 0, 64);
        WATER_STONE_DAMAGE_RANGE = defineDouble(builder,
                "The distance by which the hitbox of Vuoksikivi's holder/item entity will grow in each direction. " +
                        "Fire-sensitive entities, such as blazeman, in this expanded box takes drown damage. " +
                        "Set this value to 0 to disable.",
                "water_stone_damage_range", 1, 0, 64);
        WATER_STONE_EXTINGUISH_FIRE_RANGE = defineInteger(builder,
                "The radius in which Vuoksikivi extinguishes fire and campfire blocks. " +
                        "Set this value to 0 to disable.",
                "water_stone_extinguish_fire_range", 2, 0, 64);


        LIGHTNING_STONE_ELECTRICITY_RANGE = defineInteger(builder,
                "The radius in which the electricity Ukkoskivi may spread. " +
                        "Set this value to 0 to disable.",
                "lightning_stone_electricity_range", 16, 0, 64);
        LIGHTNING_STONE_ELECTRICITY_INTERVAL = defineInteger(builder,
                "The interval for Ukkoskivi to discharge electricity.",
                "lightning_stone_electricity_interval", 30, 0, Integer.MAX_VALUE);
        LIGHTNING_STONE_ELECTRICITY_SHOOK_CHANCE = defineDouble(builder,
                "The probability that a creature in range may take damage and receive debuff from the electricity of Ukkoskivi. " +
                        "Set this value to 0 to disable.",
                "lightning_stone_electricity_shook_chance", 1, 0, 1);
        LIGHTNING_STONE_ELECTRICITY_IMMUNE_ENTITY_TYPES = new EntityTypeTaggedListConfigValue(defineList(builder,
                "The types of creatures immune to the electricity from Ukkoskivi. (non-creatures are already excluded)",
                "lightning_stone_electricity_immune_entity_types",
                Lists.newArrayList("#uusi-aurinko:electric_immune")), this);


        EARTH_STONE_TRANSMUTATION_RANGE = defineInteger(builder,
                "The radius in which Tannerkivi transmutes blocks into dirt. " +
                        "Set this value to 0 to disable.",
                "earth_stone_transmutation_range", 2, 0, 64);
        EARTH_STONE_TRANSMUTATION_BLACKLIST = new BlockTaggedListConfigValue(defineList(builder,
                "Blocks in this blacklist won't be transmuted by Tannerkivi. " +
                        "Also accept block tags starting with #",
                "earth_stone_transmutation_blacklist",
                Lists.newArrayList("#uusi-aurinko:transmutation_immune")
        ), this);
        EARTH_STONE_EARTHQUAKE_RANGE = defineInteger(builder,
                "The radius in which the earthquake of Tannerkivi shakes blocks off. " +
                        "Set this value to 0 to disable.",
                "earth_stone_earthquake_range", 6, 0, 64);
        EARTH_STONE_EARTHQUAKE_BLACKLIST = new BlockTaggedListConfigValue(defineList(builder,
                "Blocks in this blacklist won't be affect by the earthquake of Tannerkivi." +
                        " Also accept block tags starting with #",
                "earth_stone_earthquake_blacklist",
                Lists.newArrayList("#uusi-aurinko:earthquake_immune")
        ), this);
        EARTH_STONE_EARTHQUAKE_PARTICLE_AMOUNT = defineInteger(builder,
                "The per block amount of particle made by the earthquake of Tannerkivi. " +
                        "Set this value to 0 to disable.",
                "earth_stone_earthquake_particle_amount", 7, 0, 64);


        POOP_STONE_TRANSMUTATION_RANGE = defineInteger(builder,
                "The radius in which Kakkakikkare transmutes fluids into excrement.json. " +
                        "Set this value to 0 to disable.",
                "poop_stone_transmutation_range", 2, 0, 64);
        POOP_STONE_DEBUFF_CHANCE = defineDouble(builder,
                "The chance that Kakkakikkare may cause various debuff to its holder in every tick. " +
                        "Set this value to 0 to disable debuff.",
                "poop_stone_debuff_chance", 0.01, 0, 1);

        EXCREMENT_DEBUFF_DURATION = defineInteger(builder,
                "The duration of debuff in ticks that the excrement fluid block may cast on creatures inside. " +
                        "Set this value to 0 to disable.",
                "excrement_debuff_duration", 200, 0, 1000000);
    }

    @Override
    public ForgeConfigSpec getSpec() {
        return CONFIG_SPEC;
    }
}
