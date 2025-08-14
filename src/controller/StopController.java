package controller;

import java.util.*;
import java.util.stream.Collectors;

import org.jxmapviewer.viewer.GeoPosition;

import model.ModelManager;
import model.Stop;
import model.StopTime;
import model.Trip;
import service.DataRow;
import service.GTFSManager;
import view.MainView;
import waypoint.MyWaypoint;
import net.GTFSFetcher;

public class StopController {
	 private final GTFSManager gtfsManager;
	 private final ModelManager modelManager;
     private final MainView mainView;
     private Set<MyWaypoint> waypoints = new HashSet<>();
     private Set<MyWaypoint> localWaypoints = new HashSet<>();
     private String stopId;
     private String stopName;
     private Set<String> trips_id;
     private Map<String,Trip> trips;
     private final List<String> allStops;
     private Map<String,String> stopsStaticLabel = new LinkedHashMap<>();
     private Map<String,String> localStopsStaticLabel = new LinkedHashMap<>();
     private Map<String,String> lineeStaticLabel = new LinkedHashMap<>();
     private Map<String,String> localLineeStaticLabel = new LinkedHashMap<>();
     private Map<String,String> timesLabel = new LinkedHashMap<>();
     private List<String> stopsIdByTrips = new ArrayList<>();
     private List<String> localStopsIdByTrips = new ArrayList<>();
     private boolean selectedTrip = false;
     private static Map<String, StopTime> stopTimesByTrips = new LinkedHashMap<>();
	
     public StopController(MainView mainView){
         this.gtfsManager = GTFSManager.getInstance();
         this.modelManager = ModelManager.getInstance();
         this.mainView = mainView;
         this.allStops = buildAllStops();
     }

     private List<String> buildAllStops(){
         Set<String> uniqueStops = new HashSet<>();
         for (DataRow row : gtfsManager.getStops().dataList()) {
                 String name = row.get("stop_name");
                 String my_stop_id = row.get("stop_id");
                 String item = "(" + my_stop_id + ") " + name;
                 if(my_stop_id != null){
                         uniqueStops.add(item);
                 }
         }
         List<String> sortedStops = new ArrayList<>(uniqueStops);
         Collections.sort(sortedStops);
         return sortedStops;
     }
	
	public void loadStopWaypoint(boolean isSelectedTrip) {
		localWaypoints.clear();
		localStopsIdByTrips.clear();
		localStopsStaticLabel.clear();
		mainView.get_modelFermate().clear();
		this.selectedTrip = isSelectedTrip;
		if(!isSelectedTrip) {
			stopTimesByTrips = ModelManager.getNextStopTimeForStop(trips);
		}
		else {
			stopTimesByTrips = ModelManager.getStopTimeForTrip(trips);
		}
		for (Map.Entry<String, StopTime> entry: stopTimesByTrips.entrySet()) {
			StopTime stopTime = entry.getValue();
			Stop stop = modelManager.getStops().get(stopTime.getStopId());
			String stopName = stop.getName();
			String stopId = stop.getStopId();
			double lat = stop.getLat();
			double lon = stop.getLon();
			String label = "";
			if(isSelectedTrip && stopTime.getIsPassed()) {
				label += "[PASSED]" + stopTime.getLabelLinea(stopName,false);
			}
			else {
				label += stopTime.getLabelLinea(stopName,false);
			}
			localStopsIdByTrips.add(stopId);
			localStopsStaticLabel.put(stopId, label);
			System.out.println(label + " " + stopTime.getStopSequence());
			mainView.get_modelFermate().addElement(label);
			MyWaypoint wayPoint = new MyWaypoint(mainView.get_modelFermate().indexOf(label),stopName, MyWaypoint.PointType.STOPS, mainView.get_event(), new GeoPosition(lat, lon));
			localWaypoints.add(wayPoint);
		}
    }
	
	public void viewStopById(String stop_id) {
		mainView.get_modelLinee().clear();
		localWaypoints.clear();
		localLineeStaticLabel.clear();
        Stop stop = modelManager.getStops().get(stop_id);
        this.stopId = stop.getStopId();
        this.stopName = stop.getName();

        MyWaypoint wayPoint = new MyWaypoint(0, stopName, MyWaypoint.PointType.STOPS,mainView.get_event(),new GeoPosition(stop.getLat(),stop.getLon()));
        localWaypoints.add(wayPoint);

        Map<String, StopTime> stopTimes = modelManager.getStopTimesRouteByStopId(stop_id);
        
        for (Map.Entry<String, StopTime> entry: stopTimes.entrySet()) {
			StopTime stopTime = entry.getValue();
			String routeId = entry.getKey();
			String tripId = stopTime.getTripId();
            Trip trip = modelManager.getTrips().get(tripId);
			String headsign = trip.getHeadsign();
			String label = stopTime.getLabelFermata(routeId, headsign, false);
			localLineeStaticLabel.put(routeId, label);
			System.out.println(label);
			mainView.get_modelLinee().addElement(label);
		}
    }
	
	public void viewTimesByStop(){
		mainView.get_modelOrari().clear();
		timesLabel.clear();
        int index = mainView.get_fermateList().getSelectedIndex();
        String stopIdSelected = stopsIdByTrips.get(index);
        Stop stop = modelManager.getStops().get(stopIdSelected);
		String stopName = stop.getName();
		this.stopId = stop.getStopId();
		
        List<StopTime> stopTimes = ModelManager.getAllStopTimeForStop(trips,stopIdSelected);
        for (StopTime st : stopTimes){
        	String tripId = st.getTripId();
        	String routeId = modelManager.getTrips().get(tripId).getRouteId();
        	String headsign = modelManager.getTrips().get(tripId).getHeadsign();
        	String label = routeId + " " + headsign + " " + unixToTime(st.getArrivalEpoch());
        	timesLabel.put(tripId,label);
        	mainView.get_modelOrari().addElement(label);
        }
        showOrariFermata(stopName);
	}

    public void showFermata() {
        mainView.get_lblLinea().setText(stopName);
        mainView.get_lblDettagli().setText("Fermata " + stopId);
        mainView.get_lblDescription().setText("Prossimi arrivi");
        mainView.get_btnInvertiDirezione().setVisible(false);
        mainView.get_btnLive().setVisible(false);
        mainView.get_btnIndietro().setVisible(false);
        mainView.get_btnMoreInfo().setVisible(false);
        mainView.get_btnCloseSidePanel().setVisible(true);
        mainView.get_lblDettagli().setVisible(true);
        mainView.get_scrollPanel().setViewportView(mainView.get_lineeList());
        mainView.adjustSidePanelWidth();
    }
    
    public void showOrariFermata(String stopName){     
        mainView.get_orariList().setModel(mainView.get_modelOrari());
        mainView.get_lblLinea().setText(stopName);
        mainView.get_lblDescription().setText("Tutti gli orari");
        mainView.get_btnInvertiDirezione().setVisible(false);
        mainView.get_btnLive().setVisible(false);
        mainView.get_btnIndietro().setVisible(true);
        mainView.get_btnMoreInfo().setVisible(true);
        mainView.get_btnCloseSidePanel().setVisible(false);
        mainView.get_lblDettagli().setVisible(false);
        mainView.get_scrollPanel().setViewportView(mainView.get_orariList());
    }
    
    public void setTrips(Map<String,Trip> trips) {
    	this.trips = trips;
    }
    
    public void setTrips_id(Set<String> trips_id) {
    	this.trips_id = trips_id;
    }
    
    public Set<String>  getTrips_id() {
    	return trips_id;
    }
    
    public void setCurrentLocalVariables() {
    	this.waypoints = new HashSet<>(localWaypoints);
    	this.stopsIdByTrips = new ArrayList<>(localStopsIdByTrips);
    	this.stopsStaticLabel = new LinkedHashMap<>(localStopsStaticLabel);
    }
    public void setCurrentLocalWaypoints() {
    	this.waypoints = new HashSet<>(localWaypoints);
    }
    public void setCurrentLocalStopsStaticLabel() {
    	this.stopsStaticLabel = new LinkedHashMap<>(localStopsStaticLabel);
    }
    public void setCurrentLocalLineeStaticLabel() {
    	this.lineeStaticLabel = new LinkedHashMap<>(localLineeStaticLabel);
    }
	
	public Set<MyWaypoint> get_Waypoints() {
		return waypoints;
	}
	
	public String get_stopId() {
		return stopId;
	}
	
	public boolean getSelectedTrip() {
		return selectedTrip;
	}
	
	public List<String> getStopsOf(String text){
        final String lowered = text.toLowerCase();
        return allStops.stream()
                .filter(item -> item.toLowerCase().contains(lowered))
                .collect(Collectors.toList());
	}
	
	public void updateTripUpdatesByLinea() {
		System.out.println("updateTripUpdatesByLinea");
		mainView.get_modelFermate().clear();
		GTFSFetcher.fetchTripUpdates();
		Map<String, StopTime> stopTimesRealTimeByTrips = ModelManager.getNextStopTimeRealTimeForStop(trips);
		Map<String,String> stopsLabel = new LinkedHashMap<>(stopsStaticLabel);
		for (Map.Entry<String, StopTime> entry: stopTimesRealTimeByTrips.entrySet()) {
			StopTime stopTime = entry.getValue();
			Stop stop = modelManager.getStops().get(stopTime.getStopId());
			String stopId = stop.getStopId();
			String stopName = stop.getName();
			String label = "[LIVE]" + stopTime.getLabelLinea(stopName,true);
			System.out.println(label);
			if(stopsStaticLabel.keySet().contains(stopId)){
				stopsLabel.put(stopId, label);
			}
		}
		for(String label : stopsLabel.values()) {
			mainView.get_modelFermate().addElement(label);
		}

	    mainView.get_scrollPanel().setViewportView(mainView.get_fermateList());
	}

	
	public void updateTripUpdatesByFermata() {
		System.out.println("updateTripUpdatesByFermata");
		mainView.get_modelLinee().clear();
		GTFSFetcher.fetchTripUpdates();
		Map<String, StopTime> stopTimes = modelManager.getStopTimesRealTimeRouteByStopId(stopId);
		Map<String,String> lineeLabel = new LinkedHashMap<>(lineeStaticLabel);
		
        for (Map.Entry<String, StopTime> entry: stopTimes.entrySet()) {
			StopTime stopTime = entry.getValue();
			String routeId = entry.getKey();
			String tripId = stopTime.getTripId();
            Trip trip = modelManager.getTrips().get(tripId);
			String headsign = trip.getHeadsign();
			String label = stopTime.getLabelFermata(routeId, headsign, true) + "[LIVE]";
			System.out.println(label);
			if(lineeStaticLabel.keySet().contains(routeId)){
				lineeLabel.put(routeId, label);
			}
		}
		
		for(String label : lineeLabel.values()) {
			mainView.get_modelLinee().addElement(label);
		}
		

	    mainView.get_scrollPanel().setViewportView(mainView.get_lineeList());
	}
	
	private String unixToTime(long epochSeconds) {
	    if (epochSeconds <= 0) return "N/D";
	    Date date = new Date(epochSeconds * 1000);
	    return new java.text.SimpleDateFormat("HH:mm").format(date);
	}
}
