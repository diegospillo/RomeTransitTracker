package controller;

import org.jxmapviewer.viewer.GeoPosition;

import net.GTFSFetcher;
import view.MainView;
import waypoint.MyWaypoint;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BusController{
	private final MainView mainView;
	private final Set<MyWaypoint> waypoints = new HashSet<>();

    public BusController(MainView mainView) {
    	this.mainView = mainView;
    }
    
    public void updateBusPositions(String route_id,boolean direction) {
        List<GeoPosition> busPositions = GTFSFetcher.fetchBusPositions(route_id,direction);
        loadBusWaypoint(busPositions);
    }

    private void loadBusWaypoint(List<GeoPosition> positions) {
    	waypoints.clear();
        for (GeoPosition pos : positions) {
        	MyWaypoint wayPoint = new MyWaypoint(positions.indexOf(pos),"bus", MyWaypoint.PointType.BUS, mainView.get_event(), new GeoPosition(pos.getLatitude(), pos.getLongitude()));
        	waypoints.add(wayPoint);
        }
    }
    
    public Set<MyWaypoint> get_Waypoints() {
		return waypoints;
	}
    
}

