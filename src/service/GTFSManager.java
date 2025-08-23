package service;

import java.util.*;

import model.ModelManager;

public class GTFSManager {
	
	private String CURRENT_DATE;
	
    private DataGTFS agency;
    private DataGTFS calendarDates;
    private DataGTFS routes;
    private DataGTFS shapes;
    private DataGTFS stopTimes;
    private DataGTFS stops;
    private DataGTFS trips;

    private final static GTFSManager gtfsManager = new GTFSManager();
    
    public void loadData(String folder,String current_date) {
    	this.CURRENT_DATE = current_date;
    	DataGTFS[] dataGtfs = new DataGTFS[7];
        String[] gtfsNames = {"agency", "calendar_dates", "routes", "shapes","stop_times", "stops", "trips"};
        for (int i = 0; i < gtfsNames.length; i++) {
            dataGtfs[i] = GTFSReader.readFile(folder,gtfsNames[i]);
        }
        loadGtfsData(dataGtfs);
    }

    private void loadGtfsData(DataGTFS[] dataGtfs) {
        if (dataGtfs == null || dataGtfs.length < 7) {
            throw new IllegalArgumentException("Invalid DataGtfs array: Must contain at least 7 elements.");
        }
        this.agency = dataGtfs[0];
        this.calendarDates = dataGtfs[1];
        this.routes = dataGtfs[2];
        this.shapes = dataGtfs[3];
        this.stopTimes = dataGtfs[4];
        this.stops = dataGtfs[5];
        this.trips = dataGtfs[6];
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

    public DataGTFS getAgency() {
        return agency;
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
