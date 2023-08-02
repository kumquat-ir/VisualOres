package hellfall.visualores.mixins.gregtech;

import gregtech.api.gui.Widget;
import gregtech.api.util.Position;
import gregtech.api.util.Size;
import gregtech.api.worldgen.bedrockFluids.BedrockFluidVeinHandler;
import gregtech.common.terminal.app.prospector.ProspectorMode;
import gregtech.common.terminal.app.prospector.widget.WidgetProspectingMap;
import gregtech.core.network.packets.PacketProspecting;
import hellfall.visualores.database.gregtech.GTClientCache;
import hellfall.visualores.database.gregtech.ore.ServerCache;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.World;
import net.minecraft.world.chunk.Chunk;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(WidgetProspectingMap.class)
public abstract class WidgetProspectingMapMixin extends Widget {
    @Shadow(remap = false) @Final private ProspectorMode mode;

    public WidgetProspectingMapMixin(Position selfPosition, Size size) {
        super(selfPosition, size);
    }

    // to not have to deal with the horrible looking (and subject to change) local table here
    @Redirect(method = "detectAndSendChanges", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/World;getChunk(II)Lnet/minecraft/world/chunk/Chunk;"))
    private Chunk visualores$injectDASChanges(World world, int chunkX, int chunkZ) {
        if (gui.entityPlayer instanceof EntityPlayerMP) {
            if (mode == ProspectorMode.ORE) {
                ServerCache.instance.prospectAllInChunk(world.provider.getDimension(), new ChunkPos(chunkX, chunkZ), (EntityPlayerMP) gui.entityPlayer);
            }
        }
        return world.getChunk(chunkX, chunkZ);
    }

    // handle fluids on client side, because we can
    @Inject(method = "readUpdateInfo", at = @At(value = "INVOKE",
            target = "Lgregtech/common/terminal/app/prospector/widget/WidgetProspectingMap;addPacketToQueue(Lgregtech/core/network/packets/PacketProspecting;)V"
        ), locals = LocalCapture.CAPTURE_FAILSOFT, remap = false
    )
    private void visualores$injectReadFluidPacket(int id, PacketBuffer buffer, CallbackInfo ci, PacketProspecting packet) {
        if (packet.mode == ProspectorMode.FLUID) {
            int fieldX = BedrockFluidVeinHandler.getVeinCoord(packet.chunkX);
            int fieldZ = BedrockFluidVeinHandler.getVeinCoord(packet.chunkZ);
            GTClientCache.instance.addFluid(gui.entityPlayer.getEntityWorld().provider.getDimension(), fieldX, fieldZ,
                    packet.map[0][0].get((byte) 1), Integer.parseInt(packet.map[0][0].get(((byte) 2))), Double.parseDouble(packet.map[0][0].get(((byte) 3))));
        }
    }
}
