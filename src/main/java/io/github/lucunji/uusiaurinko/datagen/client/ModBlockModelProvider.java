package io.github.lucunji.uusiaurinko.datagen.client;

import net.minecraft.data.DataGenerator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.BlockModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModBlockModelProvider extends BlockModelProvider {
    public ModBlockModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        this.wallInventory("temple_stone_wall_inventory", new ResourceLocation(MODID, "block/temple_stone"));
        this.wallInventory("polished_temple_stone_wall_inventory", new ResourceLocation(MODID, "block/polished_temple_stone"));
        this.wallInventory("temple_stone_brick_wall_inventory", new ResourceLocation(MODID, "block/temple_stone_bricks"));
        this.wallInventory("temple_brick_wall_inventory", new ResourceLocation(MODID, "block/temple_bricks"));
        this.wallInventory("shattered_temple_brick_wall_inventory", new ResourceLocation(MODID, "block/shattered_temple_bricks"));
    }
}
