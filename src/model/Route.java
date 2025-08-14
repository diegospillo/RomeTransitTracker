package model;

import service.DataRow;

public class Route {
	private String route_id;
    private String agency_id;
    private String route_short_name;
    private String route_long_name;
    
    Route(DataRow data){
    	this.route_id = data.get("route_id");
    	this.agency_id = data.get("agency_id");
    	this.route_short_name = data.get("route_short_name");
    	this.route_long_name = data.get("route_long_name");
    }
    
    public String getName() {
    	return route_short_name;
    }
}
