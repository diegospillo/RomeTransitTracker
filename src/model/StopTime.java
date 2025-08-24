package model;

import java.time.*;
import java.time.format.DateTimeFormatter;

/**
 * Orario di passaggio di un trip presso una fermata.
 */
public class StopTime {
    String trip_id;
    String stop_id;
    int stopSequence;
    long arrivalEpoch;
    int delaySeconds;
    boolean isPassed = false;

    /**
     * Costruisce un orario programmato partendo da stringhe GTFS.
     */
    StopTime(String tripId,String stopId,String arrival_time,String stopSequence){
        this.trip_id = tripId;
        this.stop_id = stopId;
        this.stopSequence = Integer.parseInt(stopSequence);
        this.arrivalEpoch = parseArrivalTimeToEpoch(arrival_time);
        this.delaySeconds = 0;
    }

    /**
     * Costruisce un orario con informazioni in tempo reale.
     */
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


    /** @return trip associato */
    public String getTripId() {
        return trip_id;
    }
    /** @return fermata associata */
    public String getStopId() {
        return stop_id;
    }
    /** @return sequenza della fermata */
    public int getStopSequence() {
        return stopSequence;
    }
    /** @return orario di arrivo in epoch */
    public long getArrivalEpoch() {
        return arrivalEpoch;
    }
    /** @return ritardo in secondi */
    public int getDelaySeconds() {
        return delaySeconds;
    }
    /** @return {@code true} se l'arrivo è già passato */
    public boolean getIsPassed() {
        return isPassed;
    }
    /**
     * @return orario di arrivo formattato
     */
    public String getOrario() {
        String orario = Instant.ofEpochSecond(this.arrivalEpoch + this.delaySeconds)
                .atZone(ZoneId.of("Europe/Rome"))
                .toLocalTime()
                .format(DateTimeFormatter.ofPattern("HH:mm"));
        return orario;
    }
    /**
     * @return stringa che indica il ritardo in minuti, se presente
     */
    public String getDelayMinutes() {
        if (this.delaySeconds >= 60) {
                return " (+ " + (this.delaySeconds / 60) + " min)";
        }
        return "";
    }
    /**
     * Calcola la previsione di arrivo rispetto all'ora attuale.
     *
     * @return stringa descrittiva del tempo rimanente
     */
    public String getArrivalPrediction() {
        long now = Instant.now().getEpochSecond();

        long adjustedArrival = arrivalEpoch + delaySeconds;

        long diffMillis = adjustedArrival - now;
        long minutes = diffMillis / 60;

        if (minutes <= 0) {
            return "In arrivo";
        } else if (minutes == 1) {
            return "1 min";
        } else {
            return minutes + " min";
        }
    }

    /**
     * Crea l'etichetta da mostrare nella vista delle linee.
     */
    public String getLabelLinea(String stopName,boolean isLive) {
        String orario = isLive ? getArrivalPrediction() : getOrario();
        String label = orario + "/" + stopName;

        return label;
    }

    /**
     * Crea l'etichetta da mostrare nella vista della fermata.
     */
    public String getLabelFermata(String routeId,String headsign,boolean isLive) {
        String orario = isLive ? getArrivalPrediction() : getOrario();
        String label = routeId + "/" + headsign + " " + orario;

        return label;
    }

    /**
     * Differenza in minuti tra ora corrente e arrivo previsto.
     */
    public Long getDiffMinutes() {
        long now = Instant.now().getEpochSecond();
        long adjustedArrival = arrivalEpoch + delaySeconds;
        long diffMillis = adjustedArrival - now;
        long minutes = diffMillis / 60;
        return minutes;
    }
}
