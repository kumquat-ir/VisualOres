package hellfall.visualores.proxy;

import gregtech.api.GregTechAPI;
import hellfall.visualores.database.WorldIDSaveData;
import hellfall.visualores.network.WorldIDPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

public class VOCommonProxy implements ICommonProxy {
    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        WorldIDSaveData.init(event.getServer().getEntityWorld());
    }

    @Override
    public void entityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote) {
            if (event.getEntity() instanceof EntityPlayerMP) {
                //todo ccl packets
                GregTechAPI.networkHandler.sendTo(new WorldIDPacket(WorldIDSaveData.getWorldID()), (EntityPlayerMP) event.getEntity());
            }
//            else if (event.getEntity() instanceof EntityPlayer) {
//                VisualOres.LOGGER.info("got id local " + WorldIDSaveData.getWorldID());
//                GTClientCache.instance.init(WorldIDSaveData.getWorldID());
//                ClientCacheManager.init(WorldIDSaveData.getWorldID());
//            }
        }
    }
}
