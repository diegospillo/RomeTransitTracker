package controller;

import java.util.*;
import data.JXMapViewerCustom;
import data.RoutingService;
import service.DataGTFS;
import service.GTFSManager;
import view.MainView;
import org.jxmapviewer.viewer.WaypointPainter;
import waypoint.MyWaypoint;
import waypoint.WaypointRender;

public class MapController {
	
	private final MainView mainView;
	private final JXMapViewerCustom jXMapViewer;
	private final GTFSManager gtfsManager;
	
	public MapController(MainView mainView) {
		this.jXMapViewer = mainView.getMapView().getMapViewer();
		this.mainView = mainView;
		this.gtfsManager = GTFSManager.getInstance();
	}
	
	public void initStopspoint(String shapes_id,Set<MyWaypoint> waypoints) {
        System.out.println("initStopspoint");
        WaypointPainter<MyWaypoint> wp = new WaypointRender();
        wp.setWaypoints(waypoints);
        jXMapViewer.setOverlayPainter(wp);
        for (MyWaypoint d : waypoints) {
            jXMapViewer.add(d.getButton());
        }

        DataGTFS routingData = RoutingService.routing(gtfsManager.getShapes(),shapes_id);

        jXMapViewer.setRoutingData(routingData);
    }
	
	public void clearWaypoint(Set<MyWaypoint> waypoints) {
        for (MyWaypoint d : waypoints) {
            jXMapViewer.remove(d.getButton());
        }
        mainView.get_modelFermate().removeAllElements();
        waypoints.clear();
    }
}
