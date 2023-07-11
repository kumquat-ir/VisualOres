package hellfall.visualores.mixins.journeymap;

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

import java.util.Arrays;
import java.util.List;

@Mixin(Fullscreen.class)
public abstract class FullscreenMixin extends JmUI implements ITabCompleter {

	@Unique private JourneymapRenderer renderer;

	@Shadow ThemeToolbar mapTypeToolbar;

	@Shadow @Final static MapState state;

	@Shadow @Final static GridRenderer gridRenderer;

	@Shadow FullMapProperties fullMapProperties;

	@Unique private ThemeToggle oreVeinButton;
	@Unique private ThemeToggle undergroundFluidButton;

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
		oreVeinButton = new ThemeToggle(theme, "visualores.button.oreveins", "oreveins");
		oreVeinButton.setToggled(ButtonState.isEnabled(ButtonState.ORE_VEINS_BUTTON), false);
		oreVeinButton.setEnabled(true);
		oreVeinButton.addToggleListener((button, toggled) -> {
			ButtonState.toggleButton(ButtonState.ORE_VEINS_BUTTON);

			return true;
		});

		undergroundFluidButton = new ThemeToggle(theme, "visualores.button.undergroundfluids", "undergroundfluid");
		undergroundFluidButton.setToggled(ButtonState.isEnabled(ButtonState.UNDERGROUND_FLUIDS_BUTTON), false);
		undergroundFluidButton.setEnabled(true);
		undergroundFluidButton.addToggleListener((button, toggled) -> {
			ButtonState.toggleButton(ButtonState.UNDERGROUND_FLUIDS_BUTTON);

			return true;
		});

		// jank to not have to add an accessor/at
		this.mapTypeToolbar.reverse();
		this.mapTypeToolbar.reverse().addAll(0, Arrays.asList(oreVeinButton, undergroundFluidButton));

		renderer = new JourneymapRenderer((Fullscreen) (Object) this);
	}

	@Inject(method = "layoutButtons", at = @At("TAIL"), remap = false)
	private void visualores$injectLayoutButtons(CallbackInfo ci) {
		oreVeinButton.setToggled(ButtonState.isEnabled(ButtonState.ORE_VEINS_BUTTON), false);
		undergroundFluidButton.setToggled(ButtonState.isEnabled(ButtonState.UNDERGROUND_FLUIDS_BUTTON), false);
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
		if (tooltip == null || tooltip.isEmpty()) {
			double scale = Math.pow(2, fullMapProperties.zoomLevel.get());
			renderer.renderTooltip(scaledMouseX, scaledMouseY, gridRenderer.getCenterBlockX(), gridRenderer.getCenterBlockZ(), scale);
		}
	}
}
