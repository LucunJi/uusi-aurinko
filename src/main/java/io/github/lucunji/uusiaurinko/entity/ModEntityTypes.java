package io.github.lucunji.uusiaurinko.entity;

import io.github.lucunji.uusiaurinko.client.render.entity.NewSunEntityRenderer;
import io.github.lucunji.uusiaurinko.client.render.entity.RadiativeItemEntityRenderer;
import io.github.lucunji.uusiaurinko.datagen.client.Localize;
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

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    @Localize(locales = {"en_us", "zh_cn"}, translations = {"Radiative Magical Stone", "放射性魔法石"})
    public static final RegistryObject<EntityType<RadiativeItemEntity>> RADIATIVE_ITEM = ENTITY_TYPES.register("radiative_item",
            () -> EntityType.Builder.<RadiativeItemEntity>create(RadiativeItemEntity::new, EntityClassification.MISC)
                    .size(0.25f, 0.25f).trackingRange(10).updateInterval(10).build("radiative_item"));

    @Localize(locales = {"en_us", "zh_cn"}, translations = {"§4§lNew Sun§r", "§4§l新日§r"})
    public static final RegistryObject<EntityType<NewSunEntity>> NEW_SUN = ENTITY_TYPES.register("new_sun",
            () -> EntityType.Builder.create(NewSunEntity::new, EntityClassification.MISC)
                    .size(0.25f, 0.25f).immuneToFire().trackingRange(16).build("new_sun"));

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
