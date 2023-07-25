package hellfall.visualores.map;

import hellfall.visualores.VOConfig;

import java.util.*;
import java.util.stream.Collectors;

public class ButtonState {
    private static final Map<String, Button> buttons = new HashMap<>();
    private static List<Button> sortedButtons;
    private static List<Button> reverseSortedButtons;

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

    public static void toggleButton(String buttonName) {
        toggleButton(buttons.get(buttonName));
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

    public static List<Button> getAllButtons() {
        if (sortedButtons == null) {
            sortedButtons = buttons.values().stream().sorted(
                    Comparator.comparingInt(b -> Arrays.asList(VOConfig.client.buttonOrder).indexOf(b.name))
            ).collect(Collectors.toList());
            reverseSortedButtons = new ArrayList<>(sortedButtons);
            Collections.reverse(reverseSortedButtons);
        }
        return VOConfig.client.reverseButtonOrder ? reverseSortedButtons : sortedButtons;
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
