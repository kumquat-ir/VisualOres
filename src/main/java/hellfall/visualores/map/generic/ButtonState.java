package hellfall.visualores.map.generic;

import java.util.HashMap;
import java.util.Map;

public class ButtonState {
    private static final Map<String, Button> buttons = new HashMap<>();

    public static Button ORE_VEINS_BUTTON = new Button("ORE_VEINS");
    public static Button UNDERGROUND_FLUIDS_BUTTON = new Button("UNDERGROUND_FLUIDS");

    public static void toggleButton(Button button) {
        button.enabled = !button.enabled;

        // disable all other buttons if one is enabled
        if(button.enabled) {
            for (String name : buttons.keySet()) {
                if (!name.equals(button.name)) {
                    buttons.get(name).enabled = false;
                }
            }
        }
    }

    public static boolean isEnabled(Button button) {
        return button.enabled;
    }

    public static class Button {
        public boolean enabled;
        public String name;

        public Button(String name) {
            this.enabled = false;
            this.name = name;
            buttons.put(name, this);
        }
    }
}
