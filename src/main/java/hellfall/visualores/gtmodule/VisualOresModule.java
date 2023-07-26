package hellfall.visualores.gtmodule;

import gregtech.api.GTValues;
import gregtech.api.GregTechAPI;
import gregtech.api.modules.GregTechModule;
import gregtech.api.modules.IGregTechModule;
import hellfall.visualores.Tags;
import hellfall.visualores.VisualOres;
import hellfall.visualores.database.ClientCacheManager;
import hellfall.visualores.database.gregtech.GTClientCache;
import hellfall.visualores.database.gregtech.fluid.UndergroundFluidPosition;
import hellfall.visualores.database.gregtech.ore.ServerCache;
import hellfall.visualores.map.layers.Layers;
import hellfall.visualores.map.layers.gregtech.OreRenderLayer;
import hellfall.visualores.map.layers.gregtech.UndergroundFluidRenderLayer;
import hellfall.visualores.network.WorldIDPacket;
import hellfall.visualores.network.gregtech.OreProspectToClientPacket;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

@GregTechModule(moduleID = VisualOresModuleContainer.VO_MODULE, containerID = Tags.MODID, name = "VisualOres module", coreModule = true)
public class VisualOresModule implements IGregTechModule {
    @NotNull
    @Override
    public List<Class<?>> getEventBusSubscribers() {
        return Collections.singletonList(VisualOresModule.class);
    }

    @NotNull
    @Override
    public Logger getLogger() {
        return VisualOres.LOGGER;
    }

    @Override
    public void registerPackets() {
        GregTechAPI.networkHandler.registerPacket(WorldIDPacket.class);
        GregTechAPI.networkHandler.registerPacket(OreProspectToClientPacket.class);
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        if (GTValues.isClientSide()) {
            Layers.registerLayer(OreRenderLayer.class, "oreveins");
            Layers.registerLayer(UndergroundFluidRenderLayer.class, "undergroundfluid");

            ClientCacheManager.registerClientCache(GTClientCache.instance, "gregtech");
            UndergroundFluidPosition.initColorOverrides();
        }
    }

    @Override
    public void serverStopped(FMLServerStoppedEvent event) {
        ServerCache.instance.clear();
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (!event.getWorld().isRemote) {
            ServerCache.instance.maybeInitWorld(event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (!event.getWorld().isRemote) {
            ServerCache.instance.invalidateWorld(event.getWorld());
        }
    }
}
