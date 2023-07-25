package hellfall.visualores.map.xaero;

import hellfall.visualores.map.WaypointManager;
import xaero.common.minimap.waypoints.Waypoint;

public class WaypointWithDimension extends Waypoint {
    private final int dim;

    public WaypointWithDimension(int dim, int x, int y, int z, String name, String symbol, int color) {
        super(x, y, z, name, symbol, color);
        this.dim = dim;
    }

    @Override
    public boolean isDisabled() {
        return super.isDisabled() || dim != WaypointManager.currentDimension;
    }
}
