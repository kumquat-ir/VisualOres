package hellfall.visualores.map;

public interface IWaypointHandler {

    void setWaypoint(String key, String name, int color, int dim, int x, int y, int z);

    void removeWaypoint(String key);

}
