package service;

import java.io.*;
import java.util.*;

public class GTFSReader {

    public static DataGTFS readFile(String folder,String filename) {
        DataGTFS dataList = new DataGTFS(new ArrayList<>());

        try (BufferedReader br = new BufferedReader(new FileReader(folder + "/" + filename + ".txt"))) {
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
//        switch (filename) {
//            case "stop_times":
//                for (int i = 0; i < headers.length && i < values.length; i++) {
//                    if (headers[i].equals("stop_id") && values[i].equals("10003")) {
//                        rowMap.put(headers[i], values[i]);
//                    }
//                }
//                break;
//            default:
        if(!filename.equals("stop_times")) {
                for (int i = 0; i < headers.length && i < values.length; i++) {
                    rowMap.put(headers[i], values[i].replace("\"",""));
//                }
                }
        }
        return rowMap;
    }

    public static List<Stop_Times> filterStop_timesByTrips_id(Set<String> trips_id) {
        try (BufferedReader br = new BufferedReader(new FileReader("rome_static_gtfs/stop_times.txt"))) {
            String headerLine = br.readLine(); // Legge la prima riga (nomi delle colonne)
            if (headerLine == null) return null;
            String line;
            List<Stop_Times> dataSet = new ArrayList<>();
            Set<String> stops = new HashSet<>();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                    if (trips_id.contains(values[StopTimesHeader.trip_id.getIndex()]) && !stops.contains(values[StopTimesHeader.stop_id.getIndex()])) {
                        Stop_Times stop_times = new Stop_Times(values);
                        stop_times.SetTimes(values[StopTimesHeader.arrival_time.getIndex()], values[StopTimesHeader.departure_time.getIndex()]);
                        stops.add(values[StopTimesHeader.stop_id.getIndex()]);
                        dataSet.add(stop_times);
                    }
                    else if (trips_id.contains(values[StopTimesHeader.trip_id.getIndex()]) && (stops.contains(values[StopTimesHeader.stop_id.getIndex()]))){
                        Stop_Times result = null;
                        for (Stop_Times c : dataSet) {
                            if (Objects.equals(c.getStop_id(), values[StopTimesHeader.stop_id.getIndex()])) {
                                result = c;
                                break;
                            }
                        }
                        assert result != null;
                        result.SetTimes(values[StopTimesHeader.arrival_time.getIndex()], values[StopTimesHeader.departure_time.getIndex()]);
                    }
            }
            return dataSet;
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }
        return null;
    }

    public static List<Stop_Routes> filterStop_timesByStop_id(String stopIdMap, GTFSManager gtfsManager) {
        try (BufferedReader br = new BufferedReader(new FileReader("rome_static_gtfs/stop_times.txt"))) {
            String headerLine = br.readLine(); // Legge la prima riga (nomi delle colonne)
            if (headerLine == null) return null;
            String line;
            List<Stop_Routes> dataSet = new ArrayList<>();
            while ((line = br.readLine()) != null) {
                String[] values = line.split(",");
                String trip_id = values[StopTimesHeader.trip_id.getIndex()];
                String arrival_time = values[StopTimesHeader.arrival_time.getIndex()];
                String departure_time = values[StopTimesHeader.departure_time.getIndex()];
                String stop_id = values[StopTimesHeader.stop_id.getIndex()];

                if (stopIdMap.equals(stop_id)) {
                    Map<String,String> trip = gtfsManager.GetTripToRoute(trip_id);
                    Map<String,String> routeInfo = gtfsManager.GetRouteInfo();
                    if (trip != null && routeInfo.containsKey(trip.get("route_id"))) {
                        String route = routeInfo.get(trip.get("route_id"));
                        dataSet.add(new Stop_Routes(trip_id, route, trip.get("headsign"), arrival_time, departure_time));
                    }
                }

            }
            return dataSet;
        } catch (IOException e) {
            System.err.println("Errore nella lettura del file: " + e.getMessage());
        }
        return null;
    }

}
