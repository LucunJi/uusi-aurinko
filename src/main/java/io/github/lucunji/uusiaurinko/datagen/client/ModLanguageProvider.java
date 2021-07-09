package io.github.lucunji.uusiaurinko.datagen.client;

import com.google.gson.Gson;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.resources.ResourcePackType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;
import java.io.*;
import java.util.*;

public class ModLanguageProvider extends LanguageProvider {
    private static final Logger LOGGER = LogManager.getLogger(ModLanguageProvider.class);

    private final ExistingFileHelper existingFileHelper;
    private final ResourceLocation existingLang;

    private final List<String> keys;
    private final List<String> translations;
    private final List<RegistryObject<?>> objects;

    public ModLanguageProvider(DataGenerator gen, ExistingFileHelper existingFileHelper, String modid, String locale) {
        super(gen, modid, locale);
        this.existingFileHelper = existingFileHelper;
        this.existingLang = new ResourceLocation(modid + ":lang/" + locale + ".json");

        this.keys = new ArrayList<>();
        this.translations = new ArrayList<>();
        this.objects = new ArrayList<>();
    }

    public void addEntry(String key, String translation, RegistryObject<?> object) {
        keys.add(key);
        translations.add(translation);
        objects.add(object);
    }

    @Override
    protected void addTranslations() {
        LOGGER.debug("Trying to merge existing language file " + existingLang.toString());
        Map<String, String> existingKeys = addExisting();
        if (existingKeys == null) {
            LOGGER.info("There is no existing language file " + existingLang);
        } else {
            LOGGER.debug(existingKeys.size() + " entries loaded from " + existingLang);
        }

        for (int i = 0; i < objects.size(); i++) {
            String key = keys.get(i);
            String translation = translations.get(i);
            RegistryObject<?> obj = objects.get(i);
            IForgeRegistryEntry<?> entry = obj.get();
            try {
                if (!key.isEmpty()) {
                    add(key, translation);
                } else if (entry instanceof Block) {
                    add((Block) entry, translation);
                } else if (entry instanceof Item) {
                    add((Item) entry, translation);
                } else if (entry instanceof Enchantment) {
                    add((Enchantment) entry, translation);
                } else if (entry instanceof Effect) {
                    add((Effect) entry, translation);
                } else if (entry instanceof EntityType) {
                    add((EntityType<?>) entry, translation);
                } else if (entry instanceof SoundEvent) {
                    add("subtitles." + obj.getId().getNamespace() + "." + obj.getId().getPath(), translation);
                } else {
                    throw new RuntimeException("Unsupported registry object type '" + entry.getClass() + "'");
                }
            } catch (IllegalStateException ise) {
                if (existingKeys != null && ise.getMessage().startsWith("Duplicate translation key ")) {
                    String dupKey = ise.getMessage().substring("Duplicate translation key ".length());
                    String dupVal = existingKeys.get(dupKey);
                    if (translation.equals(dupVal)) {
                        LOGGER.warn("The generated translation for key '" + dupKey + "' is the same in the existing language file "
                                + existingLang);
                        continue;
                    } else if (dupVal != null) {
                        LOGGER.warn("The generated translation for key '" + dupKey + "' is different in the existing language file "
                                + existingLang);
                        continue;
                    }
                }
                throw ise;
            }
        }
    }

    @Nullable
    private Map<String, String> addExisting() {
        Map<String, String> ret = new HashMap<>();
        try (InputStream inputStream = existingFileHelper
                .getResource(existingLang, ResourcePackType.CLIENT_RESOURCES).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            Map<?, ?> langMap = new Gson().fromJson(reader, Map.class);
            langMap.forEach((key, val) -> {
                if (key instanceof CharSequence && val instanceof CharSequence) {
                    add(key.toString(), val.toString());
                    ret.put(key.toString(), val.toString());
                } else {
                    if (key instanceof CharSequence) {
                        LOGGER.throwing(new IllegalArgumentException("Unexpected key which is not a CharSequence."));
                    } else {
                        LOGGER.throwing(new IllegalArgumentException("Unexpected value which is not a CharSequence " +
                                "for key '" + key.toString() + "'."));
                    }
                }
            });

        } catch (FileNotFoundException nfe) {
            return null;
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }
        return ret;
    }
}
