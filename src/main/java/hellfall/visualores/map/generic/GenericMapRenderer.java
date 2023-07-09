package hellfall.visualores.map.generic;

import hellfall.visualores.map.DrawUtils;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

/**
 * A map renderer designed to work with any map mod.
 */
@SideOnly(Side.CLIENT)
public class GenericMapRenderer {

    protected int dimensionID;
    protected GuiScreen gui;

    protected int[] visibleBounds = new int[4];

    public GenericMapRenderer() {}

    public GenericMapRenderer(GuiScreen gui) {
        this.gui = gui;
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
     * Render all active map overlays.
     * <br>
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

    /**
     * Render tooltips provided by active overlays.
     * <br>
     * Will error if called from a context with no GuiScreen provided - such as a minimap. Only call if one was provided.
     * @param mouseX X position of the mouse, intended to be taken from the first parameter of {@link net.minecraft.client.gui.GuiScreen#drawScreen}
     * @param mouseY Y position of the mouse, see mouseX
     * @param cameraX X position of the center block of the view
     * @param cameraZ Z position of the center block of the view
     * @param scale Scale of the camera, such that scaling by <code>1/scale</code> results in 1 unit = 1 pixel
     */
    public void renderTooltip(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        for (RenderLayer layer : RenderLayer.layers) {
            if (layer.isEnabled()) {
                List<String> tooltip = layer.getTooltip(mouseX, mouseY, cameraX, cameraZ, scale);
                if (tooltip != null && !tooltip.isEmpty()) {
                    DrawUtils.drawSimpleTooltip(tooltip, mouseX, mouseY, gui.width, gui.height, 0xFFFFFFFF, 0x86000000);
                }
            }
        }
    }
}
