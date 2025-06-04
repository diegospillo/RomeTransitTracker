package controller;

import java.util.HashSet;
import java.util.Set;

import org.jxmapviewer.viewer.GeoPosition;

import service.DataRow;
import service.GTFSManager;
import service.Stop_Times;
import view.MainView;
import waypoint.MyWaypoint;

public class StopController {
	private final GTFSManager gtfsManager;
	private final MainView mainView;
	private final Set<MyWaypoint> waypoints = new HashSet<>();
	
	public StopController(MainView mainView){
		this.gtfsManager = GTFSManager.getInstance();
		this.mainView = mainView;
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
                item.setCurrentTimeIndex();
            }
            else if(item.getTimepoint().equals("1")) {
                //point = MyWaypoint.PointType.END;
                point = MyWaypoint.PointType.STOPS;
            }
            else {
                point = MyWaypoint.PointType.STOPS;
            }
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
	
	public Set<MyWaypoint> get_Waypoints() {
		return waypoints;
	}
}
