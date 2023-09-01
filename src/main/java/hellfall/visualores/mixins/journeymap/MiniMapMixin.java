package hellfall.visualores.mixins.journeymap;

import hellfall.visualores.VOConfig;
import hellfall.visualores.map.GenericMapRenderer;
import journeymap.client.model.MapState;
import journeymap.client.properties.MiniMapProperties;
import journeymap.client.render.map.GridRenderer;
import journeymap.client.ui.minimap.DisplayVars;
import journeymap.client.ui.minimap.MiniMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MiniMap.class, remap = false)
public abstract class MiniMapMixin {
    @Shadow private DisplayVars dv;
    @Shadow private MiniMapProperties miniMapProperties;
    @Shadow @Final private static MapState state;
    @Shadow @Final private static GridRenderer gridRenderer;

    @Shadow @Final private Minecraft mc;
    @Unique private GenericMapRenderer renderer;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void visualores$injectInit(MiniMapProperties miniMapProperties, CallbackInfo ci) {
        renderer = new GenericMapRenderer();
    }

    @Inject(method = "drawOnMapWaypoints", at = @At("HEAD"))
    private void visualores$injectDrawMinimap(double rotation, CallbackInfo ci) {
        if (VOConfig.client.enableMinimapRendering) {
            double scale = Math.pow(2, miniMapProperties.zoomLevel.get());
            double rw = ((DisplayVarsAccessor) dv).getMinimapWidth() / scale;
            double rh = ((DisplayVarsAccessor) dv).getMinimapHeight() / scale;
            renderer.updateVisibleArea(state.getDimension(), (int) (gridRenderer.getCenterBlockX() - rw / 2), (int) (gridRenderer.getCenterBlockZ() - rh / 2), (int) rw, (int) rh);

            GlStateManager.pushMatrix();
            GlStateManager.translate(mc.displayWidth / 2.0, mc.displayHeight / 2.0, 0);
            GlStateManager.scale(scale, scale, 1);
            GlStateManager.translate(-gridRenderer.getCenterBlockX(), -gridRenderer.getCenterBlockZ(), 0);

            renderer.render((gridRenderer.getCenterBlockX()), gridRenderer.getCenterBlockZ(), scale);

            GlStateManager.popMatrix();
        }
    }
}
