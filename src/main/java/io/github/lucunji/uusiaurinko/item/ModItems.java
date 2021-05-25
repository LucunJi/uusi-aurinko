package io.github.lucunji.uusiaurinko.item;

import io.github.lucunji.uusiaurinko.item.radiative.*;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModItems {
    private static final ItemGroup DEFAULT_GROUP = new ModItemGroupDefault(MODID + ".defaults");

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);


    public static final RegistryObject<ItemFireStone> FIRE_STONE = ITEMS.register("fire_stone",
            () -> new ItemFireStone(new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULT_GROUP)));

    public static final RegistryObject<ItemWaterStone> WATER_STONE = ITEMS.register("water_stone",
            () -> new ItemWaterStone(new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULT_GROUP)));

    public static final RegistryObject<ItemLightningStone> LIGHTNING_STONE = ITEMS.register("lightning_stone",
            () -> new ItemLightningStone(new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULT_GROUP)));

    public static final RegistryObject<ItemEarthStone> EARTH_STONE = ITEMS.register("earth_stone",
            () -> new ItemEarthStone(new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULT_GROUP)));

    public static final RegistryObject<ItemPopStone> POP_STONE = ITEMS.register("pop_stone",
            () -> new ItemPopStone(new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULT_GROUP)));

    public static final RegistryObject<ItemSunSeed> SUN_SEED = ITEMS.register("sun_seed",
            () -> new ItemSunSeed(new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULT_GROUP)));

    public static final RegistryObject<ItemSunStone> SUN_STONE = ITEMS.register("sun_stone",
            () -> new ItemSunStone(new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULT_GROUP)));

    public static final RegistryObject<ItemEvilEye> EVIL_EYE = ITEMS.register("evil_eye",
            () -> new ItemEvilEye(ModArmorMaterial.EVIL_EYE, EquipmentSlotType.HEAD, new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULT_GROUP)));

    public static final RegistryObject<ItemMoon> MOON = ITEMS.register("moon",
            () -> new ItemMoon(new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULT_GROUP)));
}
