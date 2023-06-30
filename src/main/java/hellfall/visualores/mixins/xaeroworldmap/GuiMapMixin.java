package hellfall.visualores.mixins.xaeroworldmap;

import hellfall.visualores.SizedTexturedGuiButton;
import hellfall.visualores.VisualOres;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import xaero.map.gui.CursorBox;
import xaero.map.gui.GuiMap;
import xaero.map.gui.ScreenBase;

@Mixin(GuiMap.class)
public abstract class GuiMapMixin extends ScreenBase {
    @Unique
    private GuiButton testButton;

    protected GuiMapMixin(GuiScreen parent, GuiScreen escape) {
        super(parent, escape);
    }

    @Inject(method = "initGui", at = @At("TAIL"))
    private void visualores$injectInitGui(CallbackInfo ci) {
        this.addButton(
        testButton = new SizedTexturedGuiButton(this.width - 40, this.height - 20, 20, 20, 0, 0, 16, 16,
                new ResourceLocation("visualores", "textures/xaero/oreveins.png"),
                (button) -> VisualOres.LOGGER.info("buton"),
                () -> new CursorBox("ljfljsjls"))
        );
        testButton.enabled = true;
    }
}
