package controller;

import java.util.*;

import model.StopTime;
import model.Trip;
import net.ConnectivityUtil;
import waypoint.MyWaypoint;

public class GeneralController {
	private int StateControl = 0;
	private int CurrentSate = 0;
	private boolean StopSelected = false;
	private final int DELAY = 30000;
	private javax.swing.Timer fermataTimer;
	private javax.swing.Timer lineaTimer;
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

    public void visualizzaLinea(String routeId, boolean direction) {
        // 1. Reset dati precedenti
    	Close();
    	CurrentSate = 0;
    	busController.setIsSelected(false);
        // 2. Carica la linea e ottieni shape_id
        lineController.viewRouteByName(routeId, direction); // true = centratura mappa, se previsto
        
        String shapeId = lineController.get_shape_id();
        Set<String> trips_id = lineController.get_trips().keySet();

        // 3. Carica fermate per shape e aggiorna overlay
        stopController.setTrips(lineController.get_trips());
        stopController.loadStopWaypoint(false);
        stopController.setCurrentLocalVariables();
        
        lineController.setCurrentTrips(stopController.getStopTimesByTrips());
        
        Set<MyWaypoint> waypoints = stopController.get_Waypoints();      
        mapController.initStopsWaypoint(lineController.get_route_id(),shapeId, waypoints);

        // 4. Mostra linea sulla mappa
        lineController.showLinea(false);
        
        // 5. Carica dati realtime
        stopController.setTrips_id(trips_id);
        
        // 6. Init updateTrip
        initUpdatesByLinea();
        
    }
    
    public void visualizzaTrip(String tripId) {
        // 1. Reset dati precedenti
    	Close();
    	CurrentSate = 0;
    	busController.setIsSelected(true);
    	lineController.viewRouteTrip(tripId);
    	
    	String shapeId = lineController.get_shape_id();
    	Set<String> trips_id = lineController.get_trips().keySet();
        System.out.println(trips_id);
        // 3. Carica fermate per shape e aggiorna overlay
        stopController.setTrips(lineController.get_trips());
        stopController.loadStopWaypoint(true);
        stopController.setCurrentLocalWaypoints();
        stopController.setCurrentLocalStopsStaticLabel();
        
        lineController.setCurrentTrips(stopController.getStopTimesByTrips());
        
        Set<MyWaypoint> waypoints = stopController.get_Waypoints();      
        mapController.initStopsWaypoint(lineController.get_route_id(),shapeId, waypoints);

        // 4. Mostra linea sulla mappa
        lineController.showLinea(true);
        
        // 5. Carica dati realtime
        stopController.setTrips_id(trips_id);
        
        // 6. Init updateTrip
        initUpdatesByLinea();
        
    }
    
    public void visualizzaFermata(String stop_id) {
        // 1. Reset dati precedenti
    	Close();
    	CurrentSate = 1;
    	busController.setIsSelected(false);
        // 2. Carica le informazioni della fermata
        stopController.viewStopById(stop_id);
        stopController.setCurrentLocalWaypoints();
        stopController.setCurrentLocalLineeStaticLabel();
        
        Set<MyWaypoint> waypoints = stopController.get_Waypoints();

        // 3. Aggiorna la mappa con la singola fermata
        mapController.initStopsWaypoint(null,"", waypoints);

        // 4. Mostra la fermata nella vista laterale
        stopController.showFermata();
        
        // 5. Carica dati realtime
        initUpdatesByFermata();
    }
    
    public void clearAllWaypoints() {
    	Set<MyWaypoint> stopswaypoints = stopController.get_Waypoints();
    	mapController.clearStopsWaypoint(stopswaypoints);
    	Set<MyWaypoint> buswaypoints = busController.get_Waypoints();
    	mapController.clearBusWaypoint(buswaypoints);
    	Set<MyWaypoint> pingwaypoints = stopController.get_PingWaypoints();
    	mapController.clearPingWaypoint(pingwaypoints);
    }
    
    public void Close() {
    	clearAllWaypoints();
    	stopUpdatesByLinea();
    	stopUpdatesByFermata();
    }
    
    public void initUpdatesByLinea() {
    	System.out.println("initUpdatesByLinea");
    	// Evita di creare più timer
        if (lineaTimer != null && lineaTimer.isRunning()) {
            return;
        }
        
        // Esegui subito l'azione desiderata una prima volta
        updateByLinea();

        // Crea un nuovo Timer che gira ogni 30 secondi
        lineaTimer = new javax.swing.Timer(DELAY, e -> {
        	updateByLinea();       
        });

        // Avvia il timer
        lineaTimer.start();
    }
    
    public void initUpdatesByFermata() {
    	System.out.println("initUpdatesByFermata");
    	// Evita di creare più timer
        if (fermataTimer != null && fermataTimer.isRunning()) {
            return;
        }
        
        // Esegui subito l'azione desiderata una prima volta
        updateByFermata();

        // Crea un nuovo Timer che gira ogni 30 secondi
        fermataTimer = new javax.swing.Timer(DELAY, e -> {
        	updateByFermata();       
        });

        // Avvia il timer
        fermataTimer.start();
    }
    
    private void updateByLinea() {
    	//OfflineMode
    	
    	// 2.Rimuovi i vecchi waypoint
    	Set<MyWaypoint> oldWaypoints = busController.get_Waypoints();
        mapController.clearBusWaypoint(oldWaypoints);
    	
    	// 3. Carica fermate per shape e aggiorna overlay
        stopController.loadStopWaypoint(stopController.getSelectedTrip());
        lineController.setCurrentTrips(stopController.getStopTimesByTrips());
        
        
        busController.updateStaticBusPositions(lineController.get_route_id(),lineController.getCurrentTrips());
        Set<MyWaypoint> newWaypoints = busController.get_Waypoints();
        mapController.initBusWaypoint(newWaypoints);
    	
    	ConnectivityUtil.checkConnectivityAndSwitchMode();
    	if (ConnectivityUtil.isOfflineMode()) {
            return;
        }
   
    	//OnlineMode
    	
    	// 6. Init Trip
        if (!this.StopSelected) {
            stopController.updateTripUpdatesByLinea();
        }
        
        // 7. Init bus
        // Rimuovi i vecchi waypoint
        oldWaypoints = busController.get_Waypoints();
        mapController.clearBusWaypoint(oldWaypoints);

        // Aggiorna i dati dei bus
        String routeId = lineController.get_route_id();
    	boolean direction = lineController.get_direction();
        busController.updateBusPositions(routeId,direction,lineController.getCurrentTrips());

        // Aggiungi i nuovi waypoint
        newWaypoints = busController.get_Waypoints();
        mapController.initBusWaypoint(newWaypoints);
    }
    
    private void updateByFermata() {
    	//OfflineMode
    	
    	// 2. Carica le informazioni della fermata
    	String stop_id = stopController.get_stopId();
    	stopController.viewStopById(stop_id);
    	
    	ConnectivityUtil.checkConnectivityAndSwitchMode();
    	if (ConnectivityUtil.isOfflineMode()) {
            return;
        }
   
    	//OnlineMode
    	stopController.updateTripUpdatesByFermata();
    }
    
    public void stopUpdatesByLinea() {
    	System.out.println("stopUpdatesByLinea");
        if (lineaTimer != null) {
        	lineaTimer.stop();
        	lineaTimer = null;
        }
    }
    
    public void stopUpdatesByFermata() {
    	System.out.println("stopUpdatesByFermata");
        if (fermataTimer != null) {
        	fermataTimer.stop();
        	fermataTimer = null;
        }
    }

   
    public void setStateControl(int StateControl) {
    	this.StateControl = StateControl;
    }
    public int getStateControl() {
    	return StateControl;
    }
    public int getCurrentSate() {
    	return CurrentSate;
    }
    public void setStopSelected(boolean StopSelected) {
    	this.StopSelected = StopSelected;
    }
    public boolean getStopSelected() {
    	return StopSelected;
    }

}
