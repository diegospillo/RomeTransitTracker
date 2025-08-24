package service;

import java.util.Comparator;
import java.util.List;

/**
 * Contenitore per una tabella GTFS composta da più {@link DataRow}.
 */
public record DataGTFS(List<DataRow> dataList) {

    /**
     * Aggiunge una nuova riga alla tabella.
     *
     * @param rowMap riga da inserire
     */
    public void add(DataRow rowMap) {
        this.dataList.add(rowMap);
    }

    /**
     * Ordina le righe secondo il comparatore fornito.
     *
     * @param comparator criterio di ordinamento
     */
    public void sort(Comparator<DataRow> comparator) {
        this.dataList.sort(comparator);
    }

    /**
     * Restituisce il numero di righe presenti.
     *
     * @return dimensione della tabella
     */
    public int size() {
        return this.dataList.size();
    }

    /**
     * Svuota la tabella rimuovendo tutte le righe.
     */
    public void clear() {
        this.dataList.clear();
    }
}
