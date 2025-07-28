package service;

import java.time.Duration;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class OrarioUtil {

    public static String getOrarioCorrente() {
        return LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
    }

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
