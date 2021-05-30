package io.github.lucunji.uusiaurinko.mixins;

import io.github.lucunji.uusiaurinko.effects.ModEffects;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Entity.class)
public class EntityMixinClientOnly {

    /**
     * The player with true vision effect can see invisible entities.
     */
    @Inject(method = "isInvisibleToPlayer", at = @At("RETURN"), cancellable = true)
    private void onIsInvisibleToPlayerInvoked(PlayerEntity player, CallbackInfoReturnable<Boolean> cir) {
        if (player.isPotionActive(ModEffects.TRUE_VISION.get()))
            cir.setReturnValue(false);
    }
}
