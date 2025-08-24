package controller;

import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import model.ModelManager;
import model.StopTime;
import model.Trip;
import service.DataRow;
import service.FavoritesManager;
import service.GTFSManager;
import view.MainView;

/**
 * Gestisce le operazioni legate alle linee del trasporto pubblico.
 */
public class LineController{
	
	private final GTFSManager gtfsManager;
	private final ModelManager modelManager;
	private final MainView mainView;
	private String route_id;
    private String shape_id;
    private String nome_linea;
    private Map<String,Trip> trips;
    private Map<String,StopTime> currentTrips;
    private boolean direction = true;
    private final List<String> allLines;
    private boolean selectedTrip = false;
    private boolean isFavorite = false;
	
	
    /**
     * Crea il controller associato alla vista principale.
     *
     * @param mainView interfaccia grafica principale
     */
    public LineController(MainView mainView){
        this.gtfsManager = GTFSManager.getInstance();
        this.modelManager = ModelManager.getInstance();
        this.mainView = mainView;
        this.allLines = buildAllLines();
    }

        /**
         * Recupera l'elenco completo delle linee disponibili.
         *
         * @return lista delle linee ordinate alfabeticamente
         */
        private List<String> buildAllLines(){
        Set<String> uniqueLines = new HashSet<>();
        for (DataRow row : gtfsManager.getRoutes().dataList()) {
                String long_name = row.get("route_long_name");
                String my_route_id = row.get("route_id");
                String item = "(" + my_route_id + ") " + long_name;
                if(my_route_id != null){
                        uniqueLines.add(item);
                }
        }
        List<String> sortedLines = new ArrayList<>(uniqueLines);
        Collections.sort(sortedLines);
        return sortedLines;
	}
	
        /**
         * Restituisce tutte le linee.
         *
         * @return elenco completo delle linee
         */
        public List<String> getAllLines(){
        return new ArrayList<>(allLines);
        }

        /**
         * Filtra le linee in base al testo immesso.
         *
         * @param text stringa da cercare
         * @return elenco di linee corrispondenti
         */
        public List<String> getLinesOf(String text){
        final String lowered = text.toLowerCase();
        return allLines.stream()
                .filter(item -> item.toLowerCase().contains(lowered))
                .collect(Collectors.toList());
        }

        /**
         * Carica i dati di una linea specifica e prepara la vista.
         *
         * @param routeId   identificatore della linea
         * @param direction direzione del percorso
         */
        public void viewRouteByName(String routeId, Boolean direction) {
		
		System.out.println("viewRouteByName: " + routeId + " direction: " + direction);
        this.route_id = routeId;
        this.direction = direction;
        Map<String,Trip> preTrips = modelManager.getTripsByRouteId(routeId, direction);
        this.nome_linea = modelManager.getTrips().get(preTrips.keySet().toArray()[0]).getHeadsign();
        List<String> shapesId = modelManager.getShapeIdsByTrips(preTrips.keySet());
        this.shape_id = shapesId.getFirst();
        System.out.println(shape_id);
        this.trips = ModelManager.getTripsByShapeId(preTrips, shape_id);
        this.isFavorite = FavoritesManager.getInstance().getFavoriteLines().contains(routeId);
    }
	
        /**
         * Visualizza un particolare trip della linea.
         *
         * @param tripId identificatore del trip da mostrare
         */
        public void viewRouteTrip(String tripId) {
		Map<String,Trip> curTrips = new HashMap<>();
		Trip trip = modelManager.getTrips().get(tripId);
		curTrips.put(tripId, trip);
		this.trips = new HashMap<>(curTrips);
	}
	
        /**
         * Mostra le informazioni della linea nella vista laterale.
         *
         * @param isSelectedTrip indica se è stato selezionato un singolo trip
         */
        public void showLinea(boolean isSelectedTrip){
		this.selectedTrip = isSelectedTrip;
		if(!isSelectedTrip) {
			mainView.get_lblDettagli().setText("Linea " + route_id);
		}
		else {
			mainView.get_lblDettagli().setText("Linea: " + route_id + "  TripID: " + trips.keySet().toArray()[0]);
		}

		mainView.get_btnAddFavorite().setVisible(!this.isFavorite);
		mainView.get_btnDeleteFavorite().setVisible(this.isFavorite);
		mainView.get_lblLinea().setText(nome_linea);
		mainView.get_lblDescription().setText("");
		mainView.get_btnInvertiDirezione().setVisible(true);
		mainView.get_btnLive().setVisible(true);
		mainView.get_btnIndietro().setVisible(false);
		mainView.get_btnMoreInfo().setVisible(false);
		mainView.get_btnCloseSidePanel().setVisible(true);
		mainView.get_comboFavorites().setVisible(false);
		mainView.get_lblDettagli().setVisible(true);
		mainView.get_scrollPanel().setViewportView(mainView.get_fermateList());
		mainView.adjustSidePanelWidth();
    }
	
        /**
         * Aggiorna i trip correnti applicando un filtro temporale.
         *
         * @param stopTimesByTrips mappa degli orari per trip
         */
        public void setCurrentTrips(Map<String, StopTime> stopTimesByTrips) {
		currentTrips = new HashMap<>();
		long now = Instant.now().getEpochSecond();
		for(StopTime st : stopTimesByTrips.values()) {
			String tripId = st.getTripId();
			long effectiveArrival = st.getArrivalEpoch() + st.getDelaySeconds();
            // Solo se è un arrivo futuro
            if (effectiveArrival >= now) {
                // Se non c'è ancora nulla per questo stop oppure è più vicino
                if (!currentTrips.containsKey(tripId) ||
                    effectiveArrival < currentTrips.get(tripId).getArrivalEpoch() + currentTrips.get(tripId).getDelaySeconds()) {
                	currentTrips.put(tripId, st);
                }
            }
        }
	}
	
        /**
         * Imposta se la linea è tra i preferiti.
         *
         * @param isFavorite stato di preferito
         */
        public void setIsFavorite(boolean isFavorite) {
        this.isFavorite = isFavorite;
    }
	
        /**
         * @return direzione corrente della linea
         */
        public boolean get_direction() {
                return direction;
        }

        /**
         * @return identificatore della linea
         */
        public String get_route_id() {
                return route_id;
        }

        /**
         * @return shape id corrente
         */
        public String get_shape_id() {
                return shape_id;
        }

        /**
         * @return nome della linea
         */
        public String get_nome_linea() {
                return nome_linea;
        }

        /**
         * @return mappa dei trip caricati
         */
        public Map<String,Trip> get_trips() {
                return trips;
        }

        /**
         * @return {@code true} se è stato selezionato un trip specifico
         */
        public boolean getSelectedTrip() {
                return selectedTrip;
        }

        /**
         * @return mappa dei trip correnti filtrati
         */
        public Map<String,StopTime> getCurrentTrips(){
                return this.currentTrips;
        }
	
}
