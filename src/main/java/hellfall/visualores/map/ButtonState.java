package hellfall.visualores.map;

import java.util.HashMap;
import java.util.Map;

public class ButtonState {
    private static final Map<String, Button> buttons = new HashMap<>();

    public static Button ORE_VEINS_BUTTON = new Button("ORE_VEINS");

    public static void toggleButton(String buttonName) {
        Button button = buttons.get(buttonName);
        button.enabled = !button.enabled;

        // disable all other buttons if one is enabled
        if(button.enabled) {
            for (String name : buttons.keySet()) {
                if (!name.equals(buttonName)) {
                    buttons.get(name).enabled = false;
                }
            }
        }
    }

    public static boolean isEnabled(String buttonName) {
        return buttons.get(buttonName).enabled;
    }

    public static class Button {
        public boolean enabled;

        public Button(String name) {
            this.enabled = false;
            buttons.put(name, this);
        }
    }
}
