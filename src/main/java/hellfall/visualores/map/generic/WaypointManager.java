package hellfall.visualores.map.generic;

import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

public class WaypointManager {
    public static int currentDimension;

    private static final Set<IWaypointHandler> handlers = new HashSet<>();
    private static final Object2ObjectMap<String, WaypointKey> waypoints = new Object2ObjectArrayMap<>();

    public static void updateDimension(int dim) {
        currentDimension = dim;
    }

    public static void setWaypoint(String key, String name, Integer dim, int x, int y, int z) {
        if (dim == null) dim = currentDimension;
        for (IWaypointHandler handler : handlers) {
            handler.setWaypoint(key, name, dim, x, y, z);
        }
        waypoints.put(key, new WaypointKey(dim, x, y, z));
    }

    public static void removeWaypoint(String key) {
        for (IWaypointHandler handler : handlers) {
            handler.removeWaypoint(key);
        }
        waypoints.remove(key);
    }

    public static boolean toggleWaypoint(String key, String name, Integer dim, int x, int y, int z) {
        if (dim == null) dim = currentDimension;
        if ((new WaypointKey(dim, x, y, z)).equals(waypoints.get(key))) {
            removeWaypoint(key);
            return false;
        }
        setWaypoint(key, name, dim, x, y, z);
        return true;
    }

    public static void registerWaypointHandler(IWaypointHandler handler) {
        handlers.add(handler);
    }

    private static class WaypointKey {
        int dim, x, y, z;

        public WaypointKey(int dim, int x, int y, int z) {
            this.dim = dim;
            this.x = x;
            this.y = y;
            this.z = z;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            WaypointKey that = (WaypointKey) o;
            return dim == that.dim && x == that.x && y == that.y && z == that.z;
        }

        @Override
        public int hashCode() {
            return Objects.hash(dim, x, y, z);
        }
    }
}
