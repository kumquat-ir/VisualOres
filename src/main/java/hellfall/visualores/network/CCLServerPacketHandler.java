package hellfall.visualores.network;

import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import hellfall.visualores.Tags;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.network.NetHandlerPlayServer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class CCLServerPacketHandler implements ICustomPacketHandler.IServerPacketHandler {
    @Override
    public void handlePacket(PacketCustom packetCustom, EntityPlayerMP entityPlayerMP, INetHandlerPlayServer iNetHandlerPlayServer) {
        //noinspection SwitchStatementWithTooFewBranches
        switch (packetCustom.getType()) {
            case CCLPacketSender.PACKET_SHARE_PROSPECTION -> {
                // send it forward to its intended recipient
                if (iNetHandlerPlayServer instanceof NetHandlerPlayServer netHandlerPlayServer) {
                    String reciever = packetCustom.readString();
                    EntityPlayerMP target = netHandlerPlayServer.server.getPlayerList().getPlayerByUsername(reciever);
                    if (target != null) {
                        // can't just resend the packet, no, that would have been too easy
                        PacketCustom packet = new PacketCustom(Tags.MODID, CCLPacketSender.PACKET_SHARE_PROSPECTION);
                        packet.writeString(reciever);
                        packet.writeString(packetCustom.readString());
                        packet.writeString(packetCustom.readString());
                        packet.writeString(packetCustom.readString());
                        boolean isDimCache = packetCustom.readBoolean();
                        packet.writeBoolean(isDimCache);
                        if (isDimCache) {
                            packet.writeInt(packetCustom.readInt());
                        }
                        packet.writeBoolean(packetCustom.readBoolean());
                        packet.writeNBTTagCompound(packetCustom.readNBTTagCompound());
                        packet.sendToPlayer(target);
                    }
                }
            }
        }
    }
}
