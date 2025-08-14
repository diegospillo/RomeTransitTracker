package model;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.transit.realtime.GtfsRealtime.TripDescriptor;

import service.DataRow;

public class Trip {
    private String trip_id;
    private String route_id;
    private String service_id;
    private String headsign;
    private String shape_id;
    private boolean direction;
    private List<StopTime> stopTimes = new ArrayList<>();
    private List<StopTime> stopTimesRealTime = new ArrayList<>();

    Trip(DataRow data){
    	this.trip_id = data.get("trip_id");
    	this.route_id = data.get("route_id");
    	this.service_id = data.get("service_id");
    	this.headsign = data.get("trip_headsign");
    	this.shape_id = data.get("shape_id");
    	this.direction = data.get("direction_id").equals("0");
    }
    
    public void AddStopTime(String stop_id,String arrival_time,String stopSequence) {
    	stopTimes.add(new StopTime(this.trip_id, stop_id, arrival_time, stopSequence));
    }
    public void addStopTimeRealTime(String stop_id,Long arrival_time,int stopSequence, int delay) {
    	stopTimesRealTime.add(new StopTime(this.trip_id, stop_id, arrival_time, stopSequence, delay));
    }
    
    public List<Long> getAllArrivalEpoch(){
    	List<Long> arrivalEpoch = new ArrayList<>();
    	for(StopTime stoptime : stopTimes) {
    		arrivalEpoch.add(stoptime.arrivalEpoch);
    	}
    	return arrivalEpoch;
    }
    
    public String getTripId(){
    	return trip_id;
    }
    public String getRouteId(){
    	return route_id;
    }
    public String getServiceId(){
    	return service_id;
    }
    public String getHeadsign(){
    	return headsign;
    }
    public String getShapeId(){
    	return shape_id;
    }
    public Boolean getDirection(){
    	return direction;
    }
    
   public List<StopTime> GetStopTimes() {
	   return stopTimes;
   }
   public List<StopTime> GetStopTimesRealTime() {
	   return stopTimesRealTime;
   }
   public void clearStopTimesRealTime() {
	   this.stopTimesRealTime.clear();
   }


    
}

