package model;

import service.DataRow;
import waypoint.MyWaypoint.PointType;

/**
 * Descrive una linea di trasporto pubblico (bus, metro, tram).
 */
public class Route {
        private String route_id;
    private String agency_id;
    private String route_short_name;
    private String route_long_name;
    private RouteType route_type;

    /**
     * Costruisce una route a partire da una riga del file GTFS.
     *
     * @param data riga del file {@code routes.txt}
     */
    Route(DataRow data){
        this.route_id = data.get("route_id");
        this.agency_id = data.get("agency_id");
        this.route_short_name = data.get("route_short_name");
        this.route_long_name = data.get("route_long_name");

        switch(Integer.parseInt(data.get("route_type"))) {
                case 0:
                        this.route_type = RouteType.TRAM;
                        break;
                case 1:
                        this.route_type = RouteType.METRO;
                        break;
                case 3:
                        this.route_type = RouteType.BUS;
                           break;
            default:
                this.route_type = RouteType.BUS; // fallback

        }
    }

    /** @return nome breve della linea */
    public String getName() {
        return route_short_name;
    }

    /** @return nome descrittivo della linea */
    public String getLongName() {
        return route_long_name;
    }

    /** @return tipologia di linea */
    public RouteType getRouteType() {
        return route_type;
    }

    /** @return colore associato alla linea */
    public String getColor() {
        return route_type.getColor();
    }

    /**
     * Restituisce il tipo di waypoint corrispondente alla route.
     *
     * @return tipo di waypoint
     */
    public PointType getPointType() {
            PointType pointType;
                switch(this.route_type) {
                        case Route.RouteType.BUS:
                                pointType = PointType.BUS;
                                break;
                        case Route.RouteType.TRAM:
                                pointType = PointType.TRAM;
                                break;
                        case Route.RouteType.METRO:
                                pointType = PointType.METRO;
                                break;
                        default:
                                pointType = PointType.BUS;
                }
                return pointType;
    }

    /**
     * Tipologie di linea supportate.
     */
    public static enum RouteType {
        BUS("#007bff"),     // blu
        METRO("#e74c3c"),   // rosso
        TRAM("#27ae60");    // verde

        private final String color;

        RouteType(String color) {
            this.color = color;
        }

        /** @return colore HTML associato */
        public String getColor() {
            return color;
        }
    }
}
