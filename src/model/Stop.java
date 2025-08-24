package model;

import service.DataRow;

/**
 * Rappresenta una fermata del trasporto pubblico.
 */
public class Stop {
    private String stop_id;
    private String name;
    private double lat;
    private double lon;

    /**
     * Costruisce una fermata a partire dai dati GTFS.
     *
     * @param data riga del file {@code stops.txt}
     */
    Stop(DataRow data){
        this.stop_id = data.get("stop_id");
        this.name = data.get("stop_name");
        this.lat = Double.parseDouble(data.get("stop_lat"));
        this.lon = Double.parseDouble(data.get("stop_lon"));
    }

    /** @return identificativo della fermata */
    public String getStopId() {
        return stop_id;
    }

    /** @return nome della fermata */
    public String getName() {
        return name;
    }

    /** @return latitudine della fermata */
    public double getLat() {
        return lat;
    }

    /** @return longitudine della fermata */
    public double getLon() {
        return lon;
    }
}
