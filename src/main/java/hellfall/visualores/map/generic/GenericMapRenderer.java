package hellfall.visualores.map.generic;

import hellfall.visualores.map.DrawUtils;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * A map renderer designed to work with any map mod.
 */
@SideOnly(Side.CLIENT)
public class GenericMapRenderer {

    protected int mouseX;
    protected int mouseY;

    protected int dimensionID;

    protected int[] visibleBounds = new int[4];

    public void updateMousePosition(int mouseX, int mouseY) {
        this.mouseX = mouseX;
        this.mouseY = mouseY;
    }

    public void updateVisibleArea(int dim, int x, int y, int w, int h) {
        if (dimensionID != dim || visibleBounds[0] != x || visibleBounds[1] != y || visibleBounds[2] != w || visibleBounds[3] != h) {
            dimensionID = dim;
            visibleBounds[0] = x;
            visibleBounds[1] = y;
            visibleBounds[2] = w;
            visibleBounds[3] = h;

            for (RenderLayer layer : RenderLayer.layers) {
                layer.updateVisibleArea(dimensionID, visibleBounds);
            }

        }
    }

    /**
     * EXPECTED GL STATE:
     * <br>
     * Coordinates: 1 unit = 1 block, positioned at block positions in world (i.e. drawing at 5,12 draws on the block at 5,12)
     * @param cameraX X position of the center block of the view
     * @param cameraZ Z position of the center block of the view
     * @param scale Scale of the camera, such that scaling by <code>1/scale</code> results in 1 unit = 1 pixel
     */
    public void render(double cameraX, double cameraZ, double scale) {
        for (RenderLayer layer : RenderLayer.layers) {
            if (layer.isEnabled()) {
                layer.render(cameraX, cameraZ, scale);
            }
        }
    }

    public void renderTooltip(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        for (RenderLayer layer : RenderLayer.layers) {
            if (layer.isEnabled()) {
                List<String> tooltip = layer.getTooltip(mouseX, mouseY, cameraX, cameraZ, scale);
                if (tooltip != null && !tooltip.isEmpty()) {
                    DrawUtils.drawSimpleTooltip(tooltip, mouseX, mouseY, 0xFFFFFFFF, 0x86000000);
                }
            }
        }
    }

    public void mousePressed() {

    }
}
