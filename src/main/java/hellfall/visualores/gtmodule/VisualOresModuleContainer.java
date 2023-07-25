package hellfall.visualores.gtmodule;

import gregtech.api.modules.IModuleContainer;
import hellfall.visualores.Tags;

public class VisualOresModuleContainer implements IModuleContainer {
    public static final String VO_MODULE = "VisualOres";

    @Override
    public String getID() {
        return Tags.MODID;
    }
}
