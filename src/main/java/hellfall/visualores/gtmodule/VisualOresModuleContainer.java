package hellfall.visualores.gtmodule;

import gregtech.api.modules.IModuleContainer;
import gregtech.api.modules.ModuleContainer;
import hellfall.visualores.Tags;

@ModuleContainer
public class VisualOresModuleContainer implements IModuleContainer {
    public static final String VO_MODULE = "visualores";

    @Override
    public String getID() {
        return Tags.MODID;
    }
}
