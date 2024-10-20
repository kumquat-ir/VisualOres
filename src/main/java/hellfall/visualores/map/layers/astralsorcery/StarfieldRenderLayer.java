package hellfall.visualores.map.layers.astralsorcery;

import codechicken.lib.gui.GuiDraw;
import hellfall.visualores.database.astralsorcery.ASClientCache;
import hellfall.visualores.database.astralsorcery.StarfieldPosition;
import hellfall.visualores.map.layers.RenderLayer;

import java.util.ArrayList;
import java.util.List;

public class StarfieldRenderLayer extends RenderLayer {
    private static final int LOW_COLOR = 0x7700063A;
    private static final int HIGH_COLOR = 0xAA0012B7;
    private List<StarfieldPosition> visibleChunks = new ArrayList<>();

    public StarfieldRenderLayer(String key) {
        super(key);
    }

    @Override
    public void render(double cameraX, double cameraZ, double scale) {
        // is this laggy or not?
        for (StarfieldPosition pos : visibleChunks) {
            int bx = pos.x << 4;
            int bz = pos.z << 4;
            for (int i = 0; i < 256; i++) {
                if (pos.high[i]) {
                    GuiDraw.drawRect(bx + i / 16, bz + i % 16, 1, 1, HIGH_COLOR);
                }
                else if (pos.low[i]) {
                    GuiDraw.drawRect(bx + i / 16, bz + i % 16, 1, 1, LOW_COLOR);
                }
            }
        }
    }

    @Override
    public void updateVisibleArea(int dimensionID, int[] visibleBounds) {
        visibleChunks = ASClientCache.instance.getStarfieldsInBounds(dimensionID, visibleBounds);
    }
}
