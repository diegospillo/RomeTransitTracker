package waypoint;

import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.Waypoint;

/**
 * Waypoint semplice utilizzato per rappresentare un bus in movimento.
 */
public class BusWaypoint implements Waypoint {
    private final GeoPosition position;

    /**
     * Costruisce un waypoint per un bus alle coordinate specificate.
     *
     * @param lat latitudine
     * @param lon longitudine
     */
    public BusWaypoint(double lat, double lon) {
        this.position = new GeoPosition(lat, lon);
    }

    @Override
    public GeoPosition getPosition() {
        return position;
    }
}
