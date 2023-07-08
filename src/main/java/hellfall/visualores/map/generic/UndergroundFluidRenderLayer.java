package hellfall.visualores.map.generic;

import java.util.List;

public class UndergroundFluidRenderLayer extends RenderLayer {
    public UndergroundFluidRenderLayer(ButtonState.Button button) {
        super(button);
    }

    @Override
    public void render(double cameraX, double cameraZ, double scale) {

    }

    @Override
    public void updateVisibleArea(int dimensionID, int[] visibleBounds) {

    }

    @Override
    public List<String> getTooltip(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        return null;
    }

}
