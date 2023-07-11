package hellfall.visualores.map.journeymap;

import hellfall.visualores.map.generic.GenericMapRenderer;
import hellfall.visualores.map.generic.RenderLayer;
import journeymap.client.ui.fullscreen.Fullscreen;

import java.util.List;

/**
 * A map renderer for Journeymap, uses Journeymap's own tooltip rendering to fit existing theming better
 */
public class JourneymapRenderer extends GenericMapRenderer {
    public JourneymapRenderer(Fullscreen gui) {
        super(gui);
    }

    @Override
    public void renderTooltip(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        for (RenderLayer layer : layers) {
            if (layer.isEnabled()) {
                List<String> tooltip = layer.getTooltip(mouseX, mouseY, cameraX, cameraZ, scale);
                if (tooltip != null && !tooltip.isEmpty()) {
                    ((Fullscreen) gui).drawHoveringText(tooltip, (int) mouseX, (int) mouseY, ((Fullscreen) gui).getFontRenderer());
                }
            }
        }
    }
}
