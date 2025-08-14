package controller;

import java.util.*;
import java.util.stream.Collectors;

import model.ModelManager;
import model.Trip;
import service.DataRow;
import service.GTFSManager;
import view.MainView;

public class LineController{
	
	private final GTFSManager gtfsManager;
	private final ModelManager modelManager;
	private final MainView mainView;
	private String route_id;
    private String shape_id;
    private String nome_linea;
    private Map<String,Trip> trips;
    private boolean direction = true;
    private final List<String> allLines;
    private boolean selectedTrip = false;
	
	
    public LineController(MainView mainView){
        this.gtfsManager = GTFSManager.getInstance();
        this.modelManager = ModelManager.getInstance();
        this.mainView = mainView;
        this.allLines = buildAllLines();
    }

	private List<String> buildAllLines(){
        Set<String> uniqueLines = new HashSet<>();
        for (DataRow row : gtfsManager.getRoutes().dataList()) {
                String long_name = row.get("route_long_name");
                String my_route_id = row.get("route_id");
                String item = "(" + my_route_id + ") " + long_name;
                if(my_route_id != null){
                        uniqueLines.add(item);
                }
        }
        List<String> sortedLines = new ArrayList<>(uniqueLines);
        Collections.sort(sortedLines);
        return sortedLines;
	}
	
	public List<String> getAllLines(){
        return new ArrayList<>(allLines);
	}

	public List<String> getLinesOf(String text){
        final String lowered = text.toLowerCase();
        return allLines.stream()
                .filter(item -> item.toLowerCase().contains(lowered))
                .collect(Collectors.toList());
	}
	
	public void viewRouteByName(String routeId, Boolean direction) {
		
		System.out.println("viewRouteByName: " + routeId + " direction: " + direction);
        this.route_id = routeId;
        this.direction = direction;
        Map<String,Trip> preTrips = modelManager.getTripsByRouteId(routeId, direction);
        this.nome_linea = modelManager.getTrips().get(preTrips.keySet().toArray()[0]).getHeadsign();
        List<String> shapesId = modelManager.getShapeIdsByTrips(preTrips.keySet());
        this.shape_id = shapesId.getFirst();
        System.out.println(shape_id);
        this.trips = ModelManager.getTripsByShapeId(preTrips, shape_id);
    }
	
	public void viewRouteTrip(String tripId) {
		Map<String,Trip> curTrips = new HashMap<>();
		Trip trip = modelManager.getTrips().get(tripId);
		curTrips.put(tripId, trip);
		this.trips = new HashMap<>(curTrips);
	}
	
	public void showLinea(boolean isSelectedTrip){
		this.selectedTrip = isSelectedTrip;
		if(!isSelectedTrip) {
			mainView.get_lblDettagli().setText("Linea " + route_id);
		}
		else {
			mainView.get_lblDettagli().setText("Linea: " + route_id + "  TripID: " + trips.keySet().toArray()[0]);
		}
		mainView.get_lblLinea().setText(nome_linea);
		mainView.get_lblDescription().setText("");
		mainView.get_btnInvertiDirezione().setVisible(true);
		mainView.get_btnLive().setVisible(true);
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
	
	public Map<String,Trip> get_trips() {
		return trips;
	}
	public boolean getSelectedTrip() {
		return selectedTrip;
	}
	
}
