package hellfall.visualores.mixins.astralsorcery;

import hellfall.visualores.database.astralsorcery.ASClientCache;
import hellfirepvp.astralsorcery.common.item.tool.ItemSkyResonator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemSkyResonator.ResonatorUpgrade.class)
public abstract class ItemSkyResonatorMixin {
    @Inject(method = "playStarlightFieldEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;getPosition()Lnet/minecraft/util/math/BlockPos;"))
    private void visualores$injectStarfieldEffect(CallbackInfo ci) {
        ASClientCache.instance.addStarfields();
    }
}
