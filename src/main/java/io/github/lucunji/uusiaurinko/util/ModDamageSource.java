package io.github.lucunji.uusiaurinko.util;

import net.minecraft.util.DamageSource;

public class ModDamageSource {
    public static final DamageSource ELECTRICITY = new DamageSource("electricity").setDamageBypassesArmor().setMagicDamage();
}
