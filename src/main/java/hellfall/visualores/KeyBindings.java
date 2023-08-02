package hellfall.visualores;

import hellfall.visualores.map.ButtonState;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import net.minecraft.client.settings.KeyBinding;

import java.util.Map;

public class KeyBindings {
    public static KeyBinding action;

    public static final Map<KeyBinding, String> layerToggles = new Object2ObjectArrayMap<>();

    public static void toggleLayers() {
        for (KeyBinding key : layerToggles.keySet()) {
            if (key.isPressed()) {
                ButtonState.toggleButton(layerToggles.get(key));
            }
        }
    }
}
