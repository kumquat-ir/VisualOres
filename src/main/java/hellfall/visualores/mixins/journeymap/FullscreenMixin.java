package hellfall.visualores.mixins.journeymap;

import gregtech.api.util.GTLog;
import journeymap.client.io.ThemeLoader;
import journeymap.client.model.MapState;
import journeymap.client.model.MapType;
import journeymap.client.ui.component.Button;
import journeymap.client.ui.component.ButtonList;
import journeymap.client.ui.fullscreen.Fullscreen;
import journeymap.client.ui.theme.Theme;
import journeymap.client.ui.theme.ThemeButton;
import journeymap.client.ui.theme.ThemeToggle;
import journeymap.client.ui.theme.ThemeToolbar;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;
import java.util.Collections;

@Mixin(value = Fullscreen.class, remap = false)
public abstract class FullscreenMixin {

	@Unique public ThemeButton oreVeinButton;

	@Shadow ThemeToolbar mapTypeToolbar;

	@Inject(method = "initButtons",
			at = @At(value = "FIELD",
					target = "Ljourneymap/client/ui/fullscreen/Fullscreen;mapTypeToolbar:Ljourneymap/client/ui/theme/ThemeToolbar;",
					opcode = Opcodes.PUTFIELD,
					shift = At.Shift.AFTER
			)
	)
	private void injectInitButtons(CallbackInfo ci) {
		final Theme theme = ThemeLoader.getCurrentTheme();
		this.oreVeinButton = new ThemeToggle(theme, "test", "oreveins");
		this.oreVeinButton.setToggled(true, false);
		this.oreVeinButton.setEnabled(true);
		this.oreVeinButton.addToggleListener((button, toggled) -> {
			System.out.println("owao");

			return true;
		});

		// jank to not have to add an accessor/at
		this.mapTypeToolbar.reverse();
		this.mapTypeToolbar.reverse().addAll(0, Collections.singletonList(this.oreVeinButton));
	}
}
