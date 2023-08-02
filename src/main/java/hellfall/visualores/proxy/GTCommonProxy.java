package hellfall.visualores.proxy;

import gregtech.api.GregTechAPI;
import gregtech.api.worldgen.bedrockFluids.BedrockFluidVeinHandler;
import hellfall.visualores.database.gregtech.ore.ServerCache;
import hellfall.visualores.network.gregtech.FluidSaveVersionPacket;
import hellfall.visualores.network.gregtech.OreProspectToClientPacket;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;

public class GTCommonProxy implements ICommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        GregTechAPI.networkHandler.registerPacket(OreProspectToClientPacket.class);
        GregTechAPI.networkHandler.registerPacket(FluidSaveVersionPacket.class);
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

    @Override
    public void entityJoinWorld(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote && event.getEntity() instanceof EntityPlayerMP player) {
            GregTechAPI.networkHandler.sendTo(new FluidSaveVersionPacket(BedrockFluidVeinHandler.saveDataVersion), player);
        }
    }
}
