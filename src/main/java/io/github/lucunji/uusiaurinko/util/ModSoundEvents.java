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

    @Localize(locales = {"en_us", "zh_cn"}, translations = {"Emerald tablet thrown", "翠玉石板：投掷"})
    public static final RegistryObject<SoundEvent> ENTITY_EMERALD_TABLET_THROW = register("entity.emerald_tablet.throw");
    @Localize(locales = {"en_us", "zh_cn"}, translations = {"Emerald tablet lands", "翠玉石板：落地"})
    public static final RegistryObject<SoundEvent> ENTITY_EMERALD_TABLET_LAND = register("entity.emerald_tablet.land");
    @Localize(locales = {"en_us", "zh_cn"}, translations = {"Emerald tablet hits", "翠玉石板：击中"})
    public static final RegistryObject<SoundEvent> ENTITY_EMERALD_TABLET_HIT = register("entity.emerald_tablet.hit");

    @Localize(locales = {"en_us", "zh_cn"}, translations = {"Magical Stone fies", "魔法石：飞出"})
    public static final RegistryObject<SoundEvent> ENTITY_RADIATIVE_STONE_THROW = register("entity.radiative_stone.throw");
    @Localize(locales = {"en_us", "zh_cn"}, translations = {"Ukkoskivi discharges", "Ukkoskivi：放电"})
    public static final RegistryObject<SoundEvent> ENTITY_LIGHTNING_STONE_DISCHARGE = register("entity.lightning_rock.discharge");

    @Localize(locales = {"en_us", "zh_cn"}, translations = {"Paha Silmä attaches", "Paha Silmä：佩戴"})
    public static final RegistryObject<SoundEvent> ITEM_EVIL_EYE_EQUIP = register("item.evil_eye.equip");
    @Localize(locales = {"en_us", "zh_cn"}, translations = {"Aurinkokivi hums", "Aurinkokivi：嗡嗡作响"})
    public static final RegistryObject<SoundEvent> ITEM_SUN_STONE_AMBIENT = register("item.sun_stone.ambient");

    private static RegistryObject<SoundEvent> register(String key) {
        return SOUNDS.register(key, () -> new SoundEvent(new ResourceLocation(MODID, key)));
    }
}
