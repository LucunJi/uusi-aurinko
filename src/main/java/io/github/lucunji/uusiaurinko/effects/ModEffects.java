package io.github.lucunji.uusiaurinko.effects;

import io.github.lucunji.uusiaurinko.datagen.client.Localize;
import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModEffects {
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);

    @Localize(locales = "zh_cn", translations = "真实之视")
    public static final RegistryObject<Effect> TRUE_VISION = EFFECTS.register("true_vision", () ->
            new BasicModEffect(EffectType.BENEFICIAL, 0x5d4145));

    @Localize(locales = {"en_us", "zh_cn"}, translations = {"Electricity Resis.", "雷电免疫"})
    public static final RegistryObject<Effect> ELECTRICITY_RESISTANCE = EFFECTS.register("electricity_resistance", () ->
            new BasicModEffect(EffectType.BENEFICIAL, 0x7bffff));
}
