package io.github.lucunji.uusiaurinko.effects;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModEffects {
    public static final DeferredRegister<Effect> EFFECTS = DeferredRegister.create(ForgeRegistries.POTIONS, MODID);

    public static final RegistryObject<Effect> TRUE_VISION = EFFECTS.register("true_vision", () ->
            new BasicModEffect(EffectType.BENEFICIAL, 0x5d4145));
}
