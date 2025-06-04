package controller;

import java.util.*;
import waypoint.MyWaypoint;

public class GeneralController {
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
        mapController.initStopspoint(shapeId, waypoints);

        // 4. Mostra linea sulla mappa
        lineController.showLinea();
    }
    
    public void visualizzaFermata(String stop_id) {
    	
    }
}
