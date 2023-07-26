package hellfall.visualores.network;

import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import hellfall.visualores.database.ClientCacheManager;
import net.minecraft.client.Minecraft;
import net.minecraft.network.play.INetHandlerPlayClient;

public class CCLClientPacketHandler implements ICustomPacketHandler.IClientPacketHandler {
    @Override
    public void handlePacket(PacketCustom packetCustom, Minecraft minecraft, INetHandlerPlayClient iNetHandlerPlayClient) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (packetCustom.getType()) {
            case CCLPacketSender.PACKET_WORLD_ID -> ClientCacheManager.init(packetCustom.readString());
        }
    }
}
