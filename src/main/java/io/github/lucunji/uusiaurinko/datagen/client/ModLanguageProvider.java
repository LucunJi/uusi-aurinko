package io.github.lucunji.uusiaurinko.datagen.client;

import net.minecraft.block.Block;
import net.minecraft.data.DataGenerator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.EntityType;
import net.minecraft.item.Item;
import net.minecraft.potion.Effect;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.ArrayList;
import java.util.List;

public class ModLanguageProvider extends LanguageProvider {

    private final List<String> keys;
    private final List<String> translations;
    private final List<RegistryObject<?>> objects;

    public ModLanguageProvider(DataGenerator gen, String modid, String locale) {
        super(gen, modid, locale);
        this.keys = new ArrayList<>();
        this.translations = new ArrayList<>();
        this.objects = new ArrayList<>();
    }

    public void addTranslation(String key, String translation, RegistryObject<?> object) {
        keys.add(key);
        translations.add(translation);
        objects.add(object);
    }

    @Override
    protected void addTranslations() {
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
}
