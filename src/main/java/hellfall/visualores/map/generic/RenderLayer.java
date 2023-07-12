package hellfall.visualores.map.generic;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;

public abstract class RenderLayer {
    private static final List<Class<? extends RenderLayer>> layerClasses = new ArrayList<>();

    public static void registerLayer(Class<? extends RenderLayer> clazz) {
        layerClasses.add(clazz);
    }

    public static void addLayersTo(List<RenderLayer> layers) {
        for (Class<? extends RenderLayer> layer : layerClasses) {
            try {
                layers.add(layer.getConstructor().newInstance());
            } catch (InstantiationException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected ButtonState.Button button;

    public RenderLayer(ButtonState.Button button) {
        this.button = button;
    }

    public boolean isEnabled() {
        return button.enabled;
    }

    public abstract void render(double cameraX, double cameraZ, double scale);

    public abstract void updateVisibleArea(int dimensionID, int[] visibleBounds);

    public List<String> getTooltip(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        return null;
    }
}
