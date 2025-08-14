package model;

import service.DataRow;

public class Stop {
    private String stop_id;
    private String name;
    private double lat;
    private double lon;

    Stop(DataRow data){
    	this.stop_id = data.get("stop_id");
    	this.name = data.get("stop_name");
    	this.lat = Double.parseDouble(data.get("stop_lat"));
    	this.lon = Double.parseDouble(data.get("stop_lon"));
    }
    
    public String getStopId() {
    	return stop_id;
    }
    public String getName() {
    	return name;
    }
    public double getLat() {
    	return lat;
    }
    public double getLon() {
    	return lon;
    }
}
