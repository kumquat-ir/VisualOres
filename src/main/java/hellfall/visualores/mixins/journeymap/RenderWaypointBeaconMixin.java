package hellfall.visualores.mixins.journeymap;

import hellfall.visualores.map.journeymap.JourneymapWaypointHandler;
import journeymap.client.model.Waypoint;
import journeymap.client.render.ingame.RenderWaypointBeacon;
import journeymap.client.waypoint.WaypointStore;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mixin(value = RenderWaypointBeacon.class, remap = false)
public class RenderWaypointBeaconMixin {
    @Redirect(method = "renderAll",
            at = @At(value = "INVOKE", target = "Ljourneymap/client/waypoint/WaypointStore;getAll()Ljava/util/Collection;")
    )
    private static Collection<Waypoint> visualores$injectRenderAll(WaypointStore instance) {
        List<Waypoint> waypoints = new ArrayList<>(instance.getAll());
        waypoints.addAll(JourneymapWaypointHandler.getWaypoints());
        return waypoints;
    }
}
