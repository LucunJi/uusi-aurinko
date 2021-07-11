package io.github.lucunji.uusiaurinko.config.loadlistening;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class ItemTaggedListConfigValue extends TaggedListConfigValue<Item> {
    private static final Logger LOGGER = LogManager.getLogger(ItemTaggedListConfigValue.class);

    public ItemTaggedListConfigValue(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue, LoadListeningConfigManagerAbstract manager) {
        super(configValue, manager);
    }

    @Override
    protected Item string2SingletonValue(String raw) {
        ResourceLocation resourceLocation = new ResourceLocation(raw);
        IForgeRegistry<Item> items = ForgeRegistries.ITEMS;
        ResourceLocation defaultKey = items.getDefaultKey();
        Item item = items.getValue(resourceLocation);
        if (item == null ||
                !resourceLocation.equals(defaultKey) && item == items.getValue(defaultKey)) {
            LOGGER.error("Could not interpret the item '" + resourceLocation + "'");
            return null;
        }
        return item;
    }

    @Override
    public boolean contains(Item val) {
        return getSingletons().contains(val) ||
                getTags().stream()
                        .map(ItemTags.getCollection()::getTagByID)
                        .anyMatch(tag -> tag.contains(val));
    }

    public boolean contains(ItemStack val) {
        return contains(val.getItem());
    }
}
