package io.github.lucunji.uusiaurinko.config.loadlistening;

import net.minecraft.entity.EntityType;
import net.minecraft.tags.EntityTypeTags;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

public class EntityTypeTaggedListConfigValue extends TaggedListConfigValue<EntityType<?>> {
    private static final Logger LOGGER = LogManager.getLogger(EntityTypeTaggedListConfigValue.class);

    public EntityTypeTaggedListConfigValue(ForgeConfigSpec.ConfigValue<List<? extends String>> configValue, LoadListeningConfigManagerAbstract manager) {
        super(configValue, manager);
    }

    @Override
    protected EntityType<?> string2SingletonValue(String raw) {
        ResourceLocation resourceLocation = new ResourceLocation(raw);
        IForgeRegistry<EntityType<?>> entities = ForgeRegistries.ENTITIES;
        ResourceLocation defaultKey = entities.getDefaultKey();
        EntityType<?> entityType = entities.getValue(resourceLocation);
        if (entityType == null ||
                !resourceLocation.equals(defaultKey) && entityType == entities.getValue(defaultKey)) {
            LOGGER.error("Could not interpret the entity type '" + resourceLocation + "'");
            return null;
        }
        return entityType;
    }

    @Override
    public boolean contains(EntityType<?> val) {
        return getSingletons().contains(val) ||
                getTags().stream()
                        .map(EntityTypeTags.getCollection()::getTagByID)
                        .anyMatch(tag -> tag.contains(val));
    }
}
