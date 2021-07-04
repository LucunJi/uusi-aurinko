package io.github.lucunji.uusiaurinko.entity;

import io.github.lucunji.uusiaurinko.client.render.entity.NewSunEntityRenderer;
import io.github.lucunji.uusiaurinko.client.render.entity.RadiativeItemEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final RegistryObject<EntityType<RadiativeItemEntity>> RADIATIVE_ITEM = ENTITY_TYPES.register("radiative_item",
            () -> EntityType.Builder.<RadiativeItemEntity>create(RadiativeItemEntity::new, EntityClassification.MISC)
                    .size(0.25f, 0.25f).trackingRange(10).updateInterval(10).build("radiative_item"));

    public static final RegistryObject<EntityType<NewSunEntity>> NEW_SUN = ENTITY_TYPES.register("new_sun",
            () -> EntityType.Builder.create(NewSunEntity::new, EntityClassification.MISC)
                    .size(10f, 10f).trackingRange(16).build("new_sun"));

    @OnlyIn(Dist.CLIENT)
    public static class ClientRenderer {
        public static final List<Runnable> RENDERER_BINDERS = new ArrayList<>();

        static {
            registerRenderer(RADIATIVE_ITEM, manager ->
                    new RadiativeItemEntityRenderer(manager, Minecraft.getInstance().getItemRenderer()));
            registerRenderer(NEW_SUN, NewSunEntityRenderer::new);
        }

        public static <T extends Entity> void registerRenderer(RegistryObject<EntityType<T>> registryObject, IRenderFactory<? super T> renderFactory) {
            RENDERER_BINDERS.add(() -> RenderingRegistry.registerEntityRenderingHandler(registryObject.get(), renderFactory));
        }
    }
}
