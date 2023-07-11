package hellfall.visualores.mixins.gregtech;

import gregtech.api.gui.Widget;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import gregtech.common.terminal.app.prospector.ProspectorMode;
import gregtech.common.terminal.app.prospector.widget.WidgetProspectingMap;
import hellfall.visualores.database.ServerCache;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WidgetProspectingMap.class)
public abstract class WidgetProspectingMapMixin extends Widget {
    @Shadow @Final private ProspectorMode mode;

    public WidgetProspectingMapMixin(Position selfPosition, Size size) {
        super(selfPosition, size);
    }

    // to not have to deal with the horrible looking (and subject to change) local table here
    @Redirect(method = "detectAndSendChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk visualores$injectDASChanges(World instance, int chunkX, int chunkZ) {
        if (gui.entityPlayer instanceof EntityPlayerMP) {
            if (mode == ProspectorMode.ORE) {
                ServerCache.instance.prospectAllInChunk(gui.entityPlayer.world.provider.getDimension(), new ChunkPos(chunkX, chunkZ), (EntityPlayerMP) gui.entityPlayer);
            } else if (mode == ProspectorMode.FLUID) {
                //todo underground fluids
            }
        }
        return instance.getChunk(chunkX, chunkZ);
    }
}
