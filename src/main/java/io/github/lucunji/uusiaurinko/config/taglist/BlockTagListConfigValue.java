package io.github.lucunji.uusiaurinko.config.taglist;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.stream.Stream;

public class BlockTagListConfigValue extends TagListConfigValue<Block> {
    private static Logger LOGGER = LogManager.getLogger(BlockTagListConfigValue.class);

    public BlockTagListConfigValue(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue, TagListConfigManagerAbstract manager) {
        super(configValue, manager);
    }

    @Override
    protected Stream<Block> flatMapString2StreamAndValidate(String raw) {
        if (raw.startsWith("#")) {
            List<Block> list = BlockTags.getCollection().getTagByID(new ResourceLocation(raw.substring(1))).getAllElements();
            if (list.isEmpty()) {
                LOGGER.warn("could not interpret the block tag '" + raw + "'");
                return Stream.empty();
            }
            return list.stream();
        } else {
            Block block = ForgeRegistries.BLOCKS.getValue(new ResourceLocation(raw));
            if (!raw.equals("minecraft:air") && (block == null || block == Blocks.AIR)) {
                LOGGER.warn("could not interpret the block '" + raw + "'");
                return Stream.empty();
            }
            return Stream.of(block);
        }
    }
}
