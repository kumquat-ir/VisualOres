package hellfall.visualores.mixins.immersiveengineering;

import blusunrize.immersiveengineering.common.items.ItemCoresample;
import hellfall.visualores.database.immersiveengineering.IEClientCache;
import net.minecraft.client.particle.ParticleItemPickup;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ParticleItemPickup.class)
public class ParticleItemPickupMixin {
    // is there a better way to get when the client picks up an item on client-side?
    @Inject(method = "<init>", at = @At("TAIL"))
    private void visualores$injectConstructor(World world, Entity entityPickedUp, Entity livingEntity, float yOffset, CallbackInfo ci) {
        if (entityPickedUp instanceof EntityItem ei && ei.getItem().getItem() instanceof ItemCoresample) {
            IEClientCache.instance.readCoresampleNBT(ei.getItem());
        }
    }
}
