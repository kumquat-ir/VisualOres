package hellfall.visualores.map.generic;

import hellfall.visualores.VOConfig;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class ButtonState {
    private static final Map<String, Button> buttons = new HashMap<>();

    public static void toggleButton(Button button) {
        button.enabled = !button.enabled;

        // disable all other buttons if one is enabled
        if (!VOConfig.client.allowMultipleLayers && button.enabled) {
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

    public static boolean isEnabled(String buttonName) {
        return buttons.get(buttonName).enabled;
    }

    public static int buttonAmount() {
        return buttons.size();
    }

    public static Collection<Button> getAllButtons() {
        return buttons.values().stream().sorted(Comparator.comparingInt(a -> VOConfig.client.reverseButtonOrder ? -a.sort : a.sort)).collect(Collectors.toList());
    }

    public static class Button {
        public boolean enabled;
        protected int sort;
        public String name;

        public Button(String name, int sort) {
            this.enabled = false;
            this.name = name;
            this.sort = sort;
            buttons.put(name, this);
        }
    }
}
