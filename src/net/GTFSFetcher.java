package net;

import org.jxmapviewer.viewer.GeoPosition;
import com.google.transit.realtime.GtfsRealtime;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GTFSFetcher {
    private static final String GTFS_RT_URL = "https://romamobilita.it/sites/default/files/rome_rtgtfs_vehicle_positions_feed.pb";

    public static List<GeoPosition> fetchBusPositions(String route_id) {
        List<GeoPosition> positions = new ArrayList<>();

        try (InputStream inputStream = new URL(GTFS_RT_URL).openStream()) {
            GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(inputStream);

            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                if (entity.hasVehicle()) {
                    GtfsRealtime.VehiclePosition vehicle = entity.getVehicle();
                    if(route_id!=null && route_id.equals(vehicle.getTrip().getRouteId())) {
                    	System.out.println(vehicle);
	                    double lat = vehicle.getPosition().getLatitude();
	                    double lon = vehicle.getPosition().getLongitude();
	                    positions.add(new GeoPosition(lat, lon));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return positions;
    }
}
