package hellfall.visualores.module;

import gregtech.api.GregTechAPI;
import gregtech.api.modules.GregTechModule;
import gregtech.api.modules.IGregTechModule;
import hellfall.visualores.Tags;
import hellfall.visualores.VisualOres;
import hellfall.visualores.database.ClientCache;
import hellfall.visualores.database.CommandResetClientCache;
import hellfall.visualores.database.ServerCache;
import hellfall.visualores.database.WorldIDSaveData;
import hellfall.visualores.map.generic.RenderLayer;
import hellfall.visualores.network.ProspectToClientPacket;
import hellfall.visualores.network.WorldIDPacket;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
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
        GregTechAPI.networkHandler.registerPacket(ProspectToClientPacket.class);
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ClientCommandHandler.instance.registerCommand(new CommandResetClientCache());
            RenderLayer.initLayers();
        }
    }

    @Override
    public void serverStarting(FMLServerStartingEvent event) {
        WorldIDSaveData.init(event.getServer().getEntityWorld());
    }

    @Override
    public void serverStopped(FMLServerStoppedEvent event) {
        ServerCache.instance.clear();
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ClientCache.instance.clear();
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        ServerCache.instance.maybeInitWorld(event.getWorld());
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        ServerCache.instance.invalidateWorld(event.getWorld());
        if (event.getWorld().isRemote) {
            ClientCache.instance.saveCache();
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote) {
            if (event.getEntity() instanceof EntityPlayerMP) {
                GregTechAPI.networkHandler.sendTo(new WorldIDPacket(WorldIDSaveData.getWorldID()), (EntityPlayerMP) event.getEntity());
            }
            else if (event.getEntity() instanceof EntityPlayer) {
                VisualOres.LOGGER.info("got id local " + WorldIDSaveData.getWorldID());
                ClientCache.instance.init(WorldIDSaveData.getWorldID());
            }
        }
    }

    @SubscribeEvent
    public static void syncConfig(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Tags.MODID)) {
            ConfigManager.sync(Tags.MODID, Config.Type.INSTANCE);
        }
    }
}
