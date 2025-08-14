package controller;

import org.jxmapviewer.viewer.GeoPosition;

import net.GTFSFetcher;
import net.ConnectivityUtil;
import view.MainView;
import waypoint.MyWaypoint;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class BusController{
	private final MainView mainView;
	private final Set<MyWaypoint> waypoints = new HashSet<>();

    public BusController(MainView mainView) {
    	this.mainView = mainView;
    }
    
    public void updateBusPositions(String route_id,boolean direction) {
    	ConnectivityUtil.checkConnectivityAndSwitchMode();
    	if (ConnectivityUtil.isOfflineMode()) {
            waypoints.clear();
            return;
        }
    	System.out.println("updateBusPositions");
        Map<String,GeoPosition> busPositions = GTFSFetcher.fetchBusPositions(route_id,direction);
        loadBusWaypoint(busPositions);
    }

    private void loadBusWaypoint(Map<String,GeoPosition> positions) {
    	waypoints.clear();
    	int i = 0;
        for (Map.Entry<String,GeoPosition> entry : positions.entrySet()) {
        	String tripId = entry.getKey();
        	GeoPosition pos = entry.getValue();
        	MyWaypoint wayPoint = new MyWaypoint(i,tripId, MyWaypoint.PointType.BUS, mainView.get_event(), new GeoPosition(pos.getLatitude(), pos.getLongitude()));
        	waypoints.add(wayPoint);
        	i++;
        }
    }
    
    public Set<MyWaypoint> get_Waypoints() {
		return waypoints;
	}
    
}

