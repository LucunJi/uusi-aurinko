package io.github.lucunji.uusiaurinko.datagen.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DirectoryCache;
import net.minecraft.data.IDataProvider;
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
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ModLanguageProvider extends LanguageProvider {
    private static final Logger LOGGER = LogManager.getLogger(ModLanguageProvider.class);

    private final ExistingFileHelper existingFileHelper;
    private final ResourceLocation existingLang;

    private final Map<String, String> existingKeys;
    private final List<String> keys;
    private final List<String> translations;
    private final List<RegistryObject<?>> objects;

    public ModLanguageProvider(DataGenerator gen, ExistingFileHelper existingFileHelper, String modid, String locale) {
        super(gen, modid, locale);
        this.existingFileHelper = existingFileHelper;
        this.existingLang = new ResourceLocation(modid + ":lang/" + locale + ".json");

        this.existingKeys = new HashMap<>();
        this.keys = new ArrayList<>();
        this.translations = new ArrayList<>();
        this.objects = new ArrayList<>();

        this.gen = gen;
        this.modid = modid;
        this.locale = locale;
    }

    public void addEntry(String key, String translation, RegistryObject<?> object) {
        keys.add(key);
        translations.add(translation);
        objects.add(object);
    }

    @Override
    protected void addTranslations() {
        addExisting();

        for (int i = 0; i < objects.size(); i++) {
            String key = keys.get(i);
            String translation = translations.get(i);
            RegistryObject<?> obj = objects.get(i);
            IForgeRegistryEntry<?> entry = obj.get();

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
        }
    }

    @Nullable
    private void addExisting() {
        LOGGER.debug("Trying to load existing language file " + existingLang.toString());
        try (InputStream inputStream = existingFileHelper
                .getResource(existingLang, ResourcePackType.CLIENT_RESOURCES).getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            Map<?, ?> langMap = new Gson().fromJson(reader, Map.class);
            langMap.forEach((key, val) -> {
                add(key.toString(), val.toString());
                existingKeys.put(key.toString(), val.toString());
            });

        } catch (FileNotFoundException ignored) {
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        if (existingKeys.isEmpty()) {
            LOGGER.info("There is no existing language file " + existingLang);
        } else {
            LOGGER.debug(existingKeys.size() + " entries loaded from " + existingLang);
        }
    }


    /* -------------------- Code Borrowed From LanguageProvider To Remove Escaping -------------------- */
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator gen;
    private final String modid;
    private final String locale;
    private final Map<String, String> data = new TreeMap<>();

    @Override
    public void act(DirectoryCache cache) throws IOException {
        addTranslations();
        if (!data.isEmpty())
            save(cache, data, this.gen.getOutputFolder().resolve("assets/" + modid + "/lang/" + locale + ".json"));
    }

    private void save(DirectoryCache cache, Object object, Path target) throws IOException {
        String data = GSON.toJson(object);
        //noinspection UnstableApiUsage
        String hash = IDataProvider.HASH_FUNCTION.hashUnencodedChars(data).toString();
        if (!Objects.equals(cache.getPreviousHash(target), hash) || !Files.exists(target)) {
            Files.createDirectories(target.getParent());

            try (BufferedWriter bufferedwriter = Files.newBufferedWriter(target)) {
                bufferedwriter.write(data);
            }
        }

        cache.recordHash(target, hash);
    }

    @Override
    public void add(String key, String value) {
        String oldVal = data.put(key, value);
        if (oldVal != null) {
            if (value.equals(oldVal)) {
                LOGGER.warn("The generated translation for key '" + key
                        + "' is the same in the existing language file " + existingLang);
            } else {
                LOGGER.warn("The generated translation for key '" + key
                        + "' is different in the existing language file " + existingLang);
            }
            throw new IllegalStateException("Duplicate translation key " + key);
        }
    }
}
