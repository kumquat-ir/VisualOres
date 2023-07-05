package hellfall.visualores.mixins.xaeroworldmap;

import hellfall.visualores.SizedTexturedGuiButton;
import hellfall.visualores.map.ButtonState;
import hellfall.visualores.map.GenericMapRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.MapProcessor;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiMap;
import xaero.map.gui.ScreenBase;

@Mixin(GuiMap.class)
public abstract class GuiMapMixin extends ScreenBase {
    @Shadow private MapProcessor mapProcessor;

    @Shadow private double scale;
    @Shadow private double cameraX;
    @Shadow private double cameraZ;
    @Unique private GenericMapRenderer renderer;

    protected GuiMapMixin(GuiScreen parent, GuiScreen escape) {
        super(parent, escape);
    }

    @Inject(method = "initGui", at = @At("TAIL"))
    private void visualores$injectInitGui(CallbackInfo ci) {
        GuiButton testButton;
        this.addButton(
        testButton = new SizedTexturedGuiButton(this.width - 40, this.height - 20, 20, 20, 0, 0, 16, 16,
                new ResourceLocation("visualores", "textures/xaero/oreveins.png"),
                (button) -> ButtonState.toggleButton("ORE_VEINS"),
                () -> new CursorBox("ljfljsjls"))
        );
        testButton.enabled = true;

        renderer = new GenericMapRenderer();
    }

    @Inject(method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V", shift = At.Shift.AFTER),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lxaero/map/element/MapElementRenderHandler;render(Lxaero/map/gui/GuiMap;DDIIDDDDFZLxaero/map/element/HoveredMapElementHolder;Lnet/minecraft/client/Minecraft;FLnet/minecraft/client/gui/ScaledResolution;)Lxaero/map/element/HoveredMapElementHolder;"),
                    to = @At(value = "INVOKE", target = "Lxaero/map/MapProcessor;getFootprints()Ljava/util/ArrayList;")
            )
    )
    private void visualores$injectDraw(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
        double rw = mc.displayWidth / scale;
        double rh = mc.displayHeight / scale;
        renderer.updateVisibleArea(mapProcessor.getMapWorld().getCurrentDimensionId(), (int) (cameraX - rw / 2), (int) (cameraZ - rh / 2), (int) (rw), (int) (rh));

        renderer.render(cameraX, cameraZ, scale);
    }
}
