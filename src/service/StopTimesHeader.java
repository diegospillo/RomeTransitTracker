package service;

/**
 * Enum che rappresenta le colonne del file {@code stop_times.txt} del
 * feed GTFS.
 */
public enum StopTimesHeader {
    /** Identificativo del trip. */
    trip_id(0),
    /** Orario di arrivo alla fermata. */
    arrival_time(1),
    /** Orario di partenza dalla fermata. */
    departure_time(2),
    /** Identificativo della fermata. */
    stop_id(3),
    /** Sequenza della fermata all'interno del trip. */
    stop_sequence(4),
    /** Flag di precisione dell'orario. */
    timepoint(9);

    private final int index;

    StopTimesHeader(int n){
        this.index = n;
    }

    /**
     * Restituisce la posizione della colonna nel file.
     *
     * @return indice della colonna
     */
    public int getIndex(){
        return this.index;
    }
}
