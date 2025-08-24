package net;

import org.jxmapviewer.viewer.GeoPosition;
import com.google.transit.realtime.GtfsRealtime;

import model.ModelManager;
import model.Trip;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Recupera informazioni in tempo reale dai feed GTFS forniti da Roma
 * Mobilità.
 */
public class GTFSFetcher {
    private static final String BASE_URL = "https://romamobilita.it/sites/default/files";
    private static final String VEHICLE_POSITIONS = BASE_URL + "/rome_rtgtfs_vehicle_positions_feed.pb";
    private static final String TRIP_UPDATES = BASE_URL + "/rome_rtgtfs_trip_updates_feed.pb";
    private static final ModelManager modelManager = ModelManager.getInstance();

    /**
     * Ottiene la posizione corrente dei bus per una specifica linea.
     *
     * @param route_id  identificativo della linea
     * @param direction direzione (true→0, false→1)
     * @return mappa tripId → posizione geografica
     */
    public static Map<String, GeoPosition> fetchBusPositions(String route_id, boolean direction) {
        Map<String, GeoPosition> positions = new HashMap<>();
        Integer dir_int = direction ? 0 : 1;
        try (InputStream inputStream = new URL(VEHICLE_POSITIONS).openStream()) {
            GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(inputStream);

            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                if (entity.hasVehicle()) {
                    GtfsRealtime.VehiclePosition vehicle = entity.getVehicle();
                    if (route_id != null && route_id.equals(vehicle.getTrip().getRouteId())
                            && dir_int.equals(vehicle.getTrip().getDirectionId())) {
                        String tripId = vehicle.getTrip().getTripId();
                        double lat = vehicle.getPosition().getLatitude();
                        double lon = vehicle.getPosition().getLongitude();
                        positions.put(tripId, new GeoPosition(lat, lon));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return positions;
    }

    /**
     * Recupera gli aggiornamenti di orario per i trip in corso.
     *
     * @return mappa tripId → {@link Trip} aggiornato con i ritardi
     */
    public static Map<String, Trip> fetchTripUpdates() {
        Map<String, Trip> updates = new HashMap<>();
        try (InputStream inputStream = new URL(TRIP_UPDATES).openStream()) {
            GtfsRealtime.FeedMessage feed = GtfsRealtime.FeedMessage.parseFrom(inputStream);
            Set<String> tripsId = modelManager.getTrips().keySet();
            for (GtfsRealtime.FeedEntity entity : feed.getEntityList()) {
                if (entity.hasTripUpdate()) {
                    GtfsRealtime.TripUpdate tripUpdate = entity.getTripUpdate();
                    String tripId = tripUpdate.getTrip().getTripId();
                    if (tripsId.contains(tripId)) {
                        Trip trip = modelManager.getTrips().get(tripId);
                        trip.clearStopTimesRealTime();
                        for (GtfsRealtime.TripUpdate.StopTimeUpdate stu : tripUpdate.getStopTimeUpdateList()) {
                            String stopId = stu.getStopId();
                            int stopSequence = stu.getStopSequence();
                            long arrivalTime = stu.hasArrival() ? stu.getArrival().getTime() : -1;
                            int delay = stu.hasArrival() && stu.getArrival().hasDelay() ? stu.getArrival().getDelay() : 0;
                            trip.addStopTimeRealTime(stopId, arrivalTime, stopSequence, delay);
                        }
                        updates.put(tripId, trip);
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return updates;
    }
}
