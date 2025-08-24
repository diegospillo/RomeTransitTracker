package model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import service.DataGTFS;
import service.DataRow;
import service.GTFSManager;
import service.GTFSReader;

/**
 * Gestore centrale del modello dati. Fornisce metodi per caricare e filtrare
 * informazioni su linee, fermate, viaggi e orari del sistema di trasporto.
 */
public class ModelManager {
	
	private final static ModelManager modelManager = new ModelManager();
	private final GTFSManager gtfsManager = GTFSManager.getInstance();
	
	private Map<String,Route> routes = new HashMap<>();
	private Map<String,Stop> stops = new HashMap<>();
	private Map<String,Trip> trips = new HashMap<>();
	
        /**
         * Carica i dati GTFS nelle strutture interne.
         */
        public void loadData() {
        SetStops(gtfsManager.getStops());
        SetRoutes(gtfsManager.getRoutes());
        SetTrips(gtfsManager.getTrips(),gtfsManager.getFilterService_idByDate());
        GTFSReader.setStop_times(modelManager);
    }
	
        /** Inserisce una nuova linea. */
        private void putRoute(DataRow data) {
                routes.put(data.get("route_id"), new Route(data));
        }
        /** Inserisce una nuova fermata. */
        private void putStop(DataRow data) {
                stops.put(data.get("stop_id"), new Stop(data));
        }
        /** Inserisce un nuovo viaggio. */
        private void putTrip(DataRow data) {
                trips.put(data.get("trip_id"), new Trip(data));
        }
	
	// Setter
        /**
         * Popola le fermate dal dataset specificato.
         */
        public void SetStops(DataGTFS data){
        for (DataRow row : data.dataList()) {
                putStop(row);
        }
    }

    /**
     * Popola le linee dal dataset specificato.
     */
    public void SetRoutes(DataGTFS data){
        for (DataRow row : data.dataList()) {
                putRoute(row);
        }
    }

    /**
     * Popola i viaggi filtrando per service_id valido.
     *
     * @param data dataset dei trip
     * @param service_id identificativi di servizio
     */
    public void SetTrips(DataGTFS data,Set<String> service_id){
        for (DataRow row : data.dataList()) {
            if(service_id.contains(row.get("service_id"))) {
                putTrip(row);
            }
        }
    }
    
    // Getter
    /** Restituisce tutte le linee caricate. */
    public Map<String,Route> getRoutes(){
        return routes;
    }
    /** Restituisce tutte le fermate caricate. */
    public Map<String,Stop> getStops(){
        return stops;
    }
    /** Restituisce tutti i viaggi caricati. */
    public Map<String,Trip> getTrips(){
        return trips;
    }
    
    // Filter
    /**
     * Filtra i viaggi per linea e direzione.
     */
    public Map<String,Trip> getTripsByRouteId(String routeId, Boolean direction){
        Map<String,Trip> my_trips = new HashMap<>();
        for (Map.Entry<String, Trip> entry: trips.entrySet()) {
                if(entry.getValue().getRouteId().equals(routeId) && entry.getValue().getDirection().equals(direction)) {
                        my_trips.put(entry.getKey(),entry.getValue());
                }
        }
        return my_trips;
    }
    
    /**
     * Restituisce gli shape_id associati a un insieme di viaggi.
     */
    public List<String> getShapeIdsByTrips(Set<String> localTripIds) {
        List<String> shapeIds = new ArrayList<>();
        for (String tripId : localTripIds) {
            Trip trip = trips.get(tripId);
            if (trip != null && trip.getShapeId() != null) {
                shapeIds.add(trip.getShapeId());
            }
        }
        return shapeIds;
    }
    
    /**
     * Filtra i viaggi in base allo {@code shape_id} specificato.
     */
    public static Map<String, Trip> getTripsByShapeId(Map<String, Trip> trips,String shape_id) {
        Map<String,Trip> my_trips = new HashMap<>();
        for (Map.Entry<String, Trip> entry: trips.entrySet()) {
                if(entry.getValue().getShapeId().equals(shape_id)) {
                        my_trips.put(entry.getKey(),entry.getValue());
                }
        }
        return my_trips;
    }
    
    public static Map<String, StopTime> getStopTimeForTrip(Map<String, Trip> trips){
    	long now = Instant.now().getEpochSecond();
    	Map<String, StopTime> result = new LinkedHashMap<>();

        for (Trip trip : trips.values()) {
            for (StopTime st : trip.GetStopTimes()) {
                String stopId = st.getStopId();
                long effectiveArrival = st.getArrivalEpoch() + st.getDelaySeconds();
                if (effectiveArrival < now) {
                	st.isPassed = true;
                }
                result.put(stopId, st);
            }
        }
        

        Map<String, StopTime> sortedResult = result.entrySet()
        	    .stream()
        	    .sorted(Comparator.comparingInt(entry -> entry.getValue().stopSequence))
        	    .collect(Collectors.toMap(
        	        Map.Entry::getKey,
        	        Map.Entry::getValue,
        	        (e1, e2) -> e1,
        	        LinkedHashMap::new
        	    ));


        return sortedResult;
    }
    
    public static Map<String, StopTime> getNextStopTimeForStop(Map<String, Trip> trips) {
        long now = Instant.now().getEpochSecond();
        Map<String, StopTime> result = new LinkedHashMap<>();

        for (Trip trip : trips.values()) {
            for (StopTime st : trip.GetStopTimes()) {
                String stopId = st.getStopId();
                long effectiveArrival = st.getArrivalEpoch() + st.getDelaySeconds();

                // Solo se è un arrivo futuro
                if (effectiveArrival >= now) {
                    // Se non c'è ancora nulla per questo stop oppure è più vicino
                    if (!result.containsKey(stopId) ||
                        effectiveArrival < result.get(stopId).getArrivalEpoch() + result.get(stopId).getDelaySeconds()) {
                        result.put(stopId, st);
                    }
                }
            }
        }
        
        Map<String, StopTime> sortedResult = result.entrySet()
        	    .stream()
        	    .sorted(Comparator.comparingInt(entry -> entry.getValue().stopSequence))
        	    .collect(Collectors.toMap(
        	        Map.Entry::getKey,
        	        Map.Entry::getValue,
        	        (e1, e2) -> e1,
        	        LinkedHashMap::new
        	    ));


        return sortedResult;
    }
    
    public static Map<String, StopTime> getNextStopTimeRealTimeForStop(Map<String, Trip> trips) {
        long now = Instant.now().getEpochSecond();
        Map<String, StopTime> result = new LinkedHashMap<>();
        for (Trip trip : trips.values()) {
            for (StopTime st : trip.GetStopTimesRealTime()) {
                String stopId = st.getStopId();
                long effectiveArrival = st.getArrivalEpoch() + st.getDelaySeconds();
                // Solo se è un arrivo futuro
                if (effectiveArrival >= now) {
                    // Se non c'è ancora nulla per questo stop oppure è più vicino
                    if (!result.containsKey(stopId) ||
                        effectiveArrival < result.get(stopId).getArrivalEpoch() + result.get(stopId).getDelaySeconds()) {
                        result.put(stopId, st);
                    }
                }
            }
        }
        return result;
    }
    
    public static List<StopTime> getAllStopTimeForStop(Map<String, Trip> trips,String stopIdSelected) {
        List<StopTime> result = new ArrayList<>();

        for (Trip trip : trips.values()) {
            for (StopTime st : trip.GetStopTimes()) {
                String stopId = st.getStopId();
                if(stopId.equals(stopIdSelected)) {
                	result.add(st);
                }
            }
        }
        result.sort(Comparator.comparingLong(st -> st.arrivalEpoch));
        return result;
    } 
    
    public static List<StopTime> getAllStopTimeRealTimeForStop(Map<String, Trip> trips,String stopIdSelected) {
        List<StopTime> result = new ArrayList<>();

        for (Trip trip : trips.values()) {
            for (StopTime st : trip.GetStopTimesRealTime()) {
                String stopId = st.getStopId();
                if(stopId.equals(stopIdSelected)) {
                	result.add(st);
                }
            }
        }
        result.sort(Comparator.comparingLong(st -> st.arrivalEpoch));
        return result;
    } 

    
    /**
     * Restituisce tutti gli StopTime che fanno riferimento a uno stop_id dato,
     * cercando in tutti i Trip forniti.
     *
     * @param stopId l'identificativo della fermata
     * @param trips mappa trip_id → Trip
     * @return lista di StopTime relativi a quella fermata
     */
    private List<StopTime> getStopTimesByStopId(String stopId) {
        List<StopTime> result = new ArrayList<>();

        for (Map.Entry<String, Trip> entry : trips.entrySet()) {
        	Trip trip = entry.getValue();
            for (StopTime st : trip.GetStopTimes()) {
                if (st.getStopId().equals(stopId)) {
                    result.add(st);
                }
            }
        }

        return result;
    }
    
    private List<StopTime> getStopTimesRealTimeByStopId(String stopId) {
        List<StopTime> result = new ArrayList<>();

        for (Map.Entry<String, Trip> entry : trips.entrySet()) {
        	Trip trip = entry.getValue();
            for (StopTime st : trip.GetStopTimesRealTime()) {
                if (st.getStopId().equals(stopId)) {
                    result.add(st);
                }
            }
        }

        return result;
    }
    
    public Map<String, StopTime> getStopTimesRouteByStopId(String stopId) {
    	long now = Instant.now().getEpochSecond();
        Map<String, StopTime> result = new LinkedHashMap<>();
    	List<StopTime> AllstopTimes = getStopTimesByStopId(stopId);
        Map<String,List<StopTime>> stopRoutes = new HashMap<>();
        
        for (StopTime stopTime : AllstopTimes) {
            String tripId = stopTime.getTripId();
            Trip trip = modelManager.getTrips().get(tripId);
            String routeId = trip.getRouteId();
            if(stopRoutes.containsKey(routeId)){
            	stopRoutes.get(routeId).add(stopTime);
            }
            else{
            	List<StopTime> new_list = new ArrayList<>();
                new_list.add(stopTime);
                stopRoutes.put(routeId, new_list);
            }
        }
        
        for (Map.Entry<String, List<StopTime>> entry : stopRoutes.entrySet()) {
        	List<StopTime> stopTimes = entry.getValue();
        	String routeId = entry.getKey();
        	for (StopTime st : stopTimes) {
                long effectiveArrival = st.getArrivalEpoch() + st.getDelaySeconds();

                // Solo se è un arrivo futuro
                if (effectiveArrival >= now) {
                    // Se non c'è ancora nulla per questo stop oppure è più vicino
                    if (!result.containsKey(routeId) ||
                        effectiveArrival < result.get(routeId).getArrivalEpoch() + result.get(routeId).getDelaySeconds()) {
                        result.put(routeId, st);
                    }
                }
            }
        }
        return result;
    }
    
    public Map<String, StopTime> getStopTimesRealTimeRouteByStopId(String stopId) {
    	long now = Instant.now().getEpochSecond();
        Map<String, StopTime> result = new LinkedHashMap<>();
    	List<StopTime> AllstopTimes = getStopTimesRealTimeByStopId(stopId);
        Map<String,List<StopTime>> stopRoutes = new HashMap<>();
        
        for (StopTime stopTime : AllstopTimes) {
            String tripId = stopTime.getTripId();
            Trip trip = modelManager.getTrips().get(tripId);
            String routeId = trip.getRouteId();
            //System.out.println(stopTime.getOrario() + stopTime.getDelayMinutes());
            if(stopRoutes.containsKey(routeId)){
            	stopRoutes.get(routeId).add(stopTime);
            }
            else{
            	List<StopTime> new_list = new ArrayList<>();
                new_list.add(stopTime);
                stopRoutes.put(routeId, new_list);
            }
        }
        
        for (Map.Entry<String, List<StopTime>> entry : stopRoutes.entrySet()) {
        	List<StopTime> stopTimes = entry.getValue();
        	String routeId = entry.getKey();
        	for (StopTime st : stopTimes) {
                long effectiveArrival = st.getArrivalEpoch() + st.getDelaySeconds();

                // Solo se è un arrivo futuro
                if (effectiveArrival >= now) {
                    // Se non c'è ancora nulla per questo stop oppure è più vicino
                    if (!result.containsKey(routeId) ||
                        effectiveArrival < result.get(routeId).getArrivalEpoch() + result.get(routeId).getDelaySeconds()) {
                        result.put(routeId, st);
                    }
                }
            }
        }
        return result;
    }


    
    public static ModelManager getInstance() { 
    	return modelManager;
    }
}
