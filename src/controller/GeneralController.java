package controller;

import java.util.*;

import model.StopTime;
import model.Trip;
import net.ConnectivityUtil;
import waypoint.MyWaypoint;

/**
 * Controller principale che coordina la logica dell'interfaccia utente della
 * Rome Transit Tracker. Gestisce la visualizzazione di linee, viaggi e fermate
 * sulla mappa, sincronizzando i vari controller specializzati e avviando gli
 * aggiornamenti periodici dei dati.
 */
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

    /**
     * Crea un nuovo {@code GeneralController} collegando i controller
     * specializzati utilizzati per la gestione dell'applicazione.
     *
     * @param lineController gestore delle linee
     * @param stopController gestore delle fermate
     * @param busController gestore dei bus
     * @param mapController gestore della mappa
     */
    public GeneralController(LineController lineController,
                             StopController stopController,
                             BusController busController,
                             MapController mapController) {
        this.lineController = lineController;
        this.stopController = stopController;
        this.busController = busController;
        this.mapController = mapController;
    }

    /**
     * Visualizza una linea specifica sulla mappa caricando fermate e viaggi
     * associati, e avvia gli aggiornamenti periodici dei dati.
     *
     * @param routeId   identificativo della linea
     * @param direction direzione del percorso (true se andata)
     */
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
    
    /**
     * Visualizza un singolo viaggio identificato da {@code tripId} e
     * posiziona i waypoint corrispondenti alle fermate del viaggio.
     *
     * @param tripId identificativo del viaggio
     */
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
    
    /**
     * Mostra le informazioni di una fermata specifica e la evidenzia sulla
     * mappa.
     *
     * @param stop_id identificativo della fermata
     */
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
    
    /**
     * Rimuove tutti i waypoint attualmente presenti sulla mappa
     * (fermate, bus e ping).
     */
    public void clearAllWaypoints() {
        Set<MyWaypoint> stopswaypoints = stopController.get_Waypoints();
        mapController.clearStopsWaypoint(stopswaypoints);
        Set<MyWaypoint> buswaypoints = busController.get_Waypoints();
        mapController.clearBusWaypoint(buswaypoints);
        Set<MyWaypoint> pingwaypoints = stopController.get_PingWaypoints();
        mapController.clearPingWaypoint(pingwaypoints);
    }
    
    /**
     * Pulisce la mappa e interrompe tutti gli aggiornamenti periodici in corso.
     */
    public void Close() {
        clearAllWaypoints();
        stopUpdatesByLinea();
        stopUpdatesByFermata();
    }
    
    /**
     * Avvia il timer per l'aggiornamento periodico dei dati relativi a una
     * linea.
     */
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
    
    /**
     * Avvia il timer per l'aggiornamento periodico dei dati relativi a una
     * fermata.
     */
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
    
    /**
     * Aggiorna le informazioni di linea: waypoint dei bus e aggiornamenti
     * dei viaggi associati.
     */
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
    
    /**
     * Aggiorna le informazioni di una singola fermata.
     */
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
    
    /**
     * Ferma gli aggiornamenti relativi alla linea.
     */
    public void stopUpdatesByLinea() {
        System.out.println("stopUpdatesByLinea");
        if (lineaTimer != null) {
                lineaTimer.stop();
                lineaTimer = null;
        }
    }

    /**
     * Ferma gli aggiornamenti relativi alla fermata.
     */
    public void stopUpdatesByFermata() {
        System.out.println("stopUpdatesByFermata");
        if (fermataTimer != null) {
                fermataTimer.stop();
                fermataTimer = null;
        }
    }

   
    /**
     * Imposta il tipo di ricerca attuale (linee o fermate).
     *
     * @param StateControl 0 per linee, 1 per fermate
     */
    public void setStateControl(int StateControl) {
        this.StateControl = StateControl;
    }

    /**
     * Restituisce il tipo di ricerca corrente.
     *
     * @return stato della ricerca
     */
    public int getStateControl() {
        return StateControl;
    }

    /**
     * Restituisce lo stato visuale corrente (0 linea, 1 fermata).
     *
     * @return stato visuale
     */
    public int getCurrentSate() {
        return CurrentSate;
    }

    /**
     * Indica se una fermata è stata selezionata.
     *
     * @param StopSelected true se una fermata è selezionata
     */
    public void setStopSelected(boolean StopSelected) {
        this.StopSelected = StopSelected;
    }

    /**
     * Verifica se una fermata è selezionata.
     *
     * @return true se selezionata
     */
    public boolean getStopSelected() {
        return StopSelected;
    }

}
