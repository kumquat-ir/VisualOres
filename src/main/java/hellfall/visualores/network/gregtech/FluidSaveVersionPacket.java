package hellfall.visualores.network.gregtech;

import gregtech.api.network.IClientExecutor;
import gregtech.api.network.IPacket;
import gregtech.api.worldgen.bedrockFluids.BedrockFluidVeinHandler;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;

public class FluidSaveVersionPacket implements IPacket, IClientExecutor {
    private int saveDataVersion;

    public FluidSaveVersionPacket() {}

    public FluidSaveVersionPacket(int saveDataVersion) {
        this.saveDataVersion = saveDataVersion;
    }

    @Override
    public void executeClient(NetHandlerPlayClient netHandlerPlayClient) {
        BedrockFluidVeinHandler.saveDataVersion = saveDataVersion;
    }

    @Override
    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeInt(saveDataVersion);
    }

    @Override
    public void decode(PacketBuffer packetBuffer) {
        saveDataVersion = packetBuffer.readInt();
    }
}
