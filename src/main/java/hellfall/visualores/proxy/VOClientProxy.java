package hellfall.visualores.proxy;

import codechicken.lib.packet.PacketCustom;
import hellfall.visualores.KeyBindings;
import hellfall.visualores.Tags;
import hellfall.visualores.database.ClientCacheManager;
import hellfall.visualores.database.CommandResetClientCache;
import hellfall.visualores.map.DrawUtils;
import hellfall.visualores.map.WaypointManager;
import hellfall.visualores.map.journeymap.JourneymapWaypointHandler;
import hellfall.visualores.map.layers.Layers;
import hellfall.visualores.map.xaero.XaeroWaypointHandler;
import hellfall.visualores.network.CCLClientPacketHandler;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.client.settings.KeyConflictContext;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppedEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import org.lwjgl.input.Keyboard;

@SuppressWarnings("unused")
public class VOClientProxy extends VOCommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        if (Loader.isModLoaded("xaerominimap")) {
            WaypointManager.registerWaypointHandler(new XaeroWaypointHandler());
        }
        if (Loader.isModLoaded("journeymap")) {
            WaypointManager.registerWaypointHandler(new JourneymapWaypointHandler());
        }

        DrawUtils.initColorOverrides();
        PacketCustom.assignHandler(Tags.MODID, new CCLClientPacketHandler());
    }

    @Override
    public void init(FMLInitializationEvent event) {
        super.init(event);

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
        super.postInit(event);

        ClientCommandHandler.instance.registerCommand(new CommandResetClientCache());
    }

    @Override
    public void serverStopped(FMLServerStoppedEvent event) {
        super.serverStopped(event);

        ClientCacheManager.clearCaches();
    }

    @Override
    public void worldLoad(WorldEvent.Load event) {
        super.worldLoad(event);

        if (event.getWorld().isRemote) {
            WaypointManager.updateDimension(event.getWorld().provider.getDimension());
        }
    }

    @Override
    public void worldUnload(WorldEvent.Unload event) {
        super.worldUnload(event);

        if (event.getWorld().isRemote) {
            ClientCacheManager.saveCaches();
        }
    }

    @Override
    public void worldSave(WorldEvent.Save event) {
        super.worldSave(event);

        if (event.getWorld().isRemote) {
            ClientCacheManager.saveCaches();
        }
    }

    @Override
    public void syncConfig(ConfigChangedEvent.OnConfigChangedEvent event) {
        super.syncConfig(event);

        if (event.getModID().equals(Tags.MODID)) {
            ConfigManager.sync(Tags.MODID, Config.Type.INSTANCE);
        }
    }

    @Override
    public void onKeyPress(InputEvent.KeyInputEvent event) {
        super.onKeyPress(event);

        KeyBindings.toggleLayers();
    }
}
