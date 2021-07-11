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
        blockItems();
    }

    private void blockItems() {
        ModBlocks.itemizedBlocks()
                .filter(pair -> pair.getRight().genItemModel())
                .forEach(pair ->
                        inheritFromBlock((BlockItem) pair.getLeft().get().asItem(), pair.getRight().parentModel())
                );
    }

    /**
     * Inherit from a block model with the same name as the item's registry name
     * if {@code parentModel} is empty. Otherwise, use {@code parentModel} as the
     * parent's name. Thus <b>inheriting from a non-block parent is also possible</b>.
     */
    private void inheritFromBlock(BlockItem blockItem, String parentModel) {
        String name = Objects.requireNonNull(blockItem.getRegistryName()).getPath();
        withExistingParent(
                name,   // file name
                parentModel.isEmpty() ? modLoc(("block/" + name)) : new ResourceLocation(parentModel) // parent name
        );
    }
}
