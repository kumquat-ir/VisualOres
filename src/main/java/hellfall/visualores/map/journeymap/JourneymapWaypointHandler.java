package hellfall.visualores.map.journeymap;

import hellfall.visualores.map.IWaypointHandler;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import journeymap.client.model.Waypoint;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.Collection;
import java.util.Map;

public class JourneymapWaypointHandler implements IWaypointHandler {
    private static final Map<String, Waypoint> waypoints = new Object2ObjectOpenHashMap<>();

    @Override
    public void setWaypoint(String key, String name, int color, int dim, int x, int y, int z) {
        waypoints.put(key, new Waypoint(name, new BlockPos(x, y, z), new Color(color), Waypoint.Type.Normal, dim));
    }

    @Override
    public void removeWaypoint(String key) {
        waypoints.remove(key);
    }

    public static Collection<Waypoint> getWaypoints() {
        return waypoints.values();
    }
}
