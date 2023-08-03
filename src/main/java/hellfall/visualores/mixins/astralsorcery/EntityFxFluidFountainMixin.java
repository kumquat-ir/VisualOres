package hellfall.visualores.mixins.astralsorcery;

import hellfall.visualores.database.astralsorcery.ASClientCache;
import hellfirepvp.astralsorcery.client.effect.fx.EntityFXFluidFountain;
import hellfirepvp.astralsorcery.common.util.data.Vector3;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fluids.FluidStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EntityFXFluidFountain.class, remap = false)
public abstract class EntityFxFluidFountainMixin {
    @Inject(method = "spawnAt", at = @At("TAIL"))
    private static void visualores$injectSpawn(Vector3 pos, FluidStack fluid, CallbackInfoReturnable<EntityFXFluidFountain> cir) {
        ASClientCache.instance.setNeromanticFluid(Minecraft.getMinecraft().world.provider.getDimension(), new ChunkPos(pos.toBlockPos()), fluid.getFluid());
    }
}
