package io.github.lucunji.uusiaurinko.datagen;

import com.google.common.collect.Lists;
import io.github.lucunji.uusiaurinko.block.ModBlocks;
import io.github.lucunji.uusiaurinko.datagen.client.Localize;
import io.github.lucunji.uusiaurinko.datagen.client.ModItemModelProvider;
import io.github.lucunji.uusiaurinko.datagen.client.ModLanguageProvider;
import io.github.lucunji.uusiaurinko.effects.ModEffects;
import io.github.lucunji.uusiaurinko.entity.ModEntityTypes;
import io.github.lucunji.uusiaurinko.item.ModItems;
import io.github.lucunji.uusiaurinko.util.ModSoundEvents;
import net.minecraft.data.DataGenerator;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.GatherDataEvent;

import java.lang.reflect.Modifier;
import java.util.*;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModDataGenerators {
    @SubscribeEvent
    public static void onGatherData(GatherDataEvent gatherDataEvent) {
        DataGenerator dataGenerator = gatherDataEvent.getGenerator();
        ExistingFileHelper existingFileHelper = gatherDataEvent.getExistingFileHelper();

        if (gatherDataEvent.includeClient()) {
            dataGenerator.addProvider(new ModItemModelProvider(dataGenerator, MODID, existingFileHelper));
            makeLanguageProviders(dataGenerator, MODID,
                    ModBlocks.class, ModItems.class, ModEntityTypes.class, ModEffects.class, ModSoundEvents.class
            ).forEach(dataGenerator::addProvider);
        }
    }

    private static Collection<? extends LanguageProvider> makeLanguageProviders(DataGenerator gen, String modid,
                                                                                Class<?>... searchTargets) {
        Map<String, ModLanguageProvider> locale2Provider = new HashMap<>();
        Arrays.stream(searchTargets)
                .flatMap(aClass -> Arrays.stream(aClass.getDeclaredFields()))
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .filter(field -> field.isAnnotationPresent(Localize.class))
                .forEach(field -> {
                    final RegistryObject<?> registryObject;
                    try {
                        registryObject = (RegistryObject<?>) field.get(null);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException(e);
                    }
                    Localize annotation = field.getAnnotation(Localize.class);

                    List<String> locales = Lists.newArrayList(annotation.locales());
                    List<String> translations = Lists.newArrayList(annotation.translations());
                    if (annotation.autoMakeEnUs() && !locales.contains("en_us")) {
                        locales.add(0, "en_us");
                        translations.add(0, lowerSnake2SpaceCaseFirstCap(registryObject.getId().getPath()));
                    }
                    if (translations.size() != locales.size()) {
                        throw new IllegalArgumentException("The number of translations does not match the number of locales.");
                    }
                    for (int i = 0; i < locales.size(); i++) {
                        String locale = locales.get(i);
                        if (!locale2Provider.containsKey(locale)) {
                            locale2Provider.put(locale, new ModLanguageProvider(gen, modid, locale));
                        }
                        locale2Provider.get(locale).addTranslation(annotation.key(), translations.get(i), registryObject);
                    }
                });
        return locale2Provider.values();
    }

    private static String lowerSnake2SpaceCaseFirstCap(String str) {
        char[] chars = str.toCharArray();
        for (int i = 0; i < chars.length; i++) {
            char c = chars[i];
            if (c == '_') chars[i] = ' ';
            if ((i == 0 || chars[i - 1] == ' ') && c >= 'a' && c <= 'z') chars[i] -= 32;
        }
        return String.valueOf(chars);
    }
}
