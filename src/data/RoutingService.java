package data;

import java.util.*;

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
}
