package controller;

import java.util.*;
import java.util.stream.Collectors;

import org.jxmapviewer.viewer.GeoPosition;

import model.ModelManager;
import model.Stop;
import model.StopTime;
import model.Trip;
import service.DataRow;
import service.FavoritesManager;
import service.GTFSManager;
import view.MainView;
import waypoint.MyWaypoint;
import net.GTFSFetcher;

/**
 * Controller dedicato alla gestione delle fermate. Si occupa di caricare i
 * waypoint associati, gestire la ricerca e mostrare gli orari delle fermate
 * nella vista principale.
 */
public class StopController {
         private final GTFSManager gtfsManager;
         private final ModelManager modelManager;
     private final MainView mainView;
     private Set<MyWaypoint> pingWaypoints = new HashSet<>();
     private Set<MyWaypoint> waypoints = new HashSet<>();
     private Set<MyWaypoint> localWaypoints = new HashSet<>();
     private String stopId;
     private String stopName;
     private Set<String> trips_id;
     private Map<String,Trip> trips;
     private final List<String> allStops;
     private Map<String,String> stopsStaticLabel = new LinkedHashMap<>();
     private Map<String,String> localStopsStaticLabel = new LinkedHashMap<>();
     private Map<String,String> lineeStaticLabel = new LinkedHashMap<>();
     private Map<String,String> localLineeStaticLabel = new LinkedHashMap<>();
     private Map<String,String> timesLabel = new LinkedHashMap<>();
     private List<String> stopsIdByTrips = new ArrayList<>();
     private List<String> localStopsIdByTrips = new ArrayList<>();
     private boolean selectedTrip = false;
     private boolean isFavorite = false;
     private Map<String, StopTime> stopTimesByTrips = new LinkedHashMap<>();
	
     /**
      * Costruisce un nuovo controller delle fermate collegato alla vista
      * principale.
      *
      * @param mainView riferimento alla {@link MainView}
      */
     public StopController(MainView mainView){
         this.gtfsManager = GTFSManager.getInstance();
         this.modelManager = ModelManager.getInstance();
         this.mainView = mainView;
         this.allStops = buildAllStops();
     }

     /**
      * Genera la lista ordinata di tutte le fermate disponibili.
      *
      * @return elenco delle fermate formattate come "(id) nome"
      */
     private List<String> buildAllStops(){
         Set<String> uniqueStops = new HashSet<>();
         for (DataRow row : gtfsManager.getStops().dataList()) {
                 String name = row.get("stop_name");
                 String my_stop_id = row.get("stop_id");
                 String item = "(" + my_stop_id + ") " + name;
                 if(my_stop_id != null){
                         uniqueStops.add(item);
                 }
         }
         List<String> sortedStops = new ArrayList<>(uniqueStops);
         Collections.sort(sortedStops);
         return sortedStops;
     }
	
        /**
         * Popola i waypoint delle fermate in base ai viaggi correnti.
         *
         * @param isSelectedTrip indica se è stato selezionato un viaggio
         */
        public void loadStopWaypoint(boolean isSelectedTrip) {
		localWaypoints.clear();
		localStopsIdByTrips.clear();
		localStopsStaticLabel.clear();
		mainView.get_modelFermate().clear();
		this.selectedTrip = isSelectedTrip;
		if(!isSelectedTrip) {
			stopTimesByTrips = ModelManager.getNextStopTimeForStop(trips);
		}
		else {
			stopTimesByTrips = ModelManager.getStopTimeForTrip(trips);
		}
		for (Map.Entry<String, StopTime> entry: stopTimesByTrips.entrySet()) {
			StopTime stopTime = entry.getValue();
			Stop stop = modelManager.getStops().get(stopTime.getStopId());
			String stopName = stop.getName();
			String stopId = stop.getStopId();
			double lat = stop.getLat();
			double lon = stop.getLon();
			String label = "";
			if(isSelectedTrip && stopTime.getIsPassed()) {
				label += "[PASSED]" + stopTime.getLabelLinea(stopName,false);
			}
			else {
				label += stopTime.getLabelLinea(stopName,false);
			}
			localStopsIdByTrips.add(stopId);
			localStopsStaticLabel.put(stopId, label);
			System.out.println(label + " " + stopTime.getStopSequence());
			mainView.get_modelFermate().addElement(label);
			MyWaypoint wayPoint = new MyWaypoint(mainView.get_modelFermate().indexOf(label),stopName, MyWaypoint.PointType.STOPS, mainView.get_event(), new GeoPosition(lat, lon));
			localWaypoints.add(wayPoint);
		}
    }
	
        /**
         * Visualizza le informazioni della fermata specificata.
         *
         * @param stop_id identificativo della fermata
         */
        public void viewStopById(String stop_id) {
		mainView.get_modelLinee().clear();
		localWaypoints.clear();
		localLineeStaticLabel.clear();
        Stop stop = modelManager.getStops().get(stop_id);
        this.stopId = stop.getStopId();
        this.stopName = stop.getName();
        this.isFavorite = FavoritesManager.getInstance().getFavoriteStops().contains(stop_id);

        MyWaypoint wayPoint = new MyWaypoint(0, stopName, MyWaypoint.PointType.STOPS,mainView.get_event(),new GeoPosition(stop.getLat(),stop.getLon()));
        localWaypoints.add(wayPoint);

        Map<String, StopTime> stopTimes = modelManager.getStopTimesRouteByStopId(stop_id);
        
        for (Map.Entry<String, StopTime> entry: stopTimes.entrySet()) {
			StopTime stopTime = entry.getValue();
			String routeId = entry.getKey();
			String tripId = stopTime.getTripId();
            Trip trip = modelManager.getTrips().get(tripId);
			String headsign = trip.getHeadsign();
			String label = stopTime.getLabelFermata(routeId, headsign, false);
			localLineeStaticLabel.put(routeId, label);
			System.out.println(label);
			mainView.get_modelLinee().addElement(label);
		}
    }
	
    /**
     * Mostra gli orari di arrivo per la fermata selezionata.
     */
    public void viewTimesByStop(){
            mainView.get_modelOrari().clear();
            timesLabel.clear();
            int index = mainView.get_fermateList().getSelectedIndex();
            String stopIdSelected = stopsIdByTrips.get(index);
            Stop stop = modelManager.getStops().get(stopIdSelected);
            String stopName = stop.getName();
            this.stopId = stop.getStopId();

            List<StopTime> stopTimes = ModelManager.getAllStopTimeForStop(trips,stopIdSelected);
            for (StopTime st : stopTimes){
                    String tripId = st.getTripId();
                    String routeId = modelManager.getTrips().get(tripId).getRouteId();
                    String headsign = modelManager.getTrips().get(tripId).getHeadsign();
                    String label = st.getLabelFermata(routeId, headsign, false);
                    timesLabel.put(tripId,label);
                    mainView.get_modelOrari().addElement(label);
            }
            showOrariFermata(stopName);
    }

    /**
     * Visualizza il pannello laterale con i dettagli della fermata corrente.
     */
    public void showFermata() {
        mainView.get_btnAddFavorite().setVisible(!this.isFavorite);
                mainView.get_btnDeleteFavorite().setVisible(this.isFavorite);
        mainView.get_lblLinea().setText(stopName);
        mainView.get_lblDettagli().setText("Fermata " + stopId);
        mainView.get_lblDescription().setText("Prossimi arrivi");
        mainView.get_btnInvertiDirezione().setVisible(false);
        mainView.get_btnLive().setVisible(false);
        mainView.get_btnIndietro().setVisible(false);
        mainView.get_btnMoreInfo().setVisible(false);
        mainView.get_comboFavorites().setVisible(false);
        mainView.get_btnCloseSidePanel().setVisible(true);
        mainView.get_lblDettagli().setVisible(true);
        mainView.get_scrollPanel().setViewportView(mainView.get_lineeList());
        mainView.adjustSidePanelWidth();
    }

    /**
     * Mostra l'elenco completo degli orari per la fermata indicata.
     *
     * @param stopName nome della fermata
     */
    public void showOrariFermata(String stopName){
        mainView.get_orariList().setModel(mainView.get_modelOrari());
        mainView.get_lblLinea().setText(stopName);
        mainView.get_lblDescription().setText("Tutti gli orari");
        mainView.get_btnInvertiDirezione().setVisible(false);
        mainView.get_btnLive().setVisible(false);
        mainView.get_btnIndietro().setVisible(true);
        mainView.get_btnAddFavorite().setVisible(false);
        mainView.get_btnDeleteFavorite().setVisible(false);
        mainView.get_btnMoreInfo().setVisible(true);
        mainView.get_btnCloseSidePanel().setVisible(false);
        mainView.get_lblDettagli().setVisible(false);
        mainView.get_scrollPanel().setViewportView(mainView.get_orariList());
    }
    
    public void setIsFavorite(boolean isFavorite) {
    	this.isFavorite = isFavorite;
    }
    
    public void setTrips(Map<String,Trip> trips) {
    	this.trips = trips;
    }
    
    public void setTrips_id(Set<String> trips_id) {
    	this.trips_id = trips_id;
    }
    
    public Set<String>  getTrips_id() {
    	return trips_id;
    }
    
    /**
     * Trasferisce i dati locali nelle strutture principali del controller.
     */
    public void setCurrentLocalVariables() {
        this.waypoints = new HashSet<>(localWaypoints);
        this.stopsIdByTrips = new ArrayList<>(localStopsIdByTrips);
        this.stopsStaticLabel = new LinkedHashMap<>(localStopsStaticLabel);
    }

    /**
     * Copia i waypoint locali nella collezione globale.
     */
    public void setCurrentLocalWaypoints() {
        this.waypoints = new HashSet<>(localWaypoints);
    }

    /**
     * Copia le etichette statiche delle fermate nella struttura principale.
     */
    public void setCurrentLocalStopsStaticLabel() {
        this.stopsStaticLabel = new LinkedHashMap<>(localStopsStaticLabel);
    }

    /**
     * Copia le etichette statiche delle linee nella struttura principale.
     */
    public void setCurrentLocalLineeStaticLabel() {
        this.lineeStaticLabel = new LinkedHashMap<>(localLineeStaticLabel);
    }
    
    /**
     * Genera i waypoint di ping relativi alla fermata selezionata nella lista.
     */
    public void setPingWaypoints() {
        int index = mainView.get_fermateList().getSelectedIndex();
        if (index < 0 || index >= stopsIdByTrips.size()) {
            // nessuna selezione valida: opzionale refresh UI e return
            return;
        }
        pingWaypoints.clear();
        String stopIdSelected = stopsIdByTrips.get(index);
        Stop stop = modelManager.getStops().get(stopIdSelected);
        MyWaypoint wayPoint = new MyWaypoint(0,stopName, MyWaypoint.PointType.PING, mainView.get_event(), new GeoPosition(stop.getLat(), stop.getLon()));
        pingWaypoints.add(wayPoint);
    }
    /**
     * Restituisce i waypoint correnti delle fermate.
     *
     * @return set di waypoint
     */
    public Set<MyWaypoint> get_Waypoints() {
            return waypoints;
    }

    /**
     * Restituisce i waypoint di ping attivi.
     *
     * @return set di waypoint di ping
     */
    public Set<MyWaypoint> get_PingWaypoints() {
            return pingWaypoints;
    }

    /**
     * Restituisce l'identificativo della fermata attualmente selezionata.
     *
     * @return ID della fermata
     */
    public String get_stopId() {
            return stopId;
    }

    /**
     * Indica se è selezionato un viaggio specifico.
     *
     * @return true se un viaggio è selezionato
     */
    public boolean getSelectedTrip() {
            return selectedTrip;
    }

    /**
     * Cerca le fermate che contengono il testo fornito.
     *
     * @param text testo da cercare
     * @return lista di fermate corrispondenti
     */
    public List<String> getStopsOf(String text){
        final String lowered = text.toLowerCase();
        return allStops.stream()
                .filter(item -> item.toLowerCase().contains(lowered))
                .collect(Collectors.toList());
    }

    /**
     * Restituisce i tempi delle fermate per ogni viaggio.
     *
     * @return mappa tripId -> StopTime
     */
    public Map<String, StopTime> getStopTimesByTrips(){
            return stopTimesByTrips;
    }

    /**
     * Aggiorna i dati in tempo reale per la linea corrente.
     */
    /**
     * Aggiorna gli orari in tempo reale per la linea corrente.
     */
    public void updateTripUpdatesByLinea() {
            System.out.println("updateTripUpdatesByLinea");
            mainView.get_modelFermate().clear();
            GTFSFetcher.fetchTripUpdates();
            Map<String, StopTime> stopTimesRealTimeByTrips = ModelManager.getNextStopTimeRealTimeForStop(trips);
            Map<String,String> stopsLabel = new LinkedHashMap<>(stopsStaticLabel);
            for (Map.Entry<String, StopTime> entry: stopTimesRealTimeByTrips.entrySet()) {
                    StopTime stopTime = entry.getValue();
                    Stop stop = modelManager.getStops().get(stopTime.getStopId());
                    String stopId = stop.getStopId();
                    String stopName = stop.getName();
                    String label = "[LIVE]" + stopTime.getLabelLinea(stopName,true);
                    System.out.println(label);
                    if(stopsStaticLabel.keySet().contains(stopId)){
                            stopsLabel.put(stopId, label);
                    }
            }
            for(String label : stopsLabel.values()) {
                    mainView.get_modelFermate().addElement(label);
            }

            mainView.get_scrollPanel().setViewportView(mainView.get_fermateList());
        }

    /**
     * Aggiorna le informazioni in tempo reale per la fermata corrente.
     */
    public void updateTripUpdatesByFermata() {
            System.out.println("updateTripUpdatesByFermata");
            mainView.get_modelLinee().clear();
            GTFSFetcher.fetchTripUpdates();
            Map<String, StopTime> stopTimes = modelManager.getStopTimesRealTimeRouteByStopId(stopId);
            Map<String,String> lineeLabel = new LinkedHashMap<>(lineeStaticLabel);

        for (Map.Entry<String, StopTime> entry: stopTimes.entrySet()) {
			StopTime stopTime = entry.getValue();
			String routeId = entry.getKey();
			String tripId = stopTime.getTripId();
            Trip trip = modelManager.getTrips().get(tripId);
			String headsign = trip.getHeadsign();
			String label = stopTime.getLabelFermata(routeId, headsign, true) + "[LIVE]";
			System.out.println(label);
			if(lineeStaticLabel.keySet().contains(routeId)){
				lineeLabel.put(routeId, label);
			}
		}
		
		for(String label : lineeLabel.values()) {
			mainView.get_modelLinee().addElement(label);
		}
		

	    mainView.get_scrollPanel().setViewportView(mainView.get_lineeList());
	}
	
        /**
         * Converte un timestamp UNIX in formato orario HH:mm.
         *
         * @param epochSeconds secondi dal 1° gennaio 1970
         * @return orario formattato o "N/D" se il valore non è valido
         */
        private String unixToTime(long epochSeconds) {
            if (epochSeconds <= 0) return "N/D";
            Date date = new Date(epochSeconds * 1000);
            return new java.text.SimpleDateFormat("HH:mm").format(date);
        }
}
