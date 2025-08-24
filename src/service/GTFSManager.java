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
    private DataGTFS calendar;
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
        DataGTFS[] dataGtfs = new DataGTFS[8];
        String[] gtfsNames = {"agency", "calendar", "calendar_dates", "routes", "shapes","stop_times", "stops", "trips"};
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
        if (dataGtfs == null || dataGtfs.length < 8) {
            throw new IllegalArgumentException("Invalid DataGtfs array: Must contain at least 8 elements.");
        }
        this.agency = dataGtfs[0];
        this.calendar = dataGtfs[1];
        this.calendarDates = dataGtfs[2];
        this.routes = dataGtfs[3];
        this.shapes = dataGtfs[4];
        this.stopTimes = dataGtfs[5];
        this.stops = dataGtfs[6];
        this.trips = dataGtfs[7];
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

    /**
     * Estrae tutti gli identificativi {@code shape_id} presenti nella tabella.
     *
     * @param dataGtfs tabella delle shape
     * @return lista degli id shape
     */
    public List<String> getShapesId(DataGTFS dataGtfs) {
        List<String> shapesid = new ArrayList<>();
        for (DataRow row : dataGtfs.dataList()) {
            shapesid.add(row.get("shape_id"));
        }
        return shapesid;
    }

    /**
     * Filtra le corse {@code trip} in base alla linea, direzione e servizio.
     *
     * @param dataGtfs   tabella dei trip da filtrare
     * @param routes_id  insiemi degli id linea ammessi
     * @param direction  direzione del percorso ({@code true} = 0, {@code false} = 1)
     * @param service_id insiemi degli id servizio validi
     * @return tabella contenente solo i trip compatibili
     */
    public DataGTFS getFilterTripsByRoute_id(DataGTFS dataGtfs, Set<String> routes_id, Boolean direction, Set<String> service_id) {
        String dir_string = direction ? "0" : "1";
        DataGTFS trips_filteredDataGTFS = new DataGTFS(new ArrayList<>());
        for (DataRow row : dataGtfs.dataList()) {
            if (routes_id.contains(row.get("route_id")) && service_id.contains(row.get("service_id")) && dir_string.equals(row.get("direction_id"))){
                trips_filteredDataGTFS.add(row);
            }
        }
        return trips_filteredDataGTFS;
    }

    /**
     * Ricava gli identificativi delle corse associate a una determinata shape.
     *
     * @param dataGtfs tabella dei trip da analizzare
     * @param shape_id identificativo della shape
     * @return insieme degli id trip corrispondenti
     */
    public Set<String>  getFilterTripsByShape_id(DataGTFS dataGtfs,String shape_id) {
        List<String> trips_filtered = new ArrayList<>();
        for (DataRow row : dataGtfs.dataList()) {
            if (shape_id.equals(row.get("shape_id"))){
                trips_filtered.add(row.get("trip_id"));
            }
        }
        return new LinkedHashSet<>(trips_filtered);
    }

    /**
     * Imposta la tabella dei tempi di passaggio alle fermate.
     *
     * @param dataGtfs nuova tabella {@code stop_times}
     */
    public void setStopTimes(DataGTFS dataGtfs) {
        this.stopTimes = dataGtfs;
    }

    /**
     * Recupera la riga della fermata a partire dal suo identificativo.
     *
     * @param stop_id id della fermata
     * @return riga della fermata o {@code null} se non trovata
     */
    public DataRow getStopById(String stop_id) {
        for (DataRow row : this.stops.dataList()) {
            if (stop_id.equals(row.get("stop_id"))) {
                return row;
            }
        }
        return null;
    }

    /**
     * Restituisce l'identificativo di una fermata dato il suo nome.
     *
     * @param stop_name nome della fermata
     * @return id della fermata oppure {@code null} se assente
     */
    public String getStopByName(String stop_name) {
        for (DataRow row : this.stops.dataList()) {
            if (stop_name.equals(row.get("stop_name"))) {
                return row.get("stop_id");
            }
        }
        return null;
    }

    /**
     * Restituisce il nome associato a un identificativo di fermata.
     *
     * @param stop_id id della fermata
     * @return nome della fermata oppure {@code null} se inesistente
     */
    public String getNameByStopId(String stop_id) {
        for (DataRow row : this.stops.dataList()) {
            if (stop_id.equals(row.get("stop_id"))) {
                return row.get("stop_name");
            }
        }
        return null;
    }

    /**
     * Fornisce le informazioni principali di un trip identificato.
     *
     * @param tripId id della corsa
     * @return mappa con dettagli del trip o {@code null} se non presente
     */
    public Map<String, String> getTripById(String tripId) {
        for (DataRow row : this.trips.dataList()) {
            if (tripId.equals(row.get("trip_id"))) {
                Map<String, String> tripInfo = new HashMap<>();
                tripInfo.put("route_id", row.get("route_id"));
                tripInfo.put("trip_headsign", row.get("trip_headsign"));
                tripInfo.put("direction_id", row.get("direction_id"));
                tripInfo.put("shape_id", row.get("shape_id"));
                tripInfo.put("service_id", row.get("service_id"));
                return tripInfo;
            }
        }
        return null; // oppure Collections.emptyMap();
    }

    /** @return tabella delle agenzie */
    public DataGTFS getAgency() {
        return agency;
    }

    /** @return tabella dei calendari di servizio */
    public DataGTFS getCalendar() {
        return calendar;
    }

    /** @return tabella delle eccezioni al calendario */
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
