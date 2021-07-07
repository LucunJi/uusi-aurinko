package io.github.lucunji.uusiaurinko.datagen.client;

import io.github.lucunji.uusiaurinko.block.ModBlocks;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.client.model.generators.ItemModelProvider;
import net.minecraftforge.common.data.ExistingFileHelper;

public class ModItemModelProvider extends ItemModelProvider {
    public ModItemModelProvider(DataGenerator generator, String modid, ExistingFileHelper existingFileHelper) {
        super(generator, modid, existingFileHelper);
    }

    @Override
    protected void registerModels() {
        ModBlocks.itemizedBlocks().forEach(pair -> {
            String name = pair.getLeft().getId().getPath();
            withExistingParent(name, modLoc("block/" + name));
        });
    }
}
