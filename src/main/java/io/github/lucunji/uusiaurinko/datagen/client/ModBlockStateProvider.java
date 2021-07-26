package io.github.lucunji.uusiaurinko.datagen.client;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockStateProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModBlockStateProvider extends BlockStateProvider {
    public ModBlockStateProvider(DataGenerator gen, String modid, ExistingFileHelper exFileHelper) {
        super(gen, modid, exFileHelper);
    }

    @Override
    protected void registerStatesAndModels() {
        this.wallBlock(ModBlocks.TEMPLE_STONE_WALL.get(), new ResourceLocation(MODID, "block/temple_stone"));
        this.wallBlock(ModBlocks.POLISHED_TEMPLE_STONE_WALL.get(), new ResourceLocation(MODID, "block/polished_temple_stone"));
        this.wallBlock(ModBlocks.TEMPLE_STONE_BRICK_WALL.get(), new ResourceLocation(MODID, "block/temple_stone_bricks"));
        this.wallBlock(ModBlocks.TEMPLE_BRICK_WALL.get(), new ResourceLocation(MODID, "block/temple_bricks"));
        this.wallBlock(ModBlocks.SHATTERED_TEMPLE_BRICK_WALL.get(), new ResourceLocation(MODID, "block/shattered_temple_bricks"));
    }
}
