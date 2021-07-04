package io.github.lucunji.uusiaurinko.config.loadlistening;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class BlockTaggedListConfigValue extends TaggedListConfigValue<Block> {
    private static final Logger LOGGER = LogManager.getLogger(BlockTaggedListConfigValue.class);

    public BlockTaggedListConfigValue(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue, LoadListeningConfigManagerAbstract manager) {
        super(configValue, manager);
    }

    @Override
    protected Block string2SingletonValue(String raw) {
        ResourceLocation resourceLocation = new ResourceLocation(raw);
        IForgeRegistry<Block> blocks = ForgeRegistries.BLOCKS;
        ResourceLocation defaultKey = blocks.getDefaultKey();
        Block block = blocks.getValue(resourceLocation);
        if (block == null ||
                !resourceLocation.equals(defaultKey) && block == blocks.getValue(defaultKey)) {
            LOGGER.error("Could not interpret the block '" + resourceLocation + "'");
            return null;
        }
        return block;
    }

    @Override
    public boolean contains(Block val) {
        return getSingletons().contains(val) ||
                getTags().stream()
                        .map(BlockTags.getCollection()::getTagByID)
                        .anyMatch(tag -> tag.contains(val));
    }

    public boolean contains(BlockState val) {
        return contains(val.getBlock());
    }
}
