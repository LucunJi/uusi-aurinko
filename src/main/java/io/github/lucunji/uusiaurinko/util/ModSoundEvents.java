package io.github.lucunji.uusiaurinko.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

/**
 * Sound events are registered without {@link net.minecraftforge.registries.DeferredRegister}
 * because {@code ModItems.EVIL_EYE} uses sound effect in its armor material before sound registry.
 */
@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class ModSoundEvents {
    public static final SoundEvent ENTITY_RADIATIVE_STONE_THROW = register("entity.radiative_stone.throw");
    public static final SoundEvent ENTITY_LIGHTNING_STONE_EMIT = register("entity.lightning_rock.emit");

    public static final SoundEvent ITEM_EVIL_EYE_EQUIP = register("item.evil_eye.equip");

    private static SoundEvent register(String key) {
        return new SoundEvent(new ResourceLocation(MODID, key)).setRegistryName(MODID, key);
    }

    @SubscribeEvent
    public static void registerSoundEvents(final RegistryEvent.Register<SoundEvent> registryEvent) {
        registryEvent.getRegistry().registerAll(
                ENTITY_RADIATIVE_STONE_THROW,
                ENTITY_LIGHTNING_STONE_EMIT,
                ITEM_EVIL_EYE_EQUIP);
    }
}
