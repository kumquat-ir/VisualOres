package hellfall.visualores.map.xaero;

import hellfall.visualores.Tags;
import hellfall.visualores.map.IWaypointHandler;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import xaero.common.minimap.waypoints.Waypoint;
import xaero.common.minimap.waypoints.WaypointsManager;

import java.util.Hashtable;
import java.util.List;

public class XaeroWaypointHandler implements IWaypointHandler {
    private final Hashtable<Integer, Waypoint> xwaypoints = WaypointsManager.getCustomWaypoints(Tags.MODID);
    private final List<String> knownKeys = new ObjectArrayList<>();

    @Override
    public void setWaypoint(String key, String name, int color, int dim, int x, int y, int z) {
        // todo get an actual color for this
        xwaypoints.put(getIndex(key), new WaypointWithDimension(dim, x, y, z, name, name.substring(0, 1), 15));
    }

    @Override
    public void removeWaypoint(String key) {
        xwaypoints.remove(getIndex(key));
    }

    private int getIndex(String key) {
        if (!knownKeys.contains(key)) {
            knownKeys.add(key);
        }
        return knownKeys.indexOf(key);
    }
}
