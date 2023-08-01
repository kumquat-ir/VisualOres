package hellfall.visualores.proxy;

import gregtech.api.GregTechAPI;
import hellfall.visualores.database.gregtech.ore.ServerCache;
import hellfall.visualores.network.gregtech.OreProspectToClientPacket;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

public class GTCommonProxy implements ICommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GregTechAPI.networkHandler.registerPacket(OreProspectToClientPacket.class);
    }

    @Override
    public void serverStopped(FMLServerStoppedEvent event) {
        ServerCache.instance.clear();
    }

    @Override
    public void worldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            ServerCache.instance.maybeInitWorld(event.getWorld());
        }
    }

    @Override
    public void worldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            ServerCache.instance.invalidateWorld(event.getWorld());
        }
    }
}
