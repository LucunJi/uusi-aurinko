package io.github.lucunji.uusiaurinko.entity;

import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final RegistryObject<EntityType<RadiativeItemEntity>> RADIATIVE_ITEM = ENTITY_TYPES.register("radiative_item", () ->
            EntityType.Builder.<RadiativeItemEntity>create(RadiativeItemEntity::new, EntityClassification.MISC)
                    .size(0.25f, 0.25f).trackingRange(10).updateInterval(10).build("radiative_item"));
}
