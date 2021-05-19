package io.github.lucunji.uusiaurinko.effects;

import net.minecraft.potion.Effect;
import net.minecraft.potion.EffectType;
import net.minecraft.util.DamageSource;

import static net.minecraft.util.DamageSource.*;

/**
 * Give creatures a limited fire resistance ineffective in lava.
 * The main logic is implemented in {@link io.github.lucunji.uusiaurinko.events.LivingAttackEventHandler}
 */
public class LimitedFireResistanceEffect extends Effect {
    public static final DamageSource[] BLOCKED_SOURCES = {IN_FIRE, ON_FIRE, HOT_FLOOR};

    protected LimitedFireResistanceEffect(EffectType typeIn, int liquidColorIn) {
        super(typeIn, liquidColorIn);
    }
}
