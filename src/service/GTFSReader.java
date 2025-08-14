package service;

import java.io.*;
import java.util.*;

import model.ModelManager;

public class GTFSReader {
	
    private static String FOLDER;

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
        //System.out.println(filename);
        return dataList;
    }

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
