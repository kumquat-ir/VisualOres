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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]",
        dependencies = "required-before:gregtech@[2.7.2-beta,);" // actually USING the gt module system requires loading before gt
                + CodeChickenLib.MOD_VERSION_DEP + "required-after:mixinbooter")
public class VisualOres {
    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);

    @SidedProxy(modId = Tags.MODID, clientSide = "hellfall.visualores.proxy.VOClientProxy", serverSide = "hellfall.visualores.proxy.VOCommonProxy")
    public static ICommonProxy voProxy;

    public static List<ICommonProxy> proxies = new ArrayList<>();

    @EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);

        proxies.add(voProxy);
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

    @Optional.Method(modid = "gregtech")
    @SubscribeEvent
    public void onModuleRegistration(ModuleContainerRegistryEvent event) {
        ModuleManager.getInstance().registerContainer(new VisualOresModuleContainer());
    }
}
