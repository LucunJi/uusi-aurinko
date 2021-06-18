package io.github.lucunji.uusiaurinko.entity;

import io.github.lucunji.uusiaurinko.client.render.entity.RadiativeItemEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModEntityTypes {
    public static final List<Runnable> RENDERER_BINDERS = new ArrayList<>();

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final RegistryObject<EntityType<RadiativeItemEntity>> RADIATIVE_ITEM = register("radiative_item",
            () -> EntityType.Builder.<RadiativeItemEntity>create(RadiativeItemEntity::new, EntityClassification.MISC)
                    .size(0.25f, 0.25f).trackingRange(10).updateInterval(10).build("radiative_item"),
            manager -> new RadiativeItemEntityRenderer(manager, Minecraft.getInstance().getItemRenderer()));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(
            final String name, final Supplier<EntityType<T>> sup,
            IRenderFactory<? super T> renderFactory) {

        RegistryObject<EntityType<T>> registryObject = ENTITY_TYPES.register(name, sup);
        RENDERER_BINDERS.add(() -> RenderingRegistry.registerEntityRenderingHandler(registryObject.get(), renderFactory));
        return registryObject;
    }
}
