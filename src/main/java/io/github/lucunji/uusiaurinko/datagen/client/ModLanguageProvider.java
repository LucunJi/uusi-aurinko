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
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class ModLanguageProvider extends LanguageProvider {
    private static final Logger LOGGER = LogManager.getLogger(ModLanguageProvider.class);

    private final ResourceLocation existingLang;

    private final List<String> keys;
    private final List<String> translations;
    private final List<RegistryObject<?>> objects;

    public ModLanguageProvider(DataGenerator gen, ExistingFileHelper existingFileHelper, String modid, String locale) {
        super(gen, modid, locale);
        this.existingLang = new ResourceLocation(modid + ":lang/" + locale + ".json");

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
        for (Pair<String, String> entry : HardcodedLanguageEntries.valueOf(this.locale.toUpperCase()).entries) {
            add(entry.getLeft(), entry.getRight());
        }

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


    /* -------------------- Code Borrowed From LanguageProvider To Remove Escaping -------------------- */
    private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();
    private final DataGenerator gen;
    private final String modid;
    private final String locale;
    private final Map<String, String> data = new TreeMap<>();

    /**
     * Copied from {@link LanguageProvider#act} just to direct the method call to
     * {@link ModLanguageProvider#save} instead of the original private method {@link LanguageProvider#save}
     */
    @SuppressWarnings("JavadocReference")
    @Override
    public void act(DirectoryCache cache) throws IOException {
        addTranslations();
        if (!data.isEmpty())
            save(cache, data, this.gen.getOutputFolder().resolve("assets/" + modid + "/lang/" + locale + ".json"));
    }

    /**
     * Copied from {@link LanguageProvider#save} but removed the line
     * <p>
     * {@code         data = JavaUnicodeEscaper.outsideOf(0, 0x7f).translate(data); // Escape unicode after the fact so that it's not double escaped by GSON}
     * <p>
     * to avoid escaping formatting and Chinese characters.
     */
    @SuppressWarnings("JavadocReference")
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
        if (oldVal != null)
            throw new IllegalStateException("Duplicate translation key " + key);
    }
}
