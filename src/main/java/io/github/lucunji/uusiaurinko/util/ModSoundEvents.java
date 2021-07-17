package io.github.lucunji.uusiaurinko.util;

import io.github.lucunji.uusiaurinko.datagen.client.Localize;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModSoundEvents {
    public static final DeferredRegister<SoundEvent> SOUNDS = DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, MODID);

    @Localize(locales = {"en_us", "zh_cn"}, translations = {"Magical Stone fies", "魔法石：飞出"})
    public static final RegistryObject<SoundEvent> ENTITY_RADIATIVE_STONE_THROW = register("entity.radiative_stone.throw");
    @Localize(locales = {"en_us", "zh_cn"}, translations = {"§oUkkoskivi§r discharges", "§oUkkoskivi§r：放电"})
    public static final RegistryObject<SoundEvent> ENTITY_LIGHTNING_STONE_DISCHARGE = register("entity.lightning_rock.discharge");

    @Localize(locales = {"en_us", "zh_cn"}, translations = {"§oPaha Silmä§r attaches", "§oPaha Silmä§r：佩戴"})
    public static final RegistryObject<SoundEvent> ITEM_EVIL_EYE_EQUIP = register("item.evil_eye.equip");
    @Localize(locales = {"en_us", "zh_cn"}, translations = {"§oAurinkokivi§r hums", "§oAurinkokivi§r：嗡嗡作响"})
    public static final RegistryObject<SoundEvent> ITEM_SUN_STONE_AMBIENT = register("item.sun_stone.ambient");

    private static RegistryObject<SoundEvent> register(String key) {
        return SOUNDS.register(key, () -> new SoundEvent(new ResourceLocation(MODID, key)));
    }
}
