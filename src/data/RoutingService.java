package data;

import java.awt.Color;
import java.util.*;

import model.ModelManager;
import service.DataGTFS;
import service.DataRow;

/**
 * Utility per l'estrazione dei dati di instradamento dalle tabelle GTFS e per
 * la determinazione del colore della linea.
 */
public class RoutingService {

    /**
     * Filtra i punti di un percorso in base allo {@code shape_id} e li ordina
     * per sequenza.
     *
     * @param shapes    dataset contenente le shape
     * @param shapes_id identificativo della shape da estrarre
     * @return dataset ordinato dei punti del percorso
     */
    public static DataGTFS routing(DataGTFS shapes,String shapes_id) {
        DataGTFS my_shapes = new DataGTFS(new ArrayList<>());
        for (DataRow row: shapes.dataList()) {
            if (shapes_id.equals(row.get("shape_id"))) {
                my_shapes.add(row);
            }
        }
        my_shapes.sort(Comparator.comparing(m -> Integer.parseInt(m.get("shape_pt_sequence"))));
        return my_shapes;
    }
    
    /**
     * Determina il colore associato a una linea.
     *
     * @param routeId identificativo della linea
     * @return colore della linea oppure rosso di default
     */
    public static Color getColorRoutingType(String routeId) {
        if (routeId != null) {
            String color = ModelManager.getInstance()
                                       .getRoutes()
                                       .get(routeId)
                                       .getColor();
            if (color != null) {
                return Color.decode(color);
            }
        }
        return new Color(153,0,0); // colore di fallback
    }
}
