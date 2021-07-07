package io.github.lucunji.uusiaurinko.datagen.client;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.item.BlockItem;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.Objects;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ModBlocks.itemizedBlocks()
                .filter(pair -> pair.getRight().genItemModel())
                .forEach(pair ->
                        simpleBlockItem((BlockItem) pair.getLeft().get().asItem(), pair.getRight().parentModel())
                );
    }

    protected void simpleBlockItem(BlockItem blockItem, String parentModel) {
        String name = Objects.requireNonNull(blockItem.getRegistryName()).getPath();
        withExistingParent(
                name,   // file name
                parentModel.isEmpty() ? modLoc(("block/" + name)) : new ResourceLocation(parentModel) // parent name
        );
    }
}
