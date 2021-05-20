package io.github.lucunji.uusiaurinko.item;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.item.radiative.ItemFireStone;
import io.github.lucunji.uusiaurinko.item.radiative.ItemWaterStone;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModItems {
    private static final ItemGroup DEFAULT_GROUP = new DefaultGroup(MODID + ".defaults");

    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);


    public static final RegistryObject<ItemFireStone> FIRE_STONE = ITEMS.register("fire_stone",
            () -> new ItemFireStone(new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULT_GROUP)));

    public static final RegistryObject<ItemWaterStone> WATER_STONE = ITEMS.register("water_stone",
            () -> new ItemWaterStone(new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULT_GROUP)));

    public static final RegistryObject<Item> SOLIDIFIED_LAVA = ITEMS.register("solidified_lava",
            () -> new BlockItem(ModBlocks.SOLIDIFIED_LAVA.get(), new Properties().group(DEFAULT_GROUP)));


    public static class DefaultGroup extends ItemGroup {

        public DefaultGroup(String label) {
            super(label);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(ModItems.FIRE_STONE.get());
        }
    }
}
