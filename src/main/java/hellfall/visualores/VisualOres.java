package hellfall.visualores;

import codechicken.lib.CodeChickenLib;
import gregtech.api.modules.ModuleContainerRegistryEvent;
import gregtech.modules.ModuleManager;
import hellfall.visualores.gtmodule.VisualOresModuleContainer;
import hellfall.visualores.proxy.ICommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Optional;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]",
        dependencies = "before:gregtech@[2.7.2-beta,);" // actually USING the gt module system requires loading before gt
                + CodeChickenLib.MOD_VERSION_DEP + "required:mixinbooter")
public class VisualOres {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);

    @SidedProxy(modId = Tags.MODID, clientSide = "hellfall.visualores.proxy.VOClientProxy", serverSide = "hellfall.visualores.proxy.VOCommonProxy")
    public static ICommonProxy voProxy;

    private static final List<ICommonProxy> proxies = new ArrayList<>();
    private static final Set<String> modsRequiringServer = new HashSet<>();
    private static boolean clientOnlyMode = false;

    // FML lifecycle events //
    @EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        proxies.add(voProxy);
        addModRequiringServer("gregtech");
    }

    @EventHandler
    public void onPreInit(FMLPreInitializationEvent event) {
        for (ICommonProxy proxy : proxies) {
            proxy.preInit(event);
        }
    }

    @EventHandler
    public void onInit(FMLInitializationEvent event) {
        for (ICommonProxy proxy : proxies) {
            proxy.init(event);
        }
    }

    @EventHandler
    public void onPostInit(FMLPostInitializationEvent event) {
        for (ICommonProxy proxy : proxies) {
            proxy.postInit(event);
        }
    }

    @EventHandler
    public void onServerStarting(FMLServerStartingEvent event) {
        for (ICommonProxy proxy : proxies) {
            proxy.serverStarting(event);
        }
    }

    @EventHandler
    public void onServerStopped(FMLServerStoppedEvent event) {
        for (ICommonProxy proxy : proxies) {
            proxy.serverStopped(event);
        }
    }

    // Forge events //
    @SubscribeEvent
    public void onWorldLoad(WorldEvent.Load event) {
        for (ICommonProxy proxy : proxies) {
            proxy.worldLoad(event);
        }
    }

    @SubscribeEvent
    public void onWorldUnload(WorldEvent.Unload event) {
        for (ICommonProxy proxy : proxies) {
            proxy.worldUnload(event);
        }
    }

    @SubscribeEvent
    public void onWorldSave(WorldEvent.Save event) {
        for (ICommonProxy proxy : proxies) {
            proxy.worldSave(event);
        }
    }

    @SubscribeEvent
    public void onEntityJoinWorld(EntityJoinWorldEvent event) {
        for (ICommonProxy proxy : proxies) {
            proxy.entityJoinWorld(event);
        }
    }

    @SubscribeEvent
    public void onConfigSave(ConfigChangedEvent.OnConfigChangedEvent event) {
        for (ICommonProxy proxy : proxies) {
            proxy.syncConfig(event);
        }
    }

    @SubscribeEvent
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        for (ICommonProxy proxy : proxies) {
            proxy.onKeyPress(event);
        }
    }

    // non-Forge events //
    @Optional.Method(modid = "gregtech")
    @SubscribeEvent
    public void onModuleRegistration(ModuleContainerRegistryEvent event) {
        ModuleManager.getInstance().registerContainer(new VisualOresModuleContainer());
    }

    // client-only mode handling //
    @NetworkCheckHandler
    public boolean checkModVersions(Map<String, String> mods, Side side) {
        boolean containsVO = mods.containsKey(Tags.MODID);
        boolean containsSameVOVersion = containsVO && mods.get(Tags.MODID).equals(Tags.VERSION);

        if (side == Side.CLIENT) {
            // we are on the server
            // server will only accept clients with the same version of VisualOres as it
            // this means the client does not need to check if it has the same version
            return containsSameVOVersion;
        }

        // we are on the client
        clientOnlyMode = !mods.containsKey(Tags.MODID);
        for (String modid : modsRequiringServer) {
            if (mods.containsKey(modid) && !containsVO) {
                // the server has a mod that requires VisualOres to be on the server but it is not
                LOGGER.error("Could not connect to server! Server does not have " + Tags.MODID + " but it is required when " + modid + " is loaded!");
                return false;
            }
        }
        // the server has no mods that require VisualOres to be on both sides, so we do not care if the server has VisualOres or not
        // but if it does, we want it to be the same version
        return !containsVO || containsSameVOVersion;
    }

    /**
     * Add a mod whose presence requires VisualOres to be present on both the client and server.
     */
    public static void addModRequiringServer(String modid) {
        modsRequiringServer.add(modid);
    }

    public static boolean isClientOnlyMode() {
        return clientOnlyMode;
    }
}
