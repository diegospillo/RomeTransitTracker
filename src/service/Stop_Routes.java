package service;

import java.util.*;

public class Stop_Routes {
    private static Map<String,List<Stop_Routes>> stops_routes = new HashMap<>();
    private static Map<String,Integer> current_time_index = new HashMap<>();

    private String trip_id;
    private String route_id;
    private String trip_headsign;
    private String arrival_time;
    private String departure_time;

    public Stop_Routes(String trip_id, String route, String trip_headsign, String arrival_time, String departure_time) {
        this.trip_id = trip_id;
        this.route_id = route;
        this.trip_headsign = trip_headsign;
        this.arrival_time = OrarioUtil.format_times(arrival_time);
        this.departure_time = OrarioUtil.format_times(departure_time);
    }

    public static void SetStopRoutes(List<Stop_Routes> stop_routes){
        for(Stop_Routes stop_route : stop_routes){
            if(stops_routes.containsKey(stop_route.route_id)){
                stops_routes.get(stop_route.route_id).add(stop_route);
            }
            else{
                List<Stop_Routes> new_list = new ArrayList<>();
                new_list.add(stop_route);
                stops_routes.put(stop_route.route_id, new_list);
            }
        }
        SortTimes();
    }

    public static Map<String,List<Stop_Routes>> GetStopRoutes(){
        return stops_routes;
    }

    public String getTrip_id() {
        return trip_id;
    }

    public String getRoute_id() {
        return route_id;
    }

    public String getTrip_headsign() {
        return trip_headsign;
    }

    public String getArrival_time() {
        return arrival_time;
    }

    public String getDeparture_time() {
        return departure_time;
    }

    public static void SortTimes(){
        for(List<Stop_Routes> stop_routes : stops_routes.values()){
            stop_routes.sort(Comparator.comparing(m -> OrarioUtil.toMins(m.arrival_time)));
        }
    }

    public static void setCurrentTimeIndex(String route_id) {
        List<Map<String,String>> times = new ArrayList<>();
        for(Stop_Routes stop_route : stops_routes.get(route_id)){
            Map<String,String> map = new HashMap<>();
            map.put("arr",stop_route.arrival_time);
            times.add(map);
        }
        current_time_index.put(route_id,OrarioUtil.trovaIndexOrarioPiuVicino(times));
    }

    public static Integer getCurrentTimeIndex(String route_id){
        int index = current_time_index.get(route_id);
        if(index < stops_routes.get(route_id).size()){
            return index;
        }
        return null;
    }
}
