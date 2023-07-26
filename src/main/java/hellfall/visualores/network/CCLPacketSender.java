package hellfall.visualores.network;

import codechicken.lib.packet.PacketCustom;
import hellfall.visualores.Tags;
import net.minecraft.entity.player.EntityPlayerMP;

public class CCLPacketSender {
    public static final int PACKET_WORLD_ID = 1;

    public static void sendWorldID(String id, EntityPlayerMP player) {
        PacketCustom packet = new PacketCustom(Tags.MODID, PACKET_WORLD_ID);
        packet.writeString(id);
        packet.sendToPlayer(player);
    }
}
