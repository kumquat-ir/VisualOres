package hellfall.visualores.map.generic;

import java.util.List;

public abstract class RenderLayer {
    protected final String key;

    public RenderLayer(String key) {
        this.key = key;
    }

    public boolean isEnabled() {
        return ButtonState.isEnabled(key);
    }

    /**
     * Render the overlay.
     * <br>
     * Starting GL state:
     * <br>
     * 1 unit = 1 block, positioned such that drawing at (x - cameraX, z - cameraZ) draws on the entire block (x, z)
     * @param cameraX The X position of the center block of the view
     * @param cameraZ The Z position of the center block of the view
     * @param scale The scale of the view, such that going from blocks -> pixels requires scaling at <code>1/scale</code>
     */
    public abstract void render(double cameraX, double cameraZ, double scale);

    /**
     * Update what parts of the overlay should be visible.
     * @param dimensionID The ID of the dimension currently being viewed.
     * @param visibleBounds Contains the X and Z coordinates of the top left block of the view, followed by the width and height, in blocks
     */
    public abstract void updateVisibleArea(int dimensionID, int[] visibleBounds);

    /**
     * Update what part of the overlay the mouse is over.
     * Data required for {@link #getTooltip()}, {@link #onActionKey()}, etc. should be cached here.
     * @param mouseX The mouse's X position. Does not need to be adjusted for gui scale.
     * @param mouseY The mouse's Y position. Does not need to be adjusted for gui scale.
     * @param cameraX (as in {@link #render(double, double, double)})
     * @param cameraZ (as in {@link #render(double, double, double)})
     * @param scale (as in {@link #render(double, double, double)})
     */
    public void updateHovered(double mouseX, double mouseY, double cameraX, double cameraZ, double scale) {}

    /**
     * @return A list of strings that contains the lines to be rendered in the tooltip.
     */
    public List<String> getTooltip() {
        return null;
    }

    /**
     * @return true if the keypress should be consumed
     */
    public boolean onActionKey() {
        return false;
    }

    /**
     * Generally, only one of this and {@link #onDoubleClick()} should be overridden
     * @return true if the click should be consumed
     */
    public boolean onClick() {
        return false;
    }

    /**
     * Generally, only one of this and {@link #onClick()} should be overridden
     * @return true if the click should be consumed
     */
    public boolean onDoubleClick() {
        return false;
    }

    /**
     * Toggle this layer's waypoint.
     * @param name The name of the waypoint.
     * @param dim The ID of the dimension the waypoint should be in, <code>null</code> for the player's current dimension
     * @return true if the waypoint was created or moved, false if it was deleted.
     */
    @SuppressWarnings("SameParameterValue")
    protected final boolean toggleWaypoint(String name, Integer dim, int x, int y, int z) {
        return WaypointManager.toggleWaypoint(key, name, dim, x, y, z);
    }
}
