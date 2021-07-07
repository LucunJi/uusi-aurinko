package io.github.lucunji.uusiaurinko.config;

import com.google.common.collect.Lists;
import io.github.lucunji.uusiaurinko.config.loadlistening.*;
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
    public final ForgeConfigSpec.ConfigValue<Double> LIGHTNING_STONE_ELECTRICITY_SHOOK_DAMAGE;
    public final EntityTypeTaggedListConfigValue LIGHTNING_STONE_ELECTRICITY_IMMUNE_ENTITY_TYPES;

    public final BlockFluidCompositeConfigWrapper CONDUCTORS;

    public final BlockTaggedListConfigValue EARTH_STONE_TRANSMUTATION_BLACKLIST;
    public final ForgeConfigSpec.ConfigValue<Integer> EARTH_STONE_TRANSMUTATION_RANGE;
    public final ForgeConfigSpec.ConfigValue<Integer> EARTH_STONE_EARTHQUAKE_RANGE;
    public final BlockTaggedListConfigValue EARTH_STONE_EARTHQUAKE_BLACKLIST;
    public final ForgeConfigSpec.ConfigValue<Integer> EARTH_STONE_EARTHQUAKE_PARTICLE_AMOUNT;

    public final ForgeConfigSpec.ConfigValue<Integer> POOP_STONE_TRANSMUTATION_RANGE;
    public final ForgeConfigSpec.ConfigValue<Double> POOP_STONE_DEBUFF_CHANCE;

    public final ForgeConfigSpec.ConfigValue<Integer> EXCREMENT_DEBUFF_DURATION;

    public final ForgeConfigSpec.ConfigValue<Integer> SUN_SEED_EXPLOSION_RANGE;
    public final ForgeConfigSpec.ConfigValue<Double> SUN_SEED_EXPLOSION_CHANCE;
    public final ForgeConfigSpec.ConfigValue<Integer> SUN_SEED_EXPLOSION_INTERVAL;

    public final ForgeConfigSpec.ConfigValue<Integer> SUN_STONE_FIRE_RANGE;
    public final ForgeConfigSpec.ConfigValue<Double> SUN_STONE_FIRE_CHANCE;
    public final ForgeConfigSpec.ConfigValue<Integer> SUN_STONE_FIRE_INTERVAL;

    public final BlockTaggedListConfigValue POWDERY_BLOCK;

    public final BlockTaggedListConfigValue NEW_SUN_DESTROY_BLACKLIST;

    public ServerConfigs(ForgeConfigSpec.Builder builder) {
        FIRE_STONE_IGNITE_BLOCK_RANGE = defineInteger(builder,
                "Kiuaskivi may ignite blocks in the cuboid which goes from (-range, -range, -range) to (range, range + 2, range)\n" +
                        "Set this value to 0 to disable.",
                "fire_stone_ignite_block_range", 1, 0, 64);
        FIRE_STONE_IGNITE_BLOCK_BASE_CHANCE = defineDouble(builder,
                "The base probability of creating fire blocks near Kiuaskivi in each tick.\n" +
                        "Set this value to 0 to prevent inflammable blocks from fire.",
                "fire_stone_ignite_block_base_chance", 0.01, 0, 1);
        FIRE_STONE_IGNITE_FLAMMABLE_IMPROBABILITY = defineInteger(builder,
                "How unlikely a flammable block in range may be ignited by Kiuaskivi in every tick.\n" +
                        "The ignition chance per tick is (flammability / improbability + base chance).\n" +
                        "Flammability is 0 for inflammable blocks, with a maximum of 300, and 20 for a typical wooden block.",
                "fire_stone_ignite_flammable_improbability", 50, 1, Integer.MAX_VALUE);
        FIRE_STONE_IGNITE_ENTITY_RANGE = defineDouble(builder,
                "The hitbox of Kiuaskivi's holder/item entity will grow in each direction by this value, \n" +
                        "and all entities in this expanded box will be ignited quickly.\n" +
                        "Set this value to 0 to disable.",
                "fire_stone_ignite_entity_range", 0.5, 0, 64);


        WATER_STONE_SOLIDIFY_LAVA_RANGE = defineInteger(builder,
                "Vuoksikivi solidifies all lava source blocks within this distance into semisolid lava.\n" +
                        "Set this value to 0 to disable.",
                "water_stone_solidify_lava_range", 1, 0, 64);
        WATER_STONE_DAMAGE_RANGE = defineDouble(builder,
                "The hitbox of Vuoksikivi's holder/item entity will grow in each direction by this value, \n" +
                        "and fire-sensitive entities, such as blazeman, in this expanded box takes drown damage.\n" +
                        "Set this value to 0 to disable.",
                "water_stone_damage_range", 1, 0, 64);
        WATER_STONE_EXTINGUISH_FIRE_RANGE = defineInteger(builder,
                "Vuoksikivi extinguishes fire and campfire blocks within this distance.\n" +
                        "Set this value to 0 to disable.",
                "water_stone_extinguish_fire_range", 2, 0, 64);


        LIGHTNING_STONE_ELECTRICITY_RANGE = defineInteger(builder,
                "Ukkoskivi may electrify connected conductor blocks within this distance.\n" +
                        "Set this value to 0 to disable.",
                "lightning_stone_electricity_range", 10, 0, 64);
        LIGHTNING_STONE_ELECTRICITY_INTERVAL = defineInteger(builder,
                "The interval for Ukkoskivi to make electrical discharges.",
                "lightning_stone_electricity_interval", 40, 1, Integer.MAX_VALUE);
        LIGHTNING_STONE_ELECTRICITY_SHOOK_CHANCE = defineDouble(builder,
                "The probability of receiving damage and debuff by a creature connected to Ukkoskivi through conductors.\n" +
                        "Set this value to 0 to disable.",
                "lightning_stone_electricity_shook_chance", 1, 0, 1);
        LIGHTNING_STONE_ELECTRICITY_SHOOK_DAMAGE = defineDouble(builder,
                "The damage received by a creature connected to Ukkoskivi through conductors when Ukkoskivi discharges.",
                "lightning_stone_electricity_shook_damage", 2.5, 0, Float.MAX_VALUE);
        LIGHTNING_STONE_ELECTRICITY_IMMUNE_ENTITY_TYPES = new EntityTypeTaggedListConfigValue(defineList(builder,
                "The types of creatures immune to the electricity from Ukkoskivi. (non-creatures are already excluded)\n" +
                        "Also accept entity type tags starting with #",
                "lightning_stone_electricity_immune_entity_types",
                Lists.newArrayList("#uusi-aurinko:electric_immune")
        ), this);

        CONDUCTORS = new BlockFluidCompositeConfigWrapper(
                new BlockTaggedListConfigValue(defineList(builder,
                        "Blocks in this list can conduct electricity.\n" +
                                "Also accept block tags starting with #",
                        "conductive_blocks",
                        Lists.newArrayList("#uusi-aurinko:conductor")
                ), this),
                new FluidTaggedListConfigValue(defineList(builder,
                        "Fluids in this list can conduct electricity.\n" +
                                "Also accept fluid tags starting with #",
                        "conductive_fluids",
                        Lists.newArrayList("#uusi-aurinko:conductor")
                ), this)
        );


        EARTH_STONE_TRANSMUTATION_RANGE = defineInteger(builder,
                "Tannerkivi transmutes blocks in this range into dirt.\n" +
                        "Set this value to 0 to disable.",
                "earth_stone_transmutation_range", 2, 0, 64);
        EARTH_STONE_TRANSMUTATION_BLACKLIST = new BlockTaggedListConfigValue(defineList(builder,
                "Blocks in this blacklist won't be transmuted by Tannerkivi.\n" +
                        "Also accept block tags starting with #",
                "earth_stone_transmutation_blacklist",
                Lists.newArrayList("#uusi-aurinko:transmutation_immune")
        ), this);
        EARTH_STONE_EARTHQUAKE_RANGE = defineInteger(builder,
                "The earthquake of Tannerkivi collapses blocks in this range.\n" +
                        "Set this value to 0 to disable.",
                "earth_stone_earthquake_range", 6, 0, 64);
        EARTH_STONE_EARTHQUAKE_BLACKLIST = new BlockTaggedListConfigValue(defineList(builder,
                "Blocks in this blacklist won't be affect by the earthquake of Tannerkivi.\n" +
                        "Also accept block tags starting with #",
                "earth_stone_earthquake_blacklist",
                Lists.newArrayList("#uusi-aurinko:earthquake_immune")
        ), this);
        EARTH_STONE_EARTHQUAKE_PARTICLE_AMOUNT = defineInteger(builder,
                "The per block amount of particle made by the earthquake of Tannerkivi.\n" +
                        "Changing this value has no effects on server; it affects client performance instead.\n" +
                        "Set this value to 0 to disable.",
                "earth_stone_earthquake_particle_amount", 7, 0, 64);


        POOP_STONE_TRANSMUTATION_RANGE = defineInteger(builder,
                "Kakkakikkare transmutes fluid blocks within this distance into excrement.\n" +
                        "Set this value to 0 to disable.",
                "poop_stone_transmutation_range", 2, 0, 64);
        POOP_STONE_DEBUFF_CHANCE = defineDouble(builder,
                "The chance of receiving various debuff (other than hunger) by the holder of Kakkakikkare in each tick.\n" +
                        "Set this value to 0 to disable debuff.",
                "poop_stone_debuff_chance", 0.01, 0, 1);

        EXCREMENT_DEBUFF_DURATION = defineInteger(builder,
                "The duration of debuff in ticks that the excrement fluid block may give to the creatures inside.\n" +
                        "Set this value to 0 to disable.",
                "excrement_debuff_duration", 200, 0, 1000000);


        SUN_SEED_EXPLOSION_RANGE = defineInteger(builder,
                "Auringonsiemen creates explosion at powdery blocks within this distance.\n" +
                        "Set this value to 0 to disable.",
                "sun_seed_explosion_range", 2, 0, 64);
        SUN_SEED_EXPLOSION_CHANCE = defineDouble(builder,
                "The probability of explosion at a powdery block within the explosion range of Auringonsiemen.\n" +
                        "Set this value to 0 to disable.",
                "sun_seed_explosion_chance", 0.5, 0, 1);
        SUN_SEED_EXPLOSION_INTERVAL = defineInteger(builder,
                "The interval for Auringonsiemen to make an exploding attempt.",
                "sun_seed_explosion_frequency", 2, 1, Integer.MAX_VALUE);


        SUN_STONE_FIRE_RANGE = defineInteger(builder,
                "Aurinkokivi transmutes powdery blocks within this distance into fire.\n" +
                        "Set this value to 0 to disable.",
                "sun_stone_fire_range", 2, 0, 64);
        SUN_STONE_FIRE_CHANCE = defineDouble(builder,
                "The probability of transmuting a powdery block into fire within the fire range of Aurinkokivi.\n" +
                        "Set this value to 0 to disable.",
                "sun_stone_fire_chance", 0.75, 0, 1);
        SUN_STONE_FIRE_INTERVAL = defineInteger(builder,
                "The interval for Aurinkokivi to make a transmuting attempt.",
                "sun_stone_fire_interval", 2, 1, Integer.MAX_VALUE);

        POWDERY_BLOCK = new BlockTaggedListConfigValue(defineList(builder,
                "Blocks in this list will be transmuted into explosion or fire by Auringonsiemen or Aurinkokivi.\n" +
                        "Also accept block tags starting with #",
                "powdery_blocks",
                Lists.newArrayList("#uusi-aurinko:powdery")
        ), this);

        NEW_SUN_DESTROY_BLACKLIST = new BlockTaggedListConfigValue(defineList(builder,
                "????????????",
                "new_sun_destroy_blacklist", Lists.newArrayList("#")
        ), this);
    }

    @Override
    public ForgeConfigSpec getSpec() {
        return CONFIG_SPEC;
    }
}
