package hellfall.visualores;

import gregtech.api.modules.ModuleContainerRegistryEvent;
import gregtech.modules.ModuleManager;
import hellfall.visualores.module.VisualOresModuleContainer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Mod(modid = Tags.MODID, version = Tags.VERSION, name = Tags.MODNAME, acceptedMinecraftVersions = "[1.12.2]",
        dependencies = "required-before:gregtech@[2.6.2-beta,);") // actually USING the gt module system requires loading before gt
public class VisualOres {

    public static final Logger LOGGER = LogManager.getLogger(Tags.MODID);

    @EventHandler
    public void onConstruction(FMLConstructionEvent event) {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onModuleRegistration(ModuleContainerRegistryEvent event) {
        ModuleManager.getInstance().registerContainer(new VisualOresModuleContainer());
    }
}
