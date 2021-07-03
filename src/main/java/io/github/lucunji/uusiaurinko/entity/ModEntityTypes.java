package io.github.lucunji.uusiaurinko.entity;

import io.github.lucunji.uusiaurinko.client.render.entity.RadiativeItemEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityClassification;
import net.minecraft.entity.EntityType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModEntityTypes {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES = DeferredRegister.create(ForgeRegistries.ENTITIES, MODID);

    public static final RegistryObject<EntityType<RadiativeItemEntity>> RADIATIVE_ITEM = register("radiative_item",
            () -> EntityType.Builder.<RadiativeItemEntity>create(RadiativeItemEntity::new, EntityClassification.MISC)
                    .size(0.25f, 0.25f).trackingRange(10).updateInterval(10).build("radiative_item"));

    private static <T extends Entity> RegistryObject<EntityType<T>> register(
            final String name, final Supplier<EntityType<T>> sup) {

        RegistryObject<EntityType<T>> registryObject = ENTITY_TYPES.register(name, sup);
        return registryObject;
    }

    @OnlyIn(Dist.CLIENT)
    public static class ClientRenderer {
        public static final List<Runnable> RENDERER_BINDERS = new ArrayList<>();

        static {
            register(RADIATIVE_ITEM, manager -> new RadiativeItemEntityRenderer(manager, Minecraft.getInstance().getItemRenderer()));
        }

        public static <T extends Entity> void register(RegistryObject<EntityType<T>> registryObject, IRenderFactory<? super T> renderFactory) {
            RENDERER_BINDERS.add(() -> RenderingRegistry.registerEntityRenderingHandler(registryObject.get(), renderFactory));
        }
    }
}
