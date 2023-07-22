package hellfall.visualores.mixins.journeymap;

import hellfall.visualores.KeyBindings;
import hellfall.visualores.map.generic.ButtonState;
import hellfall.visualores.map.journeymap.JourneymapRenderer;
import journeymap.client.io.ThemeLoader;
import journeymap.client.model.MapState;
import journeymap.client.properties.FullMapProperties;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.map.GridRenderer;
import journeymap.client.ui.component.JmUI;
import journeymap.client.ui.fullscreen.Fullscreen;
import journeymap.client.ui.theme.Theme;
import journeymap.client.ui.theme.ThemeToggle;
import journeymap.client.ui.theme.ThemeToolbar;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ITabCompleter;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mixin(Fullscreen.class)
public abstract class FullscreenMixin extends JmUI implements ITabCompleter {

	@Unique private JourneymapRenderer renderer;

	@Shadow ThemeToolbar mapTypeToolbar;

	@Shadow @Final static MapState state;

	@Shadow @Final static GridRenderer gridRenderer;

	@Shadow FullMapProperties fullMapProperties;

	@Unique private Map<String, ThemeToggle> buttons;

	public FullscreenMixin(String title) {
		super(title);
	}

	@Inject(method = "initButtons",
			at = @At(value = "FIELD",
					target = "Ljourneymap/client/ui/fullscreen/Fullscreen;mapTypeToolbar:Ljourneymap/client/ui/theme/ThemeToolbar;",
					opcode = Opcodes.PUTFIELD,
					shift = At.Shift.AFTER
			), remap = false
	)
	private void visualores$injectInitButtons(CallbackInfo ci) {
		final Theme theme = ThemeLoader.getCurrentTheme();
		buttons = new HashMap<>();

		for (ButtonState.Button button : ButtonState.getAllButtons()) {
			ThemeToggle mapButton = new ThemeToggle(theme, "visualores.button." + button.name, button.name);
			mapButton.setToggled(ButtonState.isEnabled(button), false);
			mapButton.setEnabled(true);
			mapButton.addToggleListener((onOffButton, b) -> {
				ButtonState.toggleButton(button);

				return true;
			});

			buttons.put(button.name, mapButton);
		}

		// jank to not have to add an accessor/at
		this.mapTypeToolbar.reverse();
		this.mapTypeToolbar.reverse().addAll(0, buttons.values());

		renderer = new JourneymapRenderer((Fullscreen) (Object) this);
	}

	@Inject(method = "layoutButtons", at = @At("TAIL"), remap = false)
	private void visualores$injectLayoutButtons(CallbackInfo ci) {
		for (String buttonName : buttons.keySet()) {
			buttons.get(buttonName).setToggled(ButtonState.isEnabled(buttonName), false);
		}
	}

	@Redirect(method = "drawMap",
			at = @At(value = "INVOKE", target = "Ljourneymap/client/render/map/GridRenderer;draw(Ljava/util/List;DDDD)V", ordinal = 1),
			remap = false
	)
	private void visualores$injectDrawMap(GridRenderer instance, List<? extends DrawStep> drawStepList, double xOffset, double yOffset, double fontScale, double rotation) {
		instance.draw(drawStepList, xOffset, yOffset, fontScale, rotation);

		double scale = Math.pow(2, fullMapProperties.zoomLevel.get());
		double rw = mc.displayWidth / scale;
		double rh = mc.displayHeight / scale;
		renderer.updateVisibleArea(state.getDimension(), (int) (gridRenderer.getCenterBlockX() - xOffset - rw / 2), (int) (gridRenderer.getCenterBlockZ() - yOffset - rh / 2), (int) rw, (int) rh);

		GlStateManager.pushMatrix();
		GlStateManager.translate(xOffset, yOffset, 0);
		GlStateManager.scale(scale, scale, 1);
		GlStateManager.translate(rw / 2, rh / 2, 0);

		renderer.render(gridRenderer.getCenterBlockX(), gridRenderer.getCenterBlockZ(), scale);

		GlStateManager.popMatrix();
	}

	@Inject(method = "drawScreen", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", shift = At.Shift.BY, by = -2), locals = LocalCapture.CAPTURE_FAILEXCEPTION)
	private void visualores$injectTooltip(int scaledMouseX, int scaledMouseY, float partialTicks, CallbackInfo ci, List<String> tooltip) {
		double scale = Math.pow(2, fullMapProperties.zoomLevel.get());
		renderer.updateHovered(scaledMouseX, scaledMouseY, gridRenderer.getCenterBlockX(), gridRenderer.getCenterBlockZ(), scale);
		if (tooltip == null || tooltip.isEmpty()) {
			renderer.renderTooltip(scaledMouseX, scaledMouseY);
		}
	}

	@Inject(method = "keyTyped", at = @At("TAIL"))
	private void visualores$injectKeyPress(char c, int key, CallbackInfo ci) {
		if (KeyBindings.action.getKeyCode() == key) {
			renderer.onActionKey();
		}
	}
}
