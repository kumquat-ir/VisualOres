package hellfall.visualores.map;

import hellfall.visualores.VOConfig;
import hellfall.visualores.map.layers.Layers;
import hellfall.visualores.map.layers.RenderLayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.ArrayList;
import java.util.List;

/**
 * A map renderer designed to work with any map mod.
 */
@SideOnly(Side.CLIENT)
public class GenericMapRenderer {
    private static final int VISIBLE_AREA_PADDING = 20;

    protected int dimensionID;
    protected GuiScreen gui;

    protected int[] visibleBounds = new int[4];

    protected List<RenderLayer> layers;
    private double oldMouseX;
    private double oldMouseY;
    private long timeLastClick;

    public GenericMapRenderer() {
        layers = new ArrayList<>();
        Layers.addLayersTo(layers);
    }

    public GenericMapRenderer(GuiScreen gui) {
        this();
        this.gui = gui;
    }

    public void updateVisibleArea(int dim, int x, int y, int w, int h) {
        // padding visible area to reduce/eliminate pop-in at map edges
        x -= VISIBLE_AREA_PADDING;
        y -= VISIBLE_AREA_PADDING;
        w += VISIBLE_AREA_PADDING * 2;
        h += VISIBLE_AREA_PADDING * 2;
        if (dimensionID != dim || visibleBounds[0] != x || visibleBounds[1] != y || visibleBounds[2] != w || visibleBounds[3] != h) {
            dimensionID = dim;
            visibleBounds[0] = x;
            visibleBounds[1] = y;
            visibleBounds[2] = w;
            visibleBounds[3] = h;

            for (RenderLayer layer : layers) {
                layer.updateVisibleArea(dimensionID, visibleBounds);
            }

        }
    }

    /**
     * Update the active overlays' hovered items.
     * @param mouseX X position of the mouse, intended to be taken from the first parameter of {@link net.minecraft.client.gui.GuiScreen#drawScreen}
     * @param mouseY Y position of the mouse, see mouseX
     * @param cameraX X position of the center block of the view
     * @param cameraZ Z position of the center block of the view
     * @param scale Scale of the camera, such that scaling by <code>1/scale</code> results in 1 unit = 1 pixel
     */
    public void updateHovered(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {
        ScaledResolution res = new ScaledResolution(Minecraft.getMinecraft());
        for (RenderLayer layer : layers) {
            if (layer.isEnabled()) {
                layer.updateHovered(mouseX * res.getScaleFactor(), mouseY * res.getScaleFactor(), cameraX, cameraZ, scale);
            }
        }
    }

    /**
     * Render all active map overlays.
     * <br>
     * EXPECTED GL STATE:
     * <br>
     * 1 unit = 1 block, positioned such that drawing at (x, z) draws over the entire block (x, z)
     * @param cameraX X position of the center block of the view
     * @param cameraZ Z position of the center block of the view
     * @param scale Scale of the camera, such that scaling by <code>1/scale</code> results in 1 unit = 1 pixel
     */
    public void render(double cameraX, double cameraZ, double scale) {
        for (RenderLayer layer : layers) {
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
     */
    public void renderTooltip(double mouseX, double mouseY) {
        List<String> tooltip = new ArrayList<>();
        for (RenderLayer layer : layers) {
            if (layer.isEnabled()) {
                List<String> layerTooltip = layer.getTooltip();
                if (layerTooltip != null && !layerTooltip.isEmpty()) {
                    if (!tooltip.isEmpty() && VOConfig.client.stackTooltips) {
                        tooltip.add(0, "---");
                        tooltip.addAll(0, layerTooltip);
                    }
                    else {
                        // layerTooltip might be immutable
                        tooltip = new ArrayList<>(layerTooltip);
                    }
                }
            }
        }
        renderTooltipInternal(tooltip, mouseX, mouseY);
    }

    /**
     * Call when {@link hellfall.visualores.KeyBindings#action} is pressed.
     * @return Whether to consume the key press
     */
    public boolean onActionKey() {
        for (RenderLayer layer : layers) {
            if (layer.isEnabled() && layer.onActionKey()) {
                return true;
            }
        }
        return false;
    }

    public boolean onClick(double mouseX, double mouseY) {
        final long timestamp = System.currentTimeMillis();
        final boolean isDoubleClick = mouseX == oldMouseX && mouseY == oldMouseY && timestamp - timeLastClick < 500;
        oldMouseX = mouseX;
        oldMouseY = mouseY;
        timeLastClick = isDoubleClick ? 0 : timestamp;

        for (RenderLayer layer : layers) {
            if (layer.isEnabled()) {
                if (isDoubleClick && layer.onDoubleClick() || !isDoubleClick && layer.onClick()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Override this if specializing the renderer to have consistent tooltip theming
     */
    protected void renderTooltipInternal(List<String> tooltip, double mouseX, double mouseY) {
        DrawUtils.drawSimpleTooltip(tooltip, mouseX, mouseY, gui.width, gui.height, 0xFFFFFFFF, 0x86000000);
    }
}
