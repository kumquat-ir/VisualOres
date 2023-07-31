package hellfall.visualores.map.layers.immersiveengineering;

import hellfall.visualores.database.immersiveengineering.ExcavatorVeinPosition;
import hellfall.visualores.database.immersiveengineering.IEClientCache;
import hellfall.visualores.map.DrawUtils;
import hellfall.visualores.map.layers.RenderLayer;
import net.minecraft.util.math.ChunkPos;

import java.util.List;

public class ExcavatorRenderLayer extends RenderLayer {
    private List<ExcavatorVeinPosition> visibleVeins;
    private ExcavatorVeinPosition hoveredVein;

    private static final int sideColor = 0xDDFFFFFF;
    private static final int midColor = 0x77FFFFFF;

    public ExcavatorRenderLayer(String key) {
        super(key);
    }

    @Override
    public void render(double cameraX, double cameraZ, double scale) {
        for (ExcavatorVeinPosition vein : visibleVeins) {
            DrawUtils.drawOverlayBox(vein.x, vein.z, sideColor, midColor);
        }
    }

    @Override
    public void updateVisibleArea(int dimensionID, int[] visibleBounds) {
        visibleVeins = IEClientCache.instance.getVeinsInArea(dimensionID, visibleBounds);
    }

    @Override
    public void updateHovered(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        ChunkPos mousePos = new ChunkPos(DrawUtils.getMouseBlockPos(mouseX, mouseY, cameraX, cameraZ, scale));
        hoveredVein = null;
        for (ExcavatorVeinPosition vein : visibleVeins) {
            if (vein.x == mousePos.x && vein.z == mousePos.z) {
                hoveredVein = vein;
                break;
            }
        }
    }

    @Override
    public List<String> getTooltip() {
        if (hoveredVein != null) {
            return hoveredVein.getTooltip();
        }
        return null;
    }
}
