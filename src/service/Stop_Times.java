package service;

import java.util.*;

public class Stop_Times {
    private static List<Stop_Times> stops_times = new ArrayList<Stop_Times>();
    private String stop_id;
    private String stop_name;
    private List<Map<String,String>> times = new ArrayList<>();
    private String stop_sequence;
    private String timepoint;
    static private int current_time_index;

    public Stop_Times(String[] values) {
        stop_id = values[StopTimesHeader.stop_id.getIndex()];
        stop_sequence = values[StopTimesHeader.stop_sequence.getIndex()];
        if(StopTimesHeader.timepoint.getIndex() < values.length){
            timepoint = values[StopTimesHeader.timepoint.getIndex()];
        }
        else {
            timepoint = "0";
        }
    }

    public static List<Map<String,String>> GetTimesByIndex(int index) {
        return stops_times.get(index).times;
    }

    public static String GetNameByIndex(int index) {
        return stops_times.get(index).stop_name;
    }

    public void SetStop_name(String stop_name) {
        this.stop_name = stop_name;
    }

    public void SetTimes(String arrival_time, String departure_time){
        Map<String,String> map = new HashMap<>();
        map.put("arr", OrarioUtil.format_times(arrival_time));
        map.put("dep", OrarioUtil.format_times(departure_time));
        times.add(map);
    }

    public void SortTimes(){
        times.sort(Comparator.comparing(m -> OrarioUtil.toMins(m.get("arr"))));
    }

    public String getStop_id() {
        return stop_id;
    }
    public String getStop_name() {
        return stop_name;
    }
    public String getStop_sequence() {
        return stop_sequence;
    }
    public String getTimepoint() {
        return timepoint;
    }
    public List<Map<String,String>> getTimes() {
        return times;
    }

    public static List<Stop_Times> getStops_times() {
        return stops_times;
    }

    public static void setStops_times(List<Stop_Times> args) {
        stops_times = args;
    }

    public void setCurrentTimeIndex() {
        current_time_index = OrarioUtil.trovaIndexOrarioPiuVicino(times);
    }

    public String getCurrentTime(){
        if(current_time_index < times.size()){
            return times.get(current_time_index).get("arr");
        }
        return null;
    }
}
