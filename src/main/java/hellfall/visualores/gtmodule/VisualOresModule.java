package hellfall.visualores.gtmodule;

import gregtech.api.GregTechAPI;
import gregtech.api.modules.GregTechModule;
import gregtech.api.modules.IGregTechModule;
import hellfall.visualores.KeyBindings;
import hellfall.visualores.Tags;
import hellfall.visualores.VisualOres;
import hellfall.visualores.database.ClientCacheManager;
import hellfall.visualores.database.CommandResetClientCache;
import hellfall.visualores.database.WorldIDSaveData;
import hellfall.visualores.database.gregtech.GTClientCache;
import hellfall.visualores.database.gregtech.fluid.UndergroundFluidPosition;
import hellfall.visualores.database.gregtech.ore.ServerCache;
import hellfall.visualores.map.GenericMapRenderer;
import hellfall.visualores.map.WaypointManager;
import hellfall.visualores.map.journeymap.JourneymapWaypointHandler;
import hellfall.visualores.map.layers.Layers;
import hellfall.visualores.map.layers.gregtech.OreRenderLayer;
import hellfall.visualores.map.layers.gregtech.UndergroundFluidRenderLayer;
import hellfall.visualores.map.xaero.XaeroWaypointHandler;
import hellfall.visualores.network.WorldIDPacket;
import hellfall.visualores.network.gregtech.OreProspectToClientPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.relauncher.Side;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.lwjgl.input.Keyboard;

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
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            if (!Minecraft.getMinecraft().getFramebuffer().isStencilEnabled()) {
                Minecraft.getMinecraft().getFramebuffer().enableStencil();
            }
            GenericMapRenderer.stencilEnabled = Minecraft.getMinecraft().getFramebuffer().isStencilEnabled();
            if (!GenericMapRenderer.stencilEnabled) {
                VisualOres.LOGGER.error("Could not enable stencil buffer! Xaero's minimap rendering will be disabled.");
            }

            Layers.registerLayer(OreRenderLayer.class, "oreveins");
            Layers.registerLayer(UndergroundFluidRenderLayer.class, "undergroundfluid");
            WaypointManager.registerWaypointHandler(new XaeroWaypointHandler());
            WaypointManager.registerWaypointHandler(new JourneymapWaypointHandler());

            ClientCacheManager.registerClientCache(GTClientCache.instance, "gregtech");
            UndergroundFluidPosition.initColorOverrides();
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        KeyBindings.action = new KeyBinding("visualores.key.action", KeyConflictContext.GUI, Keyboard.KEY_DELETE, "visualores.keycategory");
        ClientRegistry.registerKeyBinding(KeyBindings.action);

        for (String layer : Layers.allKeys()) {
            KeyBinding binding = new KeyBinding("visualores.button." + layer, KeyConflictContext.IN_GAME, Keyboard.KEY_NONE, "visualores.keycategory");
            ClientRegistry.registerKeyBinding(binding);
            KeyBindings.layerToggles.put(binding, layer);
        }
    }

    @Override
    public void postInit(FMLPostInitializationEvent event) {
        if (FMLCommonHandler.instance().getSide() == Side.CLIENT) {
            ClientCommandHandler.instance.registerCommand(new CommandResetClientCache());
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
            ClientCacheManager.clearCaches();
        }
    }

    @SubscribeEvent
    public static void onWorldLoad(WorldEvent.Load event) {
        if (event.getWorld().isRemote) {
            WaypointManager.updateDimension(event.getWorld().provider.getDimension());
        }
        else {
            ServerCache.instance.maybeInitWorld(event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onWorldUnload(WorldEvent.Unload event) {
        if (event.getWorld().isRemote) {
            ClientCacheManager.saveCaches();
        }
        else {
            ServerCache.instance.invalidateWorld(event.getWorld());
        }
    }

    @SubscribeEvent
    public static void onWorldSave(WorldEvent.Save event) {
        if (event.getWorld().isRemote) {
            ClientCacheManager.saveCaches();
        }
    }

    @SubscribeEvent
    public static void onEntityJoin(EntityJoinWorldEvent event) {
        if (!event.getWorld().isRemote) {
            if (event.getEntity() instanceof EntityPlayerMP) {
                GregTechAPI.networkHandler.sendTo(new WorldIDPacket(WorldIDSaveData.getWorldID()), (EntityPlayerMP) event.getEntity());
            }
            else if (event.getEntity() instanceof EntityPlayer) {
//                VisualOres.LOGGER.info("got id local " + WorldIDSaveData.getWorldID());
//                GTClientCache.instance.init(WorldIDSaveData.getWorldID());
                ClientCacheManager.init(WorldIDSaveData.getWorldID());
            }
        }
    }

    @SubscribeEvent
    public static void syncConfig(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(Tags.MODID)) {
            ConfigManager.sync(Tags.MODID, Config.Type.INSTANCE);
        }
    }

    @SubscribeEvent
    public static void onKeyPress(InputEvent.KeyInputEvent event) {
        KeyBindings.toggleLayers();
    }
}
