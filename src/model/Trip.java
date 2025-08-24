package model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.transit.realtime.GtfsRealtime.TripDescriptor;

import service.DataRow;

/**
 * Descrive un viaggio specifico di una linea del trasporto pubblico.
 */
public class Trip {
    private String trip_id;
    private String route_id;
    private String service_id;
    private String headsign;
    private String shape_id;
    private boolean direction;
    private List<StopTime> stopTimes = new ArrayList<>();
    private List<StopTime> stopTimesRealTime = new ArrayList<>();

    /**
     * Costruisce un trip a partire dai dati GTFS.
     *
     * @param data riga del file {@code trips.txt}
     */
    Trip(DataRow data){
        this.trip_id = data.get("trip_id");
        this.route_id = data.get("route_id");
        this.service_id = data.get("service_id");
        this.headsign = data.get("trip_headsign");
        this.shape_id = data.get("shape_id");
        this.direction = data.get("direction_id").equals("0");
    }

    /**
     * Aggiunge un orario programmato al trip.
     *
     * @param stop_id      fermata interessata
     * @param arrival_time orario di arrivo programmato
     * @param stopSequence sequenza della fermata
     */
    public void AddStopTime(String stop_id,String arrival_time,String stopSequence) {
        stopTimes.add(new StopTime(this.trip_id, stop_id, arrival_time, stopSequence));
    }

    /**
     * Aggiunge un orario in tempo reale con relativo ritardo.
     *
     * @param stop_id      fermata interessata
     * @param arrival_time orario di arrivo reale (epoch)
     * @param stopSequence sequenza della fermata
     * @param delay        ritardo in secondi
     */
    public void addStopTimeRealTime(String stop_id,Long arrival_time,int stopSequence, int delay) {
        stopTimesRealTime.add(new StopTime(this.trip_id, stop_id, arrival_time, stopSequence, delay));
    }

    /**
     * Restituisce tutti gli orari di arrivo programmati in formato epoch.
     *
     * @return lista di timestamp
     */
    public List<Long> getAllArrivalEpoch(){
        List<Long> arrivalEpoch = new ArrayList<>();
        for(StopTime stoptime : stopTimes) {
                arrivalEpoch.add(stoptime.arrivalEpoch);
        }
        return arrivalEpoch;
    }

    /** @return identificativo del trip */
    public String getTripId(){
        return trip_id;
    }
    /** @return identificativo della linea */
    public String getRouteId(){
        return route_id;
    }
    /** @return identificativo del servizio */
    public String getServiceId(){
        return service_id;
    }
    /** @return destinazione del trip */
    public String getHeadsign(){
        return headsign;
    }
    /** @return identificativo della shape */
    public String getShapeId(){
        return shape_id;
    }
    /** @return direzione del viaggio */
    public Boolean getDirection(){
        return direction;
    }

   /** @return lista degli orari programmati */
   public List<StopTime> GetStopTimes() {
           return stopTimes;
   }
   /** @return lista degli orari in tempo reale */
   public List<StopTime> GetStopTimesRealTime() {
           return stopTimesRealTime;
   }
   /** Cancella gli orari in tempo reale memorizzati. */
   public void clearStopTimesRealTime() {
           this.stopTimesRealTime.clear();
   }
}

