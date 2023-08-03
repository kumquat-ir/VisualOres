package hellfall.visualores.map.layers.astralsorcery;

import hellfall.visualores.database.astralsorcery.ASClientCache;
import hellfall.visualores.database.astralsorcery.NeromanticPosition;
import hellfall.visualores.map.DrawUtils;
import hellfall.visualores.map.layers.RenderLayer;
import net.minecraft.util.math.ChunkPos;

import java.util.List;

public class NeromanticRenderLayer extends RenderLayer {
    private List<NeromanticPosition> visibleChunks;
    private NeromanticPosition hoveredChunk;

    public NeromanticRenderLayer(String key) {
        super(key);
    }

    @Override
    public void render(double cameraX, double cameraZ, double scale) {
        for (NeromanticPosition vein : visibleChunks) {
            int sideColor = (vein.color & 0x00FFFFFF) + 0xDD000000;
            int midColor = (vein.color & 0x00FFFFFF) + 0x77000000;

            DrawUtils.drawOverlayBox(vein.x, vein.z, sideColor, midColor);
        }
    }

    @Override
    public void updateVisibleArea(int dimensionID, int[] visibleBounds) {
        visibleChunks = ASClientCache.instance.getNeromanticVeinsInBounds(dimensionID, visibleBounds);
    }

    @Override
    public void updateHovered(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        ChunkPos mousePos = new ChunkPos(DrawUtils.getMouseBlockPos(mouseX, mouseY, cameraX, cameraZ, scale));
        hoveredChunk = null;
        for (NeromanticPosition vein : visibleChunks) {
            if (vein.x == mousePos.x && vein.z == mousePos.z) {
                hoveredChunk = vein;
                break;
            }
        }
    }

    @Override
    public List<String> getTooltip() {
        if (hoveredChunk != null) {
            return hoveredChunk.tooltip;
        }
        return null;
    }
}
