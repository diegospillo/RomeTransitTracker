package service;

import java.util.*;

import model.ModelManager;

public class GTFSManager {
	
	private String CURRENT_DATE;
	
    private DataGTFS agency;
    private DataGTFS calendar;
    private DataGTFS calendarDates;
    private DataGTFS routes;
    private DataGTFS shapes;
    private DataGTFS stopTimes;
    private DataGTFS stops;
    private DataGTFS trips;

    private final static GTFSManager gtfsManager = new GTFSManager();
    
    public void loadData(String folder,String current_date) {
    	this.CURRENT_DATE = current_date;
    	DataGTFS[] dataGtfs = new DataGTFS[8];
        String[] gtfsNames = {"agency", "calendar", "calendar_dates", "routes", "shapes","stop_times", "stops", "trips"};
        for (int i = 0; i < gtfsNames.length; i++) {
            dataGtfs[i] = GTFSReader.readFile(folder,gtfsNames[i]);
        }
        loadGtfsData(dataGtfs);
    }

    private void loadGtfsData(DataGTFS[] dataGtfs) {
        if (dataGtfs == null || dataGtfs.length < 8) {
            throw new IllegalArgumentException("Invalid DataGtfs array: Must contain at least 8 elements.");
        }
        this.agency = dataGtfs[0];
        this.calendar = dataGtfs[1];
        this.calendarDates = dataGtfs[2];
        this.routes = dataGtfs[3];
        this.shapes = dataGtfs[4];
        this.stopTimes = dataGtfs[5];
        this.stops = dataGtfs[6];
        this.trips = dataGtfs[7];
    }
    
    public Set<String> getFilterService_idByDate() {
        List<String> values = new ArrayList<>();
        for (DataRow row : calendarDates.dataList()) {
            if (CURRENT_DATE.equals(row.get("date"))){
                values.add(row.get("service_id"));
            }
        }
        return new LinkedHashSet<>(values);
    }

    
    
    
    

    public List<String> getShapesId(DataGTFS dataGtfs) {
        List<String> shapesid = new ArrayList<>();
        for (DataRow row : dataGtfs.dataList()) {
            shapesid.add(row.get("shape_id"));
        }
        return shapesid;
    }

    public DataGTFS getFilterTripsByRoute_id(DataGTFS dataGtfs, Set<String> routes_id, Boolean direction, Set<String> service_id) {
        String dir_string = direction ? "0" : "1";
        DataGTFS trips_filteredDataGTFS = new DataGTFS(new ArrayList<>());
        for (DataRow row : dataGtfs.dataList()) {
            if (routes_id.contains(row.get("route_id")) && service_id.contains(row.get("service_id")) && dir_string.equals(row.get("direction_id"))){
                trips_filteredDataGTFS.add(row);
            }
        }
        return trips_filteredDataGTFS;
    }

    public Set<String>  getFilterTripsByShape_id(DataGTFS dataGtfs,String shape_id) {
        List<String> trips_filtered = new ArrayList<>();
        for (DataRow row : dataGtfs.dataList()) {
            if (shape_id.equals(row.get("shape_id"))){
                trips_filtered.add(row.get("trip_id"));
            }
        }
        return new LinkedHashSet<>(trips_filtered);
    }

//    public DataGTFS getStopTimesByStopName(String stopName) {
//        List<String> result = new ArrayList<>();
//        String stopId = stops.entrySet().stream()
//                .filter(entry -> entry.getValue().equalsIgnoreCase(stopName))
//                .map(Map.Entry::getKey)
//                .findFirst()
//                .orElse(null);
//
//        if (stopId == null) return result;
//
//        for (String[] stopTime : stopTimes) {
//            if (stopTime[3].equals(stopId)) {
//                result.add(stopTime[1]); // arrival_time
//            }
//        }
//        return result;
//    }

    public void setStopTimes(DataGTFS dataGtfs) {
        this.stopTimes = dataGtfs;
    }

    public DataRow getStopById(String stop_id) {
        for (DataRow row : this.stops.dataList()) {
            if (stop_id.equals(row.get("stop_id"))) {
                return row;
            }
        }
        return null;
    }

    public String getStopByName(String stop_name) {
        for (DataRow row : this.stops.dataList()) {
            if (stop_name.equals(row.get("stop_name"))) {
                return row.get("stop_id");
            }
        }
        return null;
    }

    public String getNameByStopId(String stop_id) {
        for (DataRow row : this.stops.dataList()) {
            if (stop_id.equals(row.get("stop_id"))) {
                return row.get("stop_name");
            }
        }
        return null;
    }
    
    public Map<String, String> getTripById(String tripId) {
        for (DataRow row : this.trips.dataList()) {
            if (tripId.equals(row.get("trip_id"))) {
                Map<String, String> tripInfo = new HashMap<>();
                tripInfo.put("route_id", row.get("route_id"));
                tripInfo.put("trip_headsign", row.get("trip_headsign"));
                tripInfo.put("direction_id", row.get("direction_id"));
                tripInfo.put("shape_id", row.get("shape_id"));
                tripInfo.put("service_id", row.get("service_id"));
                return tripInfo;
            }
        }
        return null; // oppure Collections.emptyMap();
    }


    public DataGTFS getAgency() {
        return agency;
    }

    public DataGTFS getCalendar() {
        return calendar;
    }

    public DataGTFS getCalendarDates() {
        return calendarDates;
    }

    public DataGTFS getRoutes() {
        return routes;
    }

    public DataGTFS getShapes() {
        return shapes;
    }

    public DataGTFS getStopTimes() {
        return stopTimes;
    }

    public DataGTFS getStops() {
        return stops;
    }

    public DataGTFS getTrips() {
        return trips;
    }
    
    public static GTFSManager getInstance() { 
    	return gtfsManager;
    }
}
