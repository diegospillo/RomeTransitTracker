package controller;

import java.util.*;

import service.DataGTFS;
import service.GTFSManager;
import service.GTFSReader;
import service.Stop_Times;
import view.MainView;

public class LineController{
	
	private final GTFSManager gtfsManager;
	private final MainView mainView;
	private String route_id;
	private String shape_id;
	private String nome_linea;
	private boolean direction = true;
	
	
	public LineController(MainView mainView){
		this.gtfsManager = GTFSManager.getInstance();
		this.mainView = mainView;
	}
	
	public void viewRouteByName(String routeName, Boolean direction) {
        this.route_id = routeName;
        this.direction = direction;

        System.out.println("viewRouteByName: " + route_id);

        Set<String> routes_id = gtfsManager.getFilterRouteByRouteName(gtfsManager.getRoutes(),routeName);
        Set<String> service_id = gtfsManager.getFilterService_idByDate(gtfsManager.getCalendarDates(),"20250503");

        DataGTFS tripsFilter = gtfsManager.getFilterTripsByRoute_id(gtfsManager.getTrips(), routes_id, direction, service_id);
        nome_linea = tripsFilter.dataList().getFirst().get("trip_headsign").replace("\"","");
        List<String> shapesId = gtfsManager.getShapesId(tripsFilter);
        this.shape_id = shapesId.getFirst();
        Set<String> trips_id = gtfsManager.getFilterTripsByShape_id(tripsFilter,shape_id);

        Stop_Times.setStops_times(GTFSReader.filterStop_timesByTrips_id(trips_id));
    }
	
	public void showLinea(){
		mainView.get_lblLinea().setText(nome_linea);
		mainView.get_lblDettagli().setText("Linea " + route_id);
		mainView.get_lblDescription().setText("");
		mainView.get_btnInvertiDirezione().setVisible(true);
		mainView.get_btnIndietro().setVisible(false);
		mainView.get_btnMoreInfo().setVisible(false);
		mainView.get_btnCloseSidePanel().setVisible(true);
		mainView.get_lblDettagli().setVisible(true);
		mainView.get_scrollPanel().setViewportView(mainView.get_fermateList());
		mainView.adjustSidePanelWidth();
    }
	
	public boolean get_direction() {
		return direction;
	}
	
	public String get_route_id() {
		return route_id;
	}
	
	public String get_shape_id() {
		return shape_id;
	}
	
	public String get_nome_linea() {
		return nome_linea;
	}
	
}
