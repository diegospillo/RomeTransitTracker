package service;

import java.util.*;

import model.ModelManager;

/**
 * Gestisce il caricamento e l'accesso ai dati statici GTFS dell'applicazione
 * Rome Transit Tracker. Espone metodi di utilità per filtrare e recuperare
 * informazioni su linee, corse e fermate a partire dai file GTFS.
 */
public class GTFSManager {

    private String CURRENT_DATE;

    private DataGTFS agency;
    private DataGTFS calendarDates;
    private DataGTFS routes;
    private DataGTFS shapes;
    private DataGTFS stopTimes;
    private DataGTFS stops;
    private DataGTFS trips;

    private final static GTFSManager gtfsManager = new GTFSManager();

    /**
     * Carica i file GTFS dalla cartella indicata e popola le strutture dati interne.
     *
     * @param folder       directory contenente i file GTFS
     * @param current_date data corrente nel formato {@code yyyyMMdd}
     */
    public void loadData(String folder,String current_date) {
    	  this.CURRENT_DATE = current_date;
    	  DataGTFS[] dataGtfs = new DataGTFS[7];
        String[] gtfsNames = {"agency", "calendar_dates", "routes", "shapes","stop_times", "stops", "trips"};
        for (int i = 0; i < gtfsNames.length; i++) {
            dataGtfs[i] = GTFSReader.readFile(folder,gtfsNames[i]);
        }
        loadGtfsData(dataGtfs);
    }

    /**
     * Inizializza gli oggetti {@link DataGTFS} con i dati forniti.
     *
     * @param dataGtfs array con otto tabelle GTFS
     * @throws IllegalArgumentException se l'array non contiene il numero minimo di elementi richiesti
     */
    private void loadGtfsData(DataGTFS[] dataGtfs) {
        if (dataGtfs == null || dataGtfs.length < 7) {
            throw new IllegalArgumentException("Invalid DataGtfs array: Must contain at least 7 elements.");
        }
        this.agency = dataGtfs[0];
        this.calendarDates = dataGtfs[1];
        this.routes = dataGtfs[2];
        this.shapes = dataGtfs[3];
        this.stopTimes = dataGtfs[4];
        this.stops = dataGtfs[5];
        this.trips = dataGtfs[6];
    }

    /**
     * Restituisce gli identificativi dei servizi attivi nella data corrente.
     *
     * @return insieme degli id servizio filtrati per data
     */
    public Set<String> getFilterService_idByDate() {
        List<String> values = new ArrayList<>();
        for (DataRow row : calendarDates.dataList()) {
            if (CURRENT_DATE.equals(row.get("date"))){
                values.add(row.get("service_id"));
            }
        }
        return new LinkedHashSet<>(values);
    }
  
    public DataGTFS getAgency() {
        return agency;
    }

    public DataGTFS getCalendarDates() {
        return calendarDates;
    }

    /** @return tabella delle linee */
    public DataGTFS getRoutes() {
        return routes;
    }

    /** @return tabella delle shape dei percorsi */
    public DataGTFS getShapes() {
        return shapes;
    }

    /** @return tabella dei tempi di passaggio alle fermate */
    public DataGTFS getStopTimes() {
        return stopTimes;
    }

    /** @return tabella delle fermate */
    public DataGTFS getStops() {
        return stops;
    }

    /** @return tabella delle corse */
    public DataGTFS getTrips() {
        return trips;
    }

    /**
     * Restituisce l'istanza unica del gestore dei dati GTFS.
     *
     * @return singleton di {@code GTFSManager}
     */
    public static GTFSManager getInstance() {
        return gtfsManager;
    }
}
