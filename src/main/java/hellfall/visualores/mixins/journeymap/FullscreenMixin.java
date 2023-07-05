package hellfall.visualores.mixins.journeymap;

import hellfall.visualores.map.ButtonState;
import hellfall.visualores.map.GenericMapRenderer;
import journeymap.client.io.ThemeLoader;
import journeymap.client.model.MapState;
import journeymap.client.properties.FullMapProperties;
import journeymap.client.render.draw.DrawStep;
import journeymap.client.render.map.GridRenderer;
import journeymap.client.ui.component.JmUI;
import journeymap.client.ui.fullscreen.Fullscreen;
import journeymap.client.ui.theme.Theme;
import journeymap.client.ui.theme.ThemeButton;
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

import java.util.Collections;
import java.util.List;

@Mixin(value = Fullscreen.class, remap = false)
public abstract class FullscreenMixin extends JmUI implements ITabCompleter {

	@Unique private ThemeButton oreVeinButton;
	@Unique private GenericMapRenderer renderer;

	@Shadow ThemeToolbar mapTypeToolbar;

	@Shadow @Final private static MapState state;

	@Shadow @Final private static GridRenderer gridRenderer;

	@Shadow private FullMapProperties fullMapProperties;

	public FullscreenMixin(String title) {
		super(title);
	}

	@Inject(method = "initButtons",
			at = @At(value = "FIELD",
					target = "Ljourneymap/client/ui/fullscreen/Fullscreen;mapTypeToolbar:Ljourneymap/client/ui/theme/ThemeToolbar;",
					opcode = Opcodes.PUTFIELD,
					shift = At.Shift.AFTER
			)
	)
	private void visualores$injectInitButtons(CallbackInfo ci) {
		final Theme theme = ThemeLoader.getCurrentTheme();
		this.oreVeinButton = new ThemeToggle(theme, "test", "oreveins");
		this.oreVeinButton.setToggled(ButtonState.isEnabled("ORE_VEINS"), false);
		this.oreVeinButton.setEnabled(true);
		this.oreVeinButton.addToggleListener((button, toggled) -> {
			ButtonState.toggleButton("ORE_VEINS");

			return true;
		});

		// jank to not have to add an accessor/at
		this.mapTypeToolbar.reverse();
		this.mapTypeToolbar.reverse().addAll(0, Collections.singletonList(this.oreVeinButton));

		renderer = new GenericMapRenderer();
	}

	@Redirect(method = "drawMap",
			at = @At(value = "INVOKE", target = "Ljourneymap/client/render/map/GridRenderer;draw(Ljava/util/List;DDDD)V", ordinal = 1)
	)
	private void visualores$injectDrawMap(GridRenderer instance, List<? extends DrawStep> drawStepList, double xOffset, double yOffset, double fontScale, double rotation) {
		instance.draw(drawStepList, xOffset, yOffset, fontScale, rotation);

		double scale = Math.pow(2, fullMapProperties.zoomLevel.get());
		double rw = mc.displayWidth / scale;
		double rh = mc.displayHeight / scale;
		renderer.updateVisibleArea(state.getDimension(), (int) (gridRenderer.getCenterBlockX() - rw / 2), (int) (gridRenderer.getCenterBlockZ() - rh / 2), (int) rw, (int) rh);

		GlStateManager.pushMatrix();
		GlStateManager.translate(xOffset, yOffset, 0);
		GlStateManager.scale(scale, scale, 1);
		GlStateManager.translate(rw / 2, rh / 2, 0);

		renderer.render(gridRenderer.getCenterBlockX(), gridRenderer.getCenterBlockZ(), scale);

		GlStateManager.popMatrix();
	}
}
