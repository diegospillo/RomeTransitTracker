package controller;

import java.util.*;
import java.util.stream.Collectors;

import org.jxmapviewer.viewer.GeoPosition;

import service.DataRow;
import service.GTFSManager;
import service.GTFSReader;
import service.Stop_Routes;
import service.Stop_Times;
import view.MainView;
import waypoint.MyWaypoint;

public class StopController {
	 private final GTFSManager gtfsManager;
     private final MainView mainView;
     private final Set<MyWaypoint> waypoints = new HashSet<>();
     private String stopId;
     private String stopName;
     private final List<String> allStops;
	
     public StopController(MainView mainView){
         this.gtfsManager = GTFSManager.getInstance();
         this.mainView = mainView;
         this.allStops = buildAllStops();
     }

     private List<String> buildAllStops(){
         Set<String> uniqueStops = new HashSet<>();
         for (DataRow row : gtfsManager.getStops().dataList()) {
                 String name = row.get("stop_name");
                 String my_stop_id = row.get("stop_id");
                 String item = "(" + my_stop_id + ") " + name;
                 if(my_stop_id != null){
                         uniqueStops.add(item);
                 }
         }
         List<String> sortedStops = new ArrayList<>(uniqueStops);
         Collections.sort(sortedStops);
         return sortedStops;
     }
	
	public void loadStopWaypoint(String shape_id) {
        for (Stop_Times item : Stop_Times.getStops_times()) {
            MyWaypoint.PointType point;
            DataRow row = gtfsManager.getStopById(item.getStop_id());
            item.SetStop_name(row.get("stop_name"));
            item.SortTimes();
            if(item.getStop_sequence().equals("1") && item.getTimepoint().equals("1")) {
                //point = MyWaypoint.PointType.START;
                point = MyWaypoint.PointType.STOPS;
            }
            else if(item.getTimepoint().equals("1")) {
                //point = MyWaypoint.PointType.END;
                point = MyWaypoint.PointType.STOPS;
            }
            else {
                point = MyWaypoint.PointType.STOPS;
            }
            item.setCurrentTimeIndex();
            String current_time = item.getCurrentTime();
            System.out.println(item.getStop_sequence() + " " + item.getStop_name() + " => " + current_time);
            String fermata = current_time + " ";
            //String fermata = "";
            if(row.get("stop_name").length()>=30){
                fermata += row.get("stop_name").substring(0,30) + "...";
            }
            else {
                fermata += row.get("stop_name");
            }
            mainView.get_modelFermate().addElement(fermata);
            MyWaypoint wayPoint = new MyWaypoint(mainView.get_modelFermate().indexOf(fermata),row.get("stop_name"), point, mainView.get_event(), new GeoPosition(Double.parseDouble(row.get("stop_lat")), Double.parseDouble(row.get("stop_lon"))));
            waypoints.add(wayPoint);
        }
        System.out.println("waypoints: " + waypoints.size());
    }
	
	public void viewStopById(String stop_id) {
        this.stopId = stop_id;

        DataRow row = gtfsManager.getStopById(stop_id);
        if (row == null) {
            return;
        }
        this.stopName = row.get("stop_name");

        MyWaypoint wayPoint = new MyWaypoint(0, stopName, MyWaypoint.PointType.STOPS,
                mainView.get_event(),
                new GeoPosition(Double.parseDouble(row.get("stop_lat")), Double.parseDouble(row.get("stop_lon"))));
        waypoints.add(wayPoint);

        List<Stop_Routes> stopRoutes = GTFSReader.filterStop_timesByStop_id(stop_id, gtfsManager);
        if (stopRoutes != null) {
            Stop_Routes.SetStopRoutes(stopRoutes);
            Map<String, List<Stop_Routes>> routes = Stop_Routes.GetStopRoutes();
            for (Map.Entry<String, List<Stop_Routes>> entry : routes.entrySet()) {
                String route = entry.getKey();
                Stop_Routes.setCurrentTimeIndex(route);
                Integer idx = Stop_Routes.getCurrentTimeIndex(route);
                if (idx != null) {
                    Stop_Routes sr = entry.getValue().get(idx);
                    mainView.get_modelLinee().addElement(route + " " + sr.getTrip_headsign() + " " + sr.getArrival_time());
                }
            }
        }
    }

    public void showFermata() {
        mainView.get_lblLinea().setText(stopName);
        mainView.get_lblDettagli().setText("Fermata " + stopId);
        mainView.get_lblDescription().setText("Prossimi arrivi");
        mainView.get_btnInvertiDirezione().setVisible(false);
        mainView.get_btnIndietro().setVisible(false);
        mainView.get_btnMoreInfo().setVisible(false);
        mainView.get_btnCloseSidePanel().setVisible(true);
        mainView.get_lblDettagli().setVisible(true);
        mainView.get_scrollPanel().setViewportView(mainView.get_lineeList());
        mainView.adjustSidePanelWidth();
    }
    
    public void showOrariFermata(String route_id,String nome_linea){
        int index = mainView.get_fermateList().getSelectedIndex();
        List<Map<String, String>> times = Stop_Times.GetTimesByIndex(index);
        mainView.get_modelOrari().clear();
        assert times != null;
        for (Map<String, String> ora : times){
        	mainView.get_modelOrari().addElement(route_id + " " + nome_linea + " " + ora.get("arr"));
        }
        mainView.get_orariList().setModel(mainView.get_modelOrari());

        String nome_fermata = Stop_Times.GetNameByIndex(index);
        this.stopId = Stop_Times.GetStopIdByIndex(index);
        mainView.get_lblLinea().setText(nome_fermata);
        mainView.get_lblDescription().setText("Tutti gli orari");
        mainView.get_btnInvertiDirezione().setVisible(false);
        mainView.get_btnIndietro().setVisible(true);
        mainView.get_btnMoreInfo().setVisible(true);
        mainView.get_btnCloseSidePanel().setVisible(false);
        mainView.get_lblDettagli().setVisible(false);
        mainView.get_scrollPanel().setViewportView(mainView.get_orariList());
    }
	
	public Set<MyWaypoint> get_Waypoints() {
		return waypoints;
	}
	
	public String get_stopId() {
		return stopId;
	}

	public List<String> getAllStops(){
		System.out.println("getAllStops");
        return allStops;
	}
	
	public List<String> getStopsOf(String text){
		System.out.println("getStopsOf");
        final String lowered = text.toLowerCase();
        return allStops.stream()
                .filter(item -> item.toLowerCase().contains(lowered))
                .collect(Collectors.toList());
	}
}
