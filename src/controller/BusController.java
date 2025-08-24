package controller;

import org.jxmapviewer.viewer.GeoPosition;

import model.ModelManager;
import model.Stop;
import model.StopTime;
import net.GTFSFetcher;
import net.ConnectivityUtil;
import view.MainView;
import waypoint.MyWaypoint;
import waypoint.MyWaypoint.PointType;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Gestisce la visualizzazione dei bus sulla mappa.
 */
public class BusController{
        private final MainView mainView;
        private final ModelManager modelManager;
        private final Set<MyWaypoint> waypoints = new HashSet<>();
        private boolean IsSelected = false;

    /**
     * Crea il controller e associa la vista principale.
     *
     * @param mainView vista principale dell'applicazione
     */
    public BusController(MainView mainView) {
        this.mainView = mainView;
        this.modelManager = ModelManager.getInstance();
    }

    /**
     * Aggiorna le posizioni statiche dei bus per una linea.
     *
     * @param route_id      identificatore della linea
     * @param currentTrips  mappa dei trip correnti con i relativi orari
     */
    public void updateStaticBusPositions(String route_id,Map<String,StopTime> currentTrips) {
        System.out.println("updateStaticBusPositions");
        Map<String,GeoPosition> staticBusPositions = new HashMap<>();
        for(Map.Entry<String, StopTime> entry:currentTrips.entrySet()) {
    		String tripId = entry.getKey();
    		StopTime st = entry.getValue();
    		Stop stop = modelManager.getStops().get(st.getStopId());
    		double lat = stop.getLat();
            double lon = stop.getLon();
            System.out.println(tripId + " " + lat + " " + lon);
    		staticBusPositions.put(tripId,new GeoPosition(lat, lon));
    	}
    	loadBusWaypoint(staticBusPositions,modelManager.getRoutes().get(route_id).getPointType());
    }
    
    /**
     * Aggiorna le posizioni realtime dei bus richiedendo i dati al servizio GTFS.
     *
     * @param route_id     identificatore della linea
     * @param direction    direzione del percorso
     * @param currentTrips mappa dei trip correnti
     */
    public void updateBusPositions(String route_id,boolean direction,Map<String,StopTime> currentTrips) {
        System.out.println("updateBusPositions");
        Map<String,GeoPosition> busPositions = GTFSFetcher.fetchBusPositions(route_id,direction);
        if(IsSelected) {
        	String tripId = currentTrips.keySet().toArray()[0].toString();
        	GeoPosition gp = busPositions.get(tripId);
        	if(gp!=null) {
        		busPositions = new HashMap<>();
        		busPositions.put(tripId, gp);
        	}
        }
        loadBusWaypoint(busPositions,modelManager.getRoutes().get(route_id).getPointType());
    }

    /**
     * Carica i waypoint dei bus nella vista.
     *
     * @param positions mappa id bus-posizione
     * @param pointType tipo grafico del punto sulla mappa
     */
    private void loadBusWaypoint(Map<String,GeoPosition> positions,PointType pointType) {
        waypoints.clear();
        int i = 0;
        for (Map.Entry<String,GeoPosition> entry : positions.entrySet()) {
        	String tripId = entry.getKey();
        	GeoPosition pos = entry.getValue();
        	if(pos!=null) {
	        	MyWaypoint wayPoint = new MyWaypoint(i,tripId, pointType, mainView.get_event(), new GeoPosition(pos.getLatitude(), pos.getLongitude()));
	        	waypoints.add(wayPoint);
        	}
        	i++;
        }
    }
    
    /**
     * Restituisce l'insieme dei waypoint visualizzati.
     *
     * @return insieme dei waypoint attuali
     */
    public Set<MyWaypoint> get_Waypoints() {
                return waypoints;
        }

    /**
     * Imposta se mostrare solo un bus selezionato.
     *
     * @param state {@code true} per filtrare il bus selezionato
     */
    public void setIsSelected(boolean state) {
        this.IsSelected = state;
    }
    
}

