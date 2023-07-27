package hellfall.visualores.network;

import codechicken.lib.packet.ICustomPacketHandler;
import codechicken.lib.packet.PacketCustom;
import hellfall.visualores.database.ClientCacheManager;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.util.text.TextComponentTranslation;

public class CCLClientPacketHandler implements ICustomPacketHandler.IClientPacketHandler {
    @Override
    public void handlePacket(PacketCustom packetCustom, Minecraft minecraft, INetHandlerPlayClient iNetHandlerPlayClient) {
        switch (packetCustom.getType()) {
            case CCLPacketSender.PACKET_WORLD_ID -> ClientCacheManager.init(packetCustom.readString());
            case CCLPacketSender.PACKET_SHARE_PROSPECTION -> {
                packetCustom.readString(); // reciever's name, ignore
                String sender = packetCustom.readString();
                String cacheName = packetCustom.readString();
                String key = packetCustom.readString();
                boolean isDimCache = packetCustom.readBoolean();
                int cacheDim = 0;
                if (isDimCache) {
                    cacheDim = packetCustom.readInt();
                }
                boolean first = packetCustom.readBoolean();
                NBTTagCompound data = packetCustom.readNBTTagCompound();

                if (first) {
                    minecraft.ingameGUI.getChatGUI().printChatMessage(new TextComponentTranslation("visualores.share.notification", sender));
                }

                ClientCacheManager.processProspectionShare(cacheName, key, isDimCache, cacheDim, data);
            }
        }
    }
}
