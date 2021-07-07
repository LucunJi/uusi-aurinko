package io.github.lucunji.uusiaurinko.datagen;

import io.github.lucunji.uusiaurinko.datagen.client.ModItemModelProvider;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModDataGenerators {
    public static void onGatherData(GatherDataEvent gatherDataEvent) {
        DataGenerator dataGenerator = gatherDataEvent.getGenerator();
        ExistingFileHelper existingFileHelper = gatherDataEvent.getExistingFileHelper();

        if (gatherDataEvent.includeClient()) {
            dataGenerator.addProvider(new ModItemModelProvider(dataGenerator, MODID, existingFileHelper));
        }
    }
}
