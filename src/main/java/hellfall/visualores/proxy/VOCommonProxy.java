package hellfall.visualores.proxy;

import codechicken.lib.packet.PacketCustom;
import hellfall.visualores.Tags;
import hellfall.visualores.database.WorldIDSaveData;
import hellfall.visualores.network.CCLPacketSender;
import hellfall.visualores.network.CCLServerPacketHandler;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class VOCommonProxy implements ICommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        PacketCustom.assignHandler(Tags.MODID, new CCLServerPacketHandler());
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        WorldIDSaveData.init(event.getServer().getEntityWorld());
    }

    @Override
    public void entityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP) {
            CCLPacketSender.sendWorldID(WorldIDSaveData.getWorldID(), (EntityPlayerMP) event.getEntity());
        }
    }
}
