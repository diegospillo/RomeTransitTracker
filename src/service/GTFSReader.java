package service;

import java.io.*;
import java.util.*;

import model.ModelManager;

/**
 * Utilità per la lettura dei file GTFS in formato CSV.
 */
public class GTFSReader {

    private static String FOLDER;

    /**
     * Legge un file GTFS e lo converte in una struttura {@link DataGTFS}.
     *
     * @param folder   cartella contenente i file
     * @param filename nome del file (senza estensione)
     * @return dati GTFS caricati
     */
    public static DataGTFS readFile(String folder,String filename) {
        DataGTFS dataList = new DataGTFS(new ArrayList<>());
        FOLDER = folder;
        try (BufferedReader br = new BufferedReader(new FileReader(FOLDER + "/" + filename + ".txt"))) {
            String headerLine = br.readLine(); // Legge la prima riga (nomi delle colonne)
            if (headerLine == null) return dataList;

            String[] headers = headerLine.split(",");

            String line;
            while ((line = br.readLine()) != null) {
                DataRow rowMap = getDataRow(filename, line, headers);
                dataList.add(rowMap);
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }
        return dataList;
    }

    /**
     * Costruisce una {@link DataRow} a partire da una riga del file.
     *
     * @param filename nome del file da cui provengono i dati
     * @param line     riga di testo del CSV
     * @param headers  intestazioni delle colonne
     * @return riga mappata
     */
    private static DataRow getDataRow(String filename, String line, String[] headers) {
        String[] values = line.split(",");
        DataRow rowMap = new DataRow(new HashMap<>());
        if(!filename.equals("stop_times")) {
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    rowMap.put(headers[i], values[i].replace("\"",""));
                }
        }
        return rowMap;
    }

    /**
     * Popola gli orari delle fermate nei {@link model.Trip} già presenti nel
     * {@link ModelManager} leggendo il file {@code stop_times.txt}.
     *
     * @param modelMenager gestore del modello da aggiornare
     */
    public static void setStop_times(ModelManager modelMenager) {
        try (BufferedReader br = new BufferedReader(new FileReader(FOLDER + "/stop_times.txt"))) {
            String headerLine = br.readLine(); // Legge la prima riga (nomi delle colonne)
            if (headerLine == null) return;
            String line;
            Set<String> trips_id = modelMenager.getTrips().keySet();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String trip_id = values[StopTimesHeader.trip_id.getIndex()];
                String stop_id = values[StopTimesHeader.stop_id.getIndex()];
                String arrival_time = values[StopTimesHeader.arrival_time.getIndex()];
                String stopSequence = values[StopTimesHeader.stop_sequence.getIndex()];
                if (trips_id.contains(trip_id)) {
                        modelMenager.getTrips().get(trip_id).AddStopTime(stop_id, arrival_time, stopSequence);
                }
            }
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }
    }
}
