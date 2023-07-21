package hellfall.visualores.mixins.gregtech;

import gregtech.api.worldgen.config.WorldGenRegistry;
import hellfall.visualores.database.ore.VeinInfoCache;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = WorldGenRegistry.class, remap = false)
public abstract class WorldGenRegistryMixin {
    @Inject(method = "reinitializeRegisteredVeins", at = @At("TAIL"))
    private void visualores$reloadVeinInfoCache(CallbackInfo ci) {
        VeinInfoCache.init();
    }
}
