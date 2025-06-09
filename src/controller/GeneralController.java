package controller;

import java.util.*;

import waypoint.MyWaypoint;

public class GeneralController {
	private int StateControl = 0;
    private final LineController lineController;
    private final StopController stopController;
    private final MapController mapController;

    public GeneralController(LineController lineController,
                             StopController stopController,
                             MapController mapController) {
        this.lineController = lineController;
        this.stopController = stopController;
        this.mapController = mapController;
    }

    public void visualizzaLinea(String idLinea,boolean direction) {
        // 1. Reset dati precedenti
        Set<MyWaypoint> waypoints = stopController.get_Waypoints();
        mapController.clearWaypoint(waypoints);

        // 2. Carica la linea e ottieni shape_id
        lineController.viewRouteByName(idLinea, direction); // true = centratura mappa, se previsto
        String shapeId = lineController.get_shape_id();

        // 3. Carica fermate per shape e aggiorna overlay
        stopController.loadStopWaypoint(shapeId);
        waypoints = stopController.get_Waypoints();
        mapController.initWaypoint(shapeId, waypoints);

        // 4. Mostra linea sulla mappa
        lineController.showLinea();
    }
    
    public void visualizzaFermata(String stop_id) {
    	
        // 1. Reset dati precedenti
        Set<MyWaypoint> waypoints = stopController.get_Waypoints();
        mapController.clearWaypoint(waypoints);

        // 2. Carica le informazioni della fermata
        stopController.viewStopById(stop_id);
        waypoints = stopController.get_Waypoints();

        // 3. Aggiorna la mappa con la singola fermata
        mapController.initWaypoint("", waypoints);

        // 4. Mostra la fermata nella vista laterale
        stopController.showFermata();
    }

    public void setStateControl(int StateControl) {
    	this.StateControl = StateControl;
    }
    public int getStateControl() {
    	return StateControl;
    }

}
