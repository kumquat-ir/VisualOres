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
    private static ExcavatorVeinPosition waypointVein;

    public ExcavatorRenderLayer(String key) {
        super(key);
    }

    @Override
    public void render(double cameraX, double cameraZ, double scale) {
        for (ExcavatorVeinPosition vein : visibleVeins) {
            int sideColor = vein.color & 0xDDFFFFFF;
            if (vein == waypointVein) sideColor = 0xFFFFD700;
            DrawUtils.drawOverlayBox(vein.x, vein.z, sideColor, vein.color & 0x77FFFFFF);
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

    @Override
    public boolean onDoubleClick() {
        if (hoveredVein == null) return false;
        waypointVein = toggleWaypoint(
                hoveredVein.getTooltip().get(0),0xFFFFFF, null, hoveredVein.x * 16 + 8, 64, hoveredVein.z * 16 + 8
        ) ? hoveredVein : null;
        return true;
    }
}
