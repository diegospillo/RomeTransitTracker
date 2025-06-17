package controller;

import java.util.*;

import waypoint.MyWaypoint;

public class GeneralController {
	private int StateControl = 0;
	private javax.swing.Timer busTimer;
    private final LineController lineController;
    private final StopController stopController;
    private final BusController busController;
    private final MapController mapController;

    public GeneralController(LineController lineController,
                             StopController stopController,
                             BusController busController,
                             MapController mapController) {
        this.lineController = lineController;
        this.stopController = stopController;
        this.busController = busController;
        this.mapController = mapController;
    }

    public void visualizzaLinea(String idLinea,boolean direction) {
        // 1. Reset dati precedenti
    	clearAllWaypoints();

        // 2. Carica la linea e ottieni shape_id
        lineController.viewRouteByName(idLinea, direction); // true = centratura mappa, se previsto
        String shapeId = lineController.get_shape_id();

        // 3. Carica fermate per shape e aggiorna overlay
        stopController.loadStopWaypoint(shapeId);
        Set<MyWaypoint> waypoints = stopController.get_Waypoints();
        
        // 4. Carica bus TO DO
        busController.set_Route_id(lineController.get_route_id());
        
        mapController.initStopsWaypoint(shapeId, waypoints);

        // 5. Mostra linea sulla mappa
        lineController.showLinea();
        
        // 6. Init bus
        initBusUpdates();
    }
    
    public void visualizzaFermata(String stop_id) {
        // 1. Reset dati precedenti
    	clearAllWaypoints();

        // 2. Carica le informazioni della fermata
        stopController.viewStopById(stop_id);
        Set<MyWaypoint> waypoints = stopController.get_Waypoints();

        // 3. Aggiorna la mappa con la singola fermata
        mapController.initStopsWaypoint("", waypoints);

        // 4. Mostra la fermata nella vista laterale
        stopController.showFermata();
        
     	// 5. Stop bus
        stopBusUpdates();
    }
    
    private void clearAllWaypoints() {
    	Set<MyWaypoint> stopswaypoints = stopController.get_Waypoints();
    	mapController.clearStopsWaypoint(stopswaypoints);
    	Set<MyWaypoint> buswaypoints = busController.get_Waypoints();
    	mapController.clearBusWaypoint(buswaypoints);
    }
    
    public void Close() {
    	clearAllWaypoints();
    	stopBusUpdates();
    }
    
    private void initBusUpdates() {
        // Evita di creare più timer
        if (busTimer != null && busTimer.isRunning()) {
            return;
        }

        // Crea un nuovo Timer che gira ogni 5 secondi
        busTimer = new javax.swing.Timer(5000, e -> {
            // Rimuovi i vecchi waypoint
            Set<MyWaypoint> oldWaypoints = busController.get_Waypoints();
            mapController.clearBusWaypoint(oldWaypoints);

            // Aggiorna i dati dei bus
            busController.updateBusPositions();

            // Aggiungi i nuovi waypoint
            Set<MyWaypoint> newWaypoints = busController.get_Waypoints();
            mapController.initBusWaypoint(newWaypoints);
        });

        // Avvia il timer
        busTimer.start();
    }
    
    public void stopBusUpdates() {
        if (busTimer != null) {
            busTimer.stop();
            busTimer = null;
        }
    }

    public void setStateControl(int StateControl) {
    	this.StateControl = StateControl;
    }
    public int getStateControl() {
    	return StateControl;
    }

}
