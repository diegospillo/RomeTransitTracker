package net;

import org.jxmapviewer.viewer.GeoPosition;
import com.google.transit.realtime.GtfsRealtime;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class GTFSFetcher {
    private static final String GTFS_RT_URL = "https://romamobilita.it/sites/default/files";

    public static List<GeoPosition> fetchBusPositions(String route_id,boolean direction) {
        List<GeoPosition> positions = new ArrayList<>();
        Integer dir_int = direction ? 0 : 1;
        try (InputStream inputStream = new URL(GTFS_RT_URL+"/rome_rtgtfs_vehicle_positions_feed.pb").openStream()) {
            GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(inputStream);

            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                if (entity.hasVehicle()) {
                    GtfsRealtime.VehiclePosition vehicle = entity.getVehicle();
                    if(route_id!=null && route_id.equals(vehicle.getTrip().getRouteId()) && dir_int.equals(vehicle.getTrip().getDirectionId())) {
                    	//System.out.println(vehicle);
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
    
    public static List<GeoPosition> fetchTripUpdates(String route_id,boolean direction) {
        List<GeoPosition> positions = new ArrayList<>();
        Integer dir_int = direction ? 0 : 1;
        try (InputStream inputStream = new URL(GTFS_RT_URL+"/rome_rtgtfs_trip_updates_feed.pb").openStream()) {
            GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(inputStream);

            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                if (entity.hasTripUpdate()) {
                    GtfsRealtime.TripUpdate trip = entity.getTripUpdate();
                    System.out.println(trip.toString());
                    System.out.println("---------");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return positions;
    }
}
