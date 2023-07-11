package hellfall.visualores.mixins.journeymap;

import journeymap.client.ui.minimap.DisplayVars;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

// this can be removed when rfg supports applying ats to dependencies
@Mixin(value = DisplayVars.class, remap = false)
public interface DisplayVarsAccessor {
    @Accessor
    int getMinimapWidth();

    @Accessor
    int getMinimapHeight();
}
