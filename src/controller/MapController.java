package controller;

import java.awt.Color;
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
	
	public void initStopsWaypoint(String route_id, String shapes_id,Set<MyWaypoint> waypoints) {
        System.out.println("initStopsWaypoint");
        jXMapViewer.setStopPainter(waypoints);
        for (MyWaypoint d : waypoints) {
            jXMapViewer.add(d.getButton());
            jXMapViewer.setComponentZOrder(d.getButton(), 3);
        }
        jXMapViewer.setComponentZOrder(mainView.get_topBar(), 0);
        jXMapViewer.setComponentZOrder(mainView.get_sidePanel(), 0);
        DataGTFS routingData = RoutingService.routing(gtfsManager.getShapes(),shapes_id);
        Color colorRouteType = RoutingService.getColorRoutingType(route_id);

        jXMapViewer.setRoutingData(routingData,colorRouteType);
    }
	
	public void initBusWaypoint(Set<MyWaypoint> waypoints) {
        System.out.println("initBusWaypoint");
        jXMapViewer.setBusPainter(waypoints);
        for (MyWaypoint d : waypoints) {
            jXMapViewer.add(d.getButton());
            jXMapViewer.setComponentZOrder(d.getButton(), 2);
        }
        jXMapViewer.setComponentZOrder(mainView.get_topBar(), 0);
        jXMapViewer.setComponentZOrder(mainView.get_sidePanel(), 0);
    }
	
	public void initPingWaypoint(Set<MyWaypoint> waypoints) {
		System.out.println("initPingWaypoint");
        jXMapViewer.setPingPainter(waypoints);
        for (MyWaypoint d : waypoints) {
            jXMapViewer.add(d.getButton());
            jXMapViewer.setComponentZOrder(d.getButton(), 1);
        }
        jXMapViewer.setComponentZOrder(mainView.get_topBar(), 0);
        jXMapViewer.setComponentZOrder(mainView.get_sidePanel(), 0);
	}
	
	public void clearStopsWaypoint(Set<MyWaypoint> waypoints) {
        for (MyWaypoint d : waypoints) {
            jXMapViewer.remove(d.getButton());
        }
        mainView.get_modelFermate().clear();
        mainView.get_modelLinee().clear();
        jXMapViewer.clearRoutingData();
        waypoints.clear();
    }
	
	public void clearBusWaypoint(Set<MyWaypoint> waypoints) {
        for (MyWaypoint d : waypoints) {
            jXMapViewer.remove(d.getButton());
        }
        waypoints.clear();
    }
	
	public void clearPingWaypoint(Set<MyWaypoint> waypoints) {
        for (MyWaypoint d : waypoints) {
            jXMapViewer.remove(d.getButton());
        }
        waypoints.clear();
    }
}
