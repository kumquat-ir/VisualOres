package hellfall.visualores.mixins.thaumcraft;

import hellfall.visualores.database.thaumcraft.TCClientCache;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;
import thaumcraft.common.world.aura.AuraChunk;

@Mixin(targets = "thaumcraft.common.lib.network.misc.PacketAuraToClient$1")
public abstract class PacketAuraToClientMixin {
    @Redirect(method = "run", at = @At(value = "NEW", target = "(Lnet/minecraft/world/chunk/Chunk;SFF)Lthaumcraft/common/world/aura/AuraChunk;"))
    private AuraChunk visualores$injectGetData(Chunk chunk, short base, float vis, float flux) {
        TCClientCache.instance.addChunk(base, vis, flux);
        return new AuraChunk(chunk, base, vis, flux);
    }
}
