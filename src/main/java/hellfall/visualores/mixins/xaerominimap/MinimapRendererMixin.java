package hellfall.visualores.mixins.xaerominimap;

import hellfall.visualores.VOConfig;
import hellfall.visualores.map.GenericMapRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.common.IXaeroMinimap;
import xaero.common.minimap.MinimapInterface;
import xaero.common.minimap.element.render.over.MinimapElementOverMapRendererHandler;
import xaero.common.minimap.render.MinimapRenderer;
import xaero.common.minimap.render.MinimapRendererHelper;
import xaero.common.minimap.waypoints.render.CompassRenderer;
import xaero.common.minimap.waypoints.render.WaypointsGuiRenderer;

@Mixin(value = MinimapRenderer.class, remap = false)
public abstract class MinimapRendererMixin {
    @Shadow protected Minecraft mc;
    @Shadow protected double zoom;
    @Unique private GenericMapRenderer renderer;
    @Unique private int frameSize;
    @Unique private float angle;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void visualores$injectConstruct(IXaeroMinimap modMain, Minecraft mc, WaypointsGuiRenderer waypointsGuiRenderer, MinimapInterface minimapInterface, CompassRenderer compassRenderer, CallbackInfo ci) {
        renderer = new GenericMapRenderer();
    }

    // these capture* methods are to avoid having to use a particularly horrible and volatile local capture to get some values
    // note: this one seems to cause mcDev to freak out in weird ways, try commenting and uncommenting the @ModifyVariable to "fix" random "errors" that pop up
    // if veins suddenly only render on the minimap right next to where you are, you forgot to uncomment it
    @ModifyVariable(method = "renderMinimap", at = @At(value = "LOAD", ordinal = 0), name = "minimapFrameSize")
    private int visualores$captureMinimapSize(int frameSize) {
        this.frameSize = frameSize;
        return frameSize;
    }

    @Redirect(method = "renderMinimap", at = @At(value = "INVOKE", target = "Ljava/lang/Math;toRadians(D)D"))
    private double visualores$captureMinimapAngle(double v) {
        angle = (float) v - 90;
        return Math.toRadians(v);
    }

    @Redirect(method = "renderMinimap",
            at = @At(value = "INVOKE", target = "Lxaero/common/minimap/element/render/over/MinimapElementOverMapRendererHandler;render(Lnet/minecraft/entity/Entity;Lnet/minecraft/entity/player/EntityPlayer;DDDDDDZFLnet/minecraft/client/shader/Framebuffer;Lxaero/common/IXaeroMinimap;Lxaero/common/minimap/render/MinimapRendererHelper;Lnet/minecraft/client/gui/FontRenderer;Lnet/minecraft/client/gui/ScaledResolution;IIIIZF)D")
    )
    private double visualores$injectRender(MinimapElementOverMapRendererHandler instance, Entity renderEntity, EntityPlayer player, double renderX, double renderY, double renderZ, double ps, double pc, double zoom, boolean cave, float partialTicks, Framebuffer framebuffer, IXaeroMinimap modMain, MinimapRendererHelper helper, FontRenderer font, ScaledResolution scaledRes, int specW, int specH, int halfViewW, int halfViewH, boolean circle, float minimapScale) {
        if (VOConfig.client.enableMinimapRendering) {
            renderer.updateVisibleArea(mc.player.dimension, (int) (renderX - frameSize), (int) (renderZ - frameSize), frameSize * 2, frameSize * 2);

            GlStateManager.pushMatrix();
            GlStateManager.enableDepth();
            GlStateManager.depthFunc(GL11.GL_GREATER);
            GlStateManager.rotate(angle, 0, 0, 1);
            GlStateManager.scale(this.zoom, this.zoom, 1);
            GlStateManager.translate(-renderX, -renderZ, 0);
            renderer.render(renderX, renderZ, zoom);
            GlStateManager.depthFunc(GL11.GL_LEQUAL);
            GlStateManager.disableDepth();
            GlStateManager.popMatrix();
        }

        return instance.render(renderEntity, player, renderX, renderY, renderZ, ps, pc, zoom, cave, partialTicks, framebuffer, modMain, helper, font, scaledRes, specW, specH, halfViewW, halfViewH, circle, minimapScale);
    }

    @Redirect(method = "renderMinimap", at = @At(value = "INVOKE", target = "Lxaero/common/minimap/render/MinimapRendererHelper;drawMyTexturedModalRect(FFIIFFF)V"))
    private void visualores$depthRectMinimap(MinimapRendererHelper instance, float x, float y, int textureX, int textureY, float width, float height, float factor) {
        GlStateManager.enableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(false, false, false, false);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 1000);
        instance.drawMyTexturedModalRect(x, y, textureX, textureY, width, height, factor);
        GlStateManager.popMatrix();
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthMask(false);
        GlStateManager.enableTexture2D();
        GlStateManager.disableDepth();

        instance.drawMyTexturedModalRect(x, y, textureX, textureY, width, height, factor);
    }

    @Redirect(method = "renderMinimap", at = @At(value = "INVOKE", target = "Lxaero/common/minimap/render/MinimapRendererHelper;drawTexturedElipseInsideRectangle(DIFFIIFF)V"))
    private void visualores$depthCircleMinimap(MinimapRendererHelper instance, double startAngle, int sides, float x, float y, int textureX, int textureY, float width, float widthFactor) {
        GlStateManager.enableDepth();
        GlStateManager.disableTexture2D();
        GlStateManager.depthMask(true);
        GlStateManager.colorMask(false, false, false, false);
        GlStateManager.pushMatrix();
        GlStateManager.translate(0, 0, 1000);
        ((MinimapRendererHelperAccessor) instance).invokeDrawTexturedElipseInsideRectangle(startAngle, sides, x, y, textureX, textureY, width, widthFactor);
        GlStateManager.popMatrix();
        GlStateManager.colorMask(true, true, true, true);
        GlStateManager.depthMask(false);
        GlStateManager.enableTexture2D();
        GlStateManager.disableDepth();

        ((MinimapRendererHelperAccessor) instance).invokeDrawTexturedElipseInsideRectangle(startAngle, sides, x, y, textureX, textureY, width, widthFactor);
    }
}
