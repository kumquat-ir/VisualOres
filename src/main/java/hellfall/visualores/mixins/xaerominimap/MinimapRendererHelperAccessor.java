package hellfall.visualores.mixins.xaerominimap;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;
import xaero.common.minimap.render.MinimapRendererHelper;

@Mixin(MinimapRendererHelper.class)
public interface MinimapRendererHelperAccessor {
    // why the hell is this package-private
    @Invoker
    void invokeDrawTexturedElipseInsideRectangle(double startAngle, int sides, float x, float y, int textureX, int textureY, float width, float widthFactor);
}
