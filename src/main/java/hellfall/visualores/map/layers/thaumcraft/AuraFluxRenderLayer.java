package hellfall.visualores.map.layers.thaumcraft;

import hellfall.visualores.database.thaumcraft.AuraFluxPosition;
import hellfall.visualores.database.thaumcraft.TCClientCache;
import hellfall.visualores.map.DrawUtils;
import hellfall.visualores.map.layers.RenderLayer;
import net.minecraft.util.math.ChunkPos;

import java.util.ArrayList;
import java.util.List;

public class AuraFluxRenderLayer extends RenderLayer {
    private List<AuraFluxPosition> visibleChunks = new ArrayList<>();
    private AuraFluxPosition hoveredChunk;

    public AuraFluxRenderLayer(String key) {
        super(key);
    }

    @Override
    public void render(double cameraX, double cameraZ, double scale) {
        for (AuraFluxPosition chunk : visibleChunks) {
            DrawUtils.drawOverlayBox(chunk.x, chunk.z, chunk.color, chunk.midColor);
        }
    }

    @Override
    public void updateVisibleArea(int dimensionID, int[] visibleBounds) {
        visibleChunks = TCClientCache.instance.getVeinsInArea(dimensionID, visibleBounds);
    }

    @Override
    public void updateHovered(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        ChunkPos mousePos = new ChunkPos(DrawUtils.getMouseBlockPos(mouseX, mouseY, cameraX, cameraZ, scale));
        hoveredChunk = null;
        for (AuraFluxPosition chunk : visibleChunks) {
            if (chunk.x == mousePos.x && chunk.z == mousePos.z) {
                hoveredChunk = chunk;
                break;
            }
        }
    }

    @Override
    public List<String> getTooltip() {
        if (hoveredChunk != null) {
            return hoveredChunk.tooltips;
        }
        return null;
    }
}
