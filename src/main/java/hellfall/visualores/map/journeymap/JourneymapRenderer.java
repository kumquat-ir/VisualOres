package hellfall.visualores.map.journeymap;

import hellfall.visualores.map.generic.GenericMapRenderer;
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
    protected void renderTooltipInternal(List<String> tooltip, double mouseX, double mouseY) {
        ((Fullscreen) gui).drawHoveringText(tooltip, (int) mouseX, (int) mouseY, ((Fullscreen) gui).getFontRenderer());
    }
}
