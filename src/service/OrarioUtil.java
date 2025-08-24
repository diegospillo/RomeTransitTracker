package service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

/**
 * Raccolta di funzioni di utilità per la gestione degli orari.
 */
public class OrarioUtil {

    /**
     * Restituisce l'orario corrente formattato come {@code HH:mm:ss}.
     *
     * @return ora corrente formattata
     */
    public static String getOrarioCorrente() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

    /**
     * Restituisce l'epoch second corrente per il fuso orario di Roma.
     *
     * @return timestamp Unix in secondi
     */
    public static long getOrarioCorrenteEpochSeconds() {
        return ZonedDateTime.now(ZoneId.of("Europe/Rome")).toEpochSecond();
    }

    /**
     * Trova l'indice dell'orario più vicino non ancora passato in una lista.
     *
     * @param listaOrari lista di mappe contenenti l'orario con chiave "arr"
     * @return indice dell'orario futuro più vicino
     */
    public static int trovaIndexOrarioPiuVicino(List<Map<String, String>> listaOrari) {

        LocalTime orarioCorrente = LocalTime.now();
        long differenzaMinima = Long.MAX_VALUE;
        int indice = 0;

        for (Map<String, String> mappa : listaOrari) {
            String orarioStr = mappa.get("arr");
            if (orarioStr == null) continue;

            LocalTime orario = LocalTime.parse(orarioStr);

            if (!orario.isBefore(orarioCorrente)) { // solo orari >= now
                long differenza = Duration.between(orarioCorrente, orario).toSeconds();
                if (differenza < differenzaMinima) {
                    differenzaMinima = differenza;
                    indice = listaOrari.indexOf(mappa);
                }
            }
        }

        return indice;
    }

    /**
     * Variante di {@link #trovaIndexOrarioPiuVicino(List)} filtrata per trip.
     *
     * @param trip_id    identificativo del trip
     * @param listaOrari lista degli orari
     * @return indice dell'orario futuro più vicino relativo al trip
     */
    public static int trovaIndexOrarioPiuVicino(String trip_id,List<Map<String, String>> listaOrari) {

        LocalTime orarioCorrente = LocalTime.now();
        long differenzaMinima = Long.MAX_VALUE;
        int indice = 0;
        for (Map<String, String> mappa : listaOrari) {
                    String orarioStr = mappa.get("arr");
                    if (orarioStr == null) continue;

                    LocalTime orario = LocalTime.parse(orarioStr);

                    if (!orario.isBefore(orarioCorrente)) { // solo orari >= now
                        long differenza = Duration.between(orarioCorrente, orario).toSeconds();
                        if (differenza < differenzaMinima && trip_id.equals(mappa.get("trip_id"))) {
                            differenzaMinima = differenza;
                            indice = listaOrari.indexOf(mappa);
                        }
                    }
        }

        return indice;
    }

    /**
     * Converte un orario nel formato {@code HH:mm} in minuti totali.
     *
     * @param s stringa con l'orario
     * @return minuti dall'inizio del giorno
     */
    public static int toMins(String s) {
        String[] hourMin = s.split(":");
        int hour = Integer.parseInt(hourMin[0]);
        if(hour < 4){
            hour = hour+24;
        }
        int mins = Integer.parseInt(hourMin[1]);
        int hoursInMins = hour * 60;
        return hoursInMins + mins;
    }

    /**
     * Normalizza orari superiori alle 24 ore riportandoli alla scala 0-23.
     *
     * @param mytime orario nel formato {@code HH:mm:ss}
     * @return orario normalizzato, oppure {@code null} se il formato è errato
     */
    public static String format_times(String mytime){
        String[] splits = mytime.split(":");
        if(splits.length == 3){
            if(Integer.parseInt(splits[0]) >23){
                return "0"+(Integer.parseInt(splits[0])-24)+":"+splits[1]+":"+splits[2];
            }
            return splits[0]+":"+splits[1]+":"+splits[2];
        }
        return null;
    }
}
