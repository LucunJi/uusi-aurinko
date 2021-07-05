package io.github.lucunji.uusiaurinko.util;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    public static final RegistryObject<SoundEvent> ENTITY_RADIATIVE_STONE_THROW =
            register("entity.radiative_stone.throw");
    public static final RegistryObject<SoundEvent> ENTITY_LIGHTNING_STONE_DISCHARGE =
            register("entity.lightning_rock.discharge");

    public static final RegistryObject<SoundEvent> ITEM_EVIL_EYE_EQUIP = register("item.evil_eye.equip");
    public static final RegistryObject<SoundEvent> ITEM_SUN_STONE_AMBIENT = register("item.sun_stone.ambient");

    private static RegistryObject<SoundEvent> register(String key) {
        return SOUNDS.register(key, () -> new SoundEvent(new ResourceLocation(MODID, key)));
    }
}
