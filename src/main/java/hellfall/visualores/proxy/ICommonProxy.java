package hellfall.visualores.proxy;

import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.*;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public interface ICommonProxy {
    // load stages
    default void preInit(FMLPreInitializationEvent event) {}
    default void init(FMLInitializationEvent event) {}
    default void postInit(FMLPostInitializationEvent event) {}

    // server stages
    default void serverStarting(FMLServerStartingEvent event) {}
    default void serverStopped(FMLServerStoppedEvent event) {}

    // world events
    default void worldLoad(WorldEvent.Load event) {}
    default void worldUnload(WorldEvent.Unload event) {}
    default void worldSave(WorldEvent.Save event) {}

    // other events
    default void entityJoinWorld(EntityJoinWorldEvent event) {}
    default void syncConfig(ConfigChangedEvent.OnConfigChangedEvent event) {}
    default void onKeyPress(InputEvent.KeyInputEvent event) {}
    default void onClientDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {}
}
