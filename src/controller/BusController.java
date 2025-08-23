package controller;

import org.jxmapviewer.viewer.GeoPosition;

import model.ModelManager;
import model.Route;
import model.Stop;
import model.StopTime;
import net.GTFSFetcher;
import net.ConnectivityUtil;
import view.MainView;
import waypoint.MyWaypoint;
import waypoint.MyWaypoint.PointType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BusController{
	private final MainView mainView;
	private final ModelManager modelManager;
	private final Set<MyWaypoint> waypoints = new HashSet<>();
	private boolean IsSelected = false;

    public BusController(MainView mainView) {
    	this.mainView = mainView;
    	this.modelManager = ModelManager.getInstance();
    }
    
    public void updateStaticBusPositions(String route_id,Map<String,StopTime> currentTrips) {
    	System.out.println("updateStaticBusPositions");
    	Map<String,GeoPosition> staticBusPositions = new HashMap<>();
    	for(Map.Entry<String, StopTime> entry:currentTrips.entrySet()) {
    		String tripId = entry.getKey();
    		StopTime st = entry.getValue();
    		Stop stop = modelManager.getStops().get(st.getStopId());
    		double lat = stop.getLat();
            double lon = stop.getLon();
            System.out.println(tripId + " " + lat + " " + lon);
    		staticBusPositions.put(tripId,new GeoPosition(lat, lon));
    	}
    	loadBusWaypoint(staticBusPositions,modelManager.getRoutes().get(route_id).getPointType());
    }
    
    public void updateBusPositions(String route_id,boolean direction,Map<String,StopTime> currentTrips) {
    	System.out.println("updateBusPositions");
        Map<String,GeoPosition> busPositions = GTFSFetcher.fetchBusPositions(route_id,direction);
        if(IsSelected) {
        	String tripId = currentTrips.keySet().toArray()[0].toString();
        	GeoPosition gp = busPositions.get(tripId);
        	if(gp!=null) {
        		busPositions = new HashMap<>();
        		busPositions.put(tripId, gp);
        	}
        }
        loadBusWaypoint(busPositions,modelManager.getRoutes().get(route_id).getPointType());
    }

    private void loadBusWaypoint(Map<String,GeoPosition> positions,PointType pointType) {
    	waypoints.clear();
    	int i = 0;
        for (Map.Entry<String,GeoPosition> entry : positions.entrySet()) {
        	String tripId = entry.getKey();
        	GeoPosition pos = entry.getValue();
        	if(pos!=null) {
	        	MyWaypoint wayPoint = new MyWaypoint(i,tripId, pointType, mainView.get_event(), new GeoPosition(pos.getLatitude(), pos.getLongitude()));
	        	waypoints.add(wayPoint);
        	}
        	i++;
        }
    }
    
    public Set<MyWaypoint> get_Waypoints() {
		return waypoints;
	}
    
    public void setIsSelected(boolean state) {
    	this.IsSelected = state;
    }
    
}

