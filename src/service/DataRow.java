package service;

import java.util.HashMap;
import java.util.Map;

/**
 * Rappresenta una riga generica di un file GTFS, modellata come mappa
 * chiave-valore.
 */
public record DataRow (Map<String, String> rowMap){

    /**
     * Inserisce o sovrascrive un valore nella riga.
     *
     * @param key   nome della colonna
     * @param value valore associato
     */
    public void put(String key, String value) {
        this.rowMap.put(key, value);
    }

    /**
     * Recupera il valore associato alla colonna indicata.
     *
     * @param key nome della colonna
     * @return valore corrispondente oppure {@code null}
     */
    public String get(String key) {
        return this.rowMap.get(key);
    }

    /**
     * Restituisce una copia della mappa interna.
     *
     * @return mappa chiave-valore della riga
     */
    public Map<String, String> getRowMap() {
        return new HashMap<>(this.rowMap);
    }
}