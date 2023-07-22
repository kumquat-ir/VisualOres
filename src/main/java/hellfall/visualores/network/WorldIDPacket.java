package hellfall.visualores.network;

import gregtech.api.network.IClientExecutor;
import gregtech.api.network.IPacket;
import hellfall.visualores.VisualOres;
import hellfall.visualores.database.ClientCache;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.network.PacketBuffer;

public class WorldIDPacket implements IPacket, IClientExecutor {
    private String id;

    public WorldIDPacket() {}

    public WorldIDPacket(String id) {
        this.id = id;
    }

    @Override
    public void executeClient(NetHandlerPlayClient netHandlerPlayClient) {
//        VisualOres.LOGGER.info("got id " + id);
        ClientCache.instance.init(id);
    }

    @Override
    public void encode(PacketBuffer packetBuffer) {
        packetBuffer.writeString(id);
    }

    @Override
    public void decode(PacketBuffer packetBuffer) {
        this.id = packetBuffer.readString(Short.MAX_VALUE);
    }
}
