package hellfall.visualores.proxy;

import hellfall.visualores.database.ClientCacheManager;
import hellfall.visualores.database.gregtech.GTClientCache;
import hellfall.visualores.map.layers.Layers;
import hellfall.visualores.map.layers.gregtech.OreRenderLayer;
import hellfall.visualores.map.layers.gregtech.UndergroundFluidRenderLayer;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class GTClientProxy extends GTCommonProxy {
    @Override
    public void preInit(FMLPreInitializationEvent event) {
        super.preInit(event);

        Layers.registerLayer(OreRenderLayer.class, "oreveins");
        Layers.registerLayer(UndergroundFluidRenderLayer.class, "undergroundfluid");

        ClientCacheManager.registerClientCache(GTClientCache.instance, "gregtech");
    }
}
