package io.github.lucunji.uusiaurinko.util;

import net.minecraft.util.DamageSource;

import static io.github.lucunji.uusiaurinko.UusiAurinko.MODID;

public class ModDamageSource {
    public static final DamageSource ELECTRICITY = new DamageSource(MODID + ".electricity")
            .setDamageBypassesArmor().setMagicDamage();

    public static final DamageSource SUN_BLAZE = new DamageSource(MODID + ".sun_blaze")
            .setFireDamage();

    public static final DamageSource SUN_FUSION = new DamageSource(MODID + ".sun_fusion")
            .setDamageBypassesArmor().setMagicDamage();
}
