package hellfall.visualores.mixins.xaeroworldmap;

import hellfall.visualores.map.xaero.SizedTexturedGuiButton;
import hellfall.visualores.map.generic.ButtonState;
import hellfall.visualores.map.generic.GenericMapRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.objectweb.asm.Opcodes;
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
        GuiButton oreVeinsButton = new SizedTexturedGuiButton(this.width - 40, this.height - 20, 20, 20,
                ButtonState.isEnabled(ButtonState.ORE_VEINS_BUTTON) ? 16 : 0, 0, 16, 16,
                new ResourceLocation("visualores", "textures/xaero/oreveins.png"),
                (button) -> {
                    ButtonState.toggleButton(ButtonState.ORE_VEINS_BUTTON);
                    setWorldAndResolution(mc, width, height);
                },
                () -> new CursorBox("visualores.button.oreveins"));
        GuiButton undergroundFluidsButton = new SizedTexturedGuiButton(this.width - 40, this.height - 40, 20, 20,
                ButtonState.isEnabled(ButtonState.UNDERGROUND_FLUIDS_BUTTON) ? 16 : 0, 0, 16, 16,
                new ResourceLocation("visualores", "textures/xaero/undergroundfluid.png"),
                (button) -> {
                    ButtonState.toggleButton(ButtonState.UNDERGROUND_FLUIDS_BUTTON);
                    setWorldAndResolution(mc, width, height);
                },
                () -> new CursorBox("visualores.button.undergroundfluids"));

        this.addButton(oreVeinsButton);
        this.addButton(undergroundFluidsButton);
        oreVeinsButton.enabled = true;
        undergroundFluidsButton.enabled = true;

        renderer = new GenericMapRenderer();
    }

    @Inject(method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"),
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

    @Inject(method = "drawScreen",
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lxaero/map/MapProcessor;renderThreadPauseSync:Ljava/lang/Object;"),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lxaero/map/gui/GuiMap;renderTooltips(IIF)Z"))
    )
    private void visualores$injectTooltip(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
        renderer.renderTooltip(scaledMouseX, scaledMouseY, cameraX, cameraZ, scale);
    }
}
