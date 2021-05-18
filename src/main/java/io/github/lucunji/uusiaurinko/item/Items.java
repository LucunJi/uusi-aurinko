package io.github.lucunji.uusiaurinko.item;

import io.github.lucunji.uusiaurinko.item.radiative.ItemFireStone;
import net.minecraft.item.Item;
import net.minecraft.item.Item.Properties;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ObjectHolder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@ObjectHolder(MODID)
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class Items {
    private static final Logger LOGGER = LogManager.getLogger();

    private static final ItemGroup DEFAULTS = new DefaultGroup(MODID+".defaults");

    @ObjectHolder("fire_stone")
    public static final ItemFireStone FIRE_STONE = null;

    @SubscribeEvent
    public static void onItemsRegistry(final RegistryEvent.Register<Item> itemRegistryEvent) {
        LOGGER.info("Register items");
        itemRegistryEvent.getRegistry().registerAll(
                new ItemFireStone(new Properties().isImmuneToFire().maxStackSize(1).group(DEFAULTS))
                        .setRegistryName(MODID, "fire_stone")
        );
    }

    public static class DefaultGroup extends ItemGroup {

        public DefaultGroup(String label) {
            super(label);
        }

        @Override
        public ItemStack createIcon() {
            return new ItemStack(FIRE_STONE);
        }
    }
}
