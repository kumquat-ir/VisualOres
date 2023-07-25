package hellfall.visualores.mixins.xaeroworldmap;

import hellfall.visualores.KeyBindings;
import hellfall.visualores.VOConfig;
import hellfall.visualores.map.generic.ButtonState;
import hellfall.visualores.map.generic.GenericMapRenderer;
import hellfall.visualores.map.xaero.SizedTexturedGuiButton;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.settings.KeyConflictContext;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import xaero.map.MapProcessor;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiMap;
import xaero.map.gui.ScreenBase;
import xaero.map.misc.Misc;

@Mixin(GuiMap.class)
public abstract class GuiMapMixin extends ScreenBase {
    @Shadow(remap = false) private MapProcessor mapProcessor;

    @Shadow(remap = false) private double scale;
    @Shadow(remap = false) private double cameraX;
    @Shadow(remap = false) private double cameraZ;
    @Unique private GenericMapRenderer renderer;

    protected GuiMapMixin(GuiScreen parent, GuiScreen escape) {
        super(parent, escape);
    }

    @Inject(method = "initGui", at = @At("TAIL"))
    private void visualores$injectInitGui(CallbackInfo ci) {
        int startX, startY, xOffset, yOffset;

        switch (VOConfig.client.xmap.direction) {
            case VERTICAL -> {
                xOffset = 0;
                yOffset = 1;
            }
            case HORIZONTAL -> {
                xOffset = 1;
                yOffset = 0;
            }
            default -> throw new IllegalStateException("Unexpected value: " + VOConfig.client.xmap.direction);
        }

        switch (VOConfig.client.xmap.buttonAnchor) {
            case TOP_LEFT -> {
                startX = VOConfig.client.xmap.xOffset;
                startY = VOConfig.client.xmap.yOffset;
            }
            case TOP_CENTER -> {
                startX = width / 2 + VOConfig.client.xmap.xOffset;
                startY = VOConfig.client.xmap.yOffset;
            }
            case TOP_RIGHT -> {
                startX = width - 20 - VOConfig.client.xmap.xOffset;
                startY = VOConfig.client.xmap.yOffset;
                xOffset = -xOffset;
            }
            case RIGHT_CENTER -> {
                startX = width - 20 - VOConfig.client.xmap.xOffset;
                startY = height / 2 + VOConfig.client.xmap.yOffset;
                xOffset = -xOffset;
                yOffset = -yOffset;
            }
            case BOTTOM_RIGHT -> {
                startX = width - 20 - VOConfig.client.xmap.xOffset;
                startY = height - 20 - VOConfig.client.xmap.yOffset;
                xOffset = -xOffset;
                yOffset = -yOffset;
            }
            case BOTTOM_CENTER -> {
                startX = width / 2 + VOConfig.client.xmap.xOffset;
                startY = height - 20 - VOConfig.client.xmap.yOffset;
                yOffset = -yOffset;
            }
            case BOTTOM_LEFT -> {
                startX = VOConfig.client.xmap.xOffset;
                startY = height - 20 - VOConfig.client.xmap.yOffset;
                yOffset = -yOffset;
            }
            case LEFT_CENTER -> {
                startX = VOConfig.client.xmap.xOffset;
                startY = height / 2 + VOConfig.client.xmap.yOffset;
                yOffset = -yOffset;
            }
            default -> throw new IllegalStateException("Unexpected value: " + VOConfig.client.xmap.buttonAnchor);
        }

        if (VOConfig.client.xmap.buttonAnchor.isCentered()) {
            int totalButtonSize = ButtonState.buttonAmount() * 10;
            if (VOConfig.client.xmap.buttonAnchor.usualDirection().equals(VOConfig.client.xmap.direction)) {
                startX -= xOffset * totalButtonSize;
                startY -= yOffset * totalButtonSize;
                if (xOffset < 0) startX -= 20;
                if (yOffset < 0) startY -= 20;
            }
            else {
                startX -= Math.abs(yOffset) * 10;
                startY -= Math.abs(xOffset) * 10;
            }
        }

        int offset = 0;
        for (ButtonState.Button button : ButtonState.getAllButtons()) {
            GuiButton mapButton = new SizedTexturedGuiButton(
                    startX + (20 * xOffset * offset), startY + (20 * yOffset * offset), 20, 20,
                    ButtonState.isEnabled(button) ? 16 : 0, 0, 16, 16,
                    new ResourceLocation("visualores", "textures/xaero/" + button.name + ".png"),
                    (guiButton -> {
                        ButtonState.toggleButton(button);
                        setWorldAndResolution(mc, width, height);
                    }),
                    () -> new CursorBox("visualores.button." + button.name)
            );

            addButton(mapButton);
            mapButton.enabled = true;
            offset++;
        }

        renderer = new GenericMapRenderer((GuiMap) (Object) this);
    }

    @Inject(method = "drawScreen",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/GlStateManager;translate(FFF)V"),
            slice = @Slice(
                    from = @At(value = "INVOKE", target = "Lxaero/map/element/MapElementRenderHandler;render(Lxaero/map/gui/GuiMap;DDIIDDDDFZLxaero/map/element/HoveredMapElementHolder;Lnet/minecraft/client/Minecraft;FLnet/minecraft/client/gui/ScaledResolution;)Lxaero/map/element/HoveredMapElementHolder;", remap = false),
                    to = @At(value = "INVOKE", target = "Lxaero/map/MapProcessor;getFootprints()Ljava/util/ArrayList;", remap = false)
            )
    )
    private void visualores$injectDraw(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
        double rw = mc.displayWidth / scale;
        double rh = mc.displayHeight / scale;
        renderer.updateVisibleArea(mapProcessor.getMapWorld().getCurrentDimensionId(), (int) (cameraX - rw / 2), (int) (cameraZ - rh / 2), (int) (rw), (int) (rh));

        renderer.render(cameraX, cameraZ, scale);
    }

    // so buttons with semi-transparent regions render correctly
    @Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Lxaero/map/gui/ScreenBase;drawScreen(IIF)V"))
    private void visualores$injectDrawButtons(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
        GlStateManager.enableBlend();
    }

    @Inject(method = "drawScreen",
            at = @At(value = "FIELD", opcode = Opcodes.GETFIELD, target = "Lxaero/map/MapProcessor;renderThreadPauseSync:Ljava/lang/Object;", remap = false),
            slice = @Slice(from = @At(value = "INVOKE", target = "Lxaero/map/gui/GuiMap;renderTooltips(IIF)Z", remap = false))
    )
    private void visualores$injectTooltip(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci) {
        renderer.updateHovered(scaledMouseX, scaledMouseY, cameraX, cameraZ, scale);
        renderer.renderTooltip(scaledMouseX, scaledMouseY);
    }

    @Inject(method = "onInputPress", at = @At("HEAD"), cancellable = true, remap = false)
    private void visualores$injectKeyPress(boolean mouse, int code, CallbackInfoReturnable<Boolean> cir) {
        if (Misc.inputMatchesKeyBinding(mouse, code, KeyBindings.action, KeyConflictContext.GUI) && renderer.onActionKey()) {
            cir.setReturnValue(true);
        }
    }

    // no need to cancel if something was done, mapClicked normally only does stuff on right click
    @Inject(method = "mapClicked", at = @At("TAIL"), remap = false)
    private void visualores$injectMapClicked(int button, int x, int y, CallbackInfo ci) {
        if (button == 0) {
            renderer.onClick(x, y);
        }
    }
}
