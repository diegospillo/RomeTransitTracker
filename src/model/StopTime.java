package model;

import java.time.*;
import java.time.format.DateTimeFormatter;

public class StopTime {
    String trip_id;
    String stop_id;
    int stopSequence;
    long arrivalEpoch;
    int delaySeconds;
    boolean isPassed = false;
    
    StopTime(String tripId,String stopId,String arrival_time,String stopSequence){
    	this.trip_id = tripId;
    	this.stop_id = stopId;
    	this.stopSequence = Integer.parseInt(stopSequence);
    	this.arrivalEpoch = parseArrivalTimeToEpoch(arrival_time);
    	this.delaySeconds = 0;
    }
    
    StopTime(String tripId, String stopId,Long arrival_time,int stopSequence, int delay){
    	this.trip_id = tripId;
    	this.stop_id = stopId;
    	this.stopSequence = stopSequence;
    	this.arrivalEpoch = arrival_time;
    	this.delaySeconds = delay;
    }
    
    private long parseArrivalTimeToEpoch(String arrivalTime) {
        try {
            String[] parts = arrivalTime.split(":");
            int hours = Integer.parseInt(parts[0]);
            int minutes = Integer.parseInt(parts[1]);
            int seconds = Integer.parseInt(parts[2]);

            // Usa la data di oggi come riferimento
            LocalDate baseDate = LocalDate.now();
            LocalTime baseTime = LocalTime.of(hours % 24, minutes, seconds);
            LocalDateTime dateTime = LocalDateTime.of(baseDate, baseTime);

            // Se l'orario è >= 24:00:00, aggiungi giorni extra
            int extraDays = hours / 24;
            if (extraDays > 0) {
                dateTime = dateTime.plusDays(extraDays);
            }

            // Converti in epoch (Europe/Rome)
            return dateTime.atZone(ZoneId.of("Europe/Rome")).toEpochSecond();

        } catch (Exception e) {
            System.err.println("Errore nel parsing dell'orario: " + arrivalTime);
            return -1;
        }
    }
    
   
    public String getTripId() {
    	return trip_id;
    }
    public String getStopId() {
    	return stop_id;
    }
    public int getStopSequence() {
    	return stopSequence;
    }
    public long getArrivalEpoch() {
    	return arrivalEpoch;
    }
    public int getDelaySeconds() {
    	return delaySeconds;
    }
    public boolean getIsPassed() {
    	return isPassed;
    }
    public String getOrario() {
    	String orario = Instant.ofEpochSecond(this.arrivalEpoch + this.delaySeconds)
                .atZone(ZoneId.of("Europe/Rome"))
                .toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
    	return orario;
    }
    public String getDelayMinutes() {
    	if (this.delaySeconds >= 60) {
    		return " (+ " + (this.delaySeconds / 60) + " min)";
    	}
    	return "";
    }
    public String getArrivalPrediction() {
    	long now = Instant.now().getEpochSecond();

        // arrivalEpoch è il tempo previsto originario, delaySeconds è il ritardo in secondi
        long adjustedArrival = arrivalEpoch + delaySeconds;

        // calcola la differenza in minuti
        long diffMillis = adjustedArrival - now;
        long minutes = diffMillis / 60; // 1 minuto = 60.000 ms

        if (minutes <= 0) {
            return "In arrivo";
        } else if (minutes == 1) {
            return "1 min";
        } else {
            return minutes + " min";
        }
    }
    
    public String getLabelLinea(String stopName,boolean isLive) {
    	String orario = isLive ? getArrivalPrediction() : getOrario();
        String label = orario + "/" + stopName;
        
        //label += getDelayMinutes();

        return label;
    }
    
    public String getLabelFermata(String routeId,String headsign,boolean isLive) {
    	String orario = isLive ? getArrivalPrediction() : getOrario();
        String label = routeId + "/" + headsign + " " + orario;
        //label += getDelayMinutes();

        return label;
    }


}
