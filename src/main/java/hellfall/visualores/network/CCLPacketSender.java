package hellfall.visualores.network;

import codechicken.lib.packet.PacketCustom;
import hellfall.visualores.Tags;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;

public class CCLPacketSender {
    public static final int PACKET_WORLD_ID = 1;
    public static final int PACKET_SHARE_PROSPECTION = 2;

    public static void sendWorldID(String id, EntityPlayerMP player) {
        PacketCustom packet = new PacketCustom(Tags.MODID, PACKET_WORLD_ID);
        packet.writeString(id);
        packet.sendToPlayer(player);
    }

    public static void sendSharePacketToServer(String sender, String reciever, String cachename, String key, boolean isDimCache, int dim, NBTTagCompound data, boolean first) {
        PacketCustom packet = new PacketCustom(Tags.MODID, PACKET_SHARE_PROSPECTION);
        packet.writeString(reciever);
        packet.writeString(sender);
        packet.writeString(cachename);
        packet.writeString(key);
        packet.writeBoolean(isDimCache);
        if (isDimCache) {
            packet.writeInt(dim);
        }
        packet.writeBoolean(first);
        packet.writeNBTTagCompound(data);
        packet.sendToServer();
    }
}
