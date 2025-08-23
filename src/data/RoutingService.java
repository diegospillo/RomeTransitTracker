package data;

import java.awt.Color;
import java.util.*;

import model.ModelManager;
import service.DataGTFS;
import service.DataRow;

public class RoutingService {

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
