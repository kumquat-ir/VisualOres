package hellfall.visualores.map.generic;

import java.util.ArrayList;
import java.util.List;

public abstract class RenderLayer {
    public static List<RenderLayer> layers = new ArrayList<>();

    protected ButtonState.Button button;

    public static void initLayers() {
        layers.add(new OreRenderLayer(ButtonState.ORE_VEINS_BUTTON));
        layers.add(new UndergroundFluidRenderLayer(ButtonState.UNDERGROUND_FLUIDS_BUTTON));
    }

    public RenderLayer(ButtonState.Button button) {
        this.button = button;
    }

    public boolean isEnabled() {
        return button.enabled;
    }

    public abstract void render(double cameraX, double cameraZ, double scale);

    public abstract void updateVisibleArea(int dimensionID, int[] visibleBounds);

    public abstract List<String> getTooltip(double mouseX, double mouseY, double cameraX, double cameraZ, double scale);
}
