package data;

import org.jxmapviewer.painter.CompoundPainter;
import org.jxmapviewer.painter.Painter;
import org.jxmapviewer.JXMapViewer;
import org.jxmapviewer.viewer.GeoPosition;
import org.jxmapviewer.viewer.WaypointPainter;

import service.DataGTFS;
import service.DataRow;
import waypoint.MyWaypoint;
import waypoint.WaypointRender;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.util.*;

/**
 * Estensione personalizzata di {@link JXMapViewer} con supporto ai waypoint e
 * al disegno dei percorsi delle linee del trasporto pubblico.
 */
public class JXMapViewerCustom extends JXMapViewer {

	private WaypointPainter<MyWaypoint> stopPainter;
    private WaypointPainter<MyWaypoint> busPainter;
    private WaypointPainter<MyWaypoint> pingPainter;
    
    /**
     * Imposta il painter per le fermate sulla mappa.
     *
     * @param stopWaypoints insieme di waypoint delle fermate
     */
    public void setStopPainter(Set<MyWaypoint> stopWaypoints) {
        stopPainter = new WaypointRender();
        stopPainter.setWaypoints(stopWaypoints);
        updatePainters();
    }

    /**
     * Imposta il painter per i bus sulla mappa.
     *
     * @param busWaypoints waypoint dei bus
     */
    public void setBusPainter(Set<MyWaypoint> busWaypoints) {
        if (busPainter == null) {
            busPainter = new WaypointRender();
        }
        busPainter.setWaypoints(busWaypoints);
        updatePainters();
    }
    
    /**
     * Imposta il painter per i waypoint di ping.
     *
     * @param pingWaypoints waypoint da evidenziare
     */
    public void setPingPainter(Set<MyWaypoint> pingWaypoints) {
        if (pingPainter == null) {
                pingPainter = new WaypointRender();
        }
        pingPainter.setWaypoints(pingWaypoints);
        updatePainters();
    }

    /**
     * Ricompone e applica il painter delle sovrapposizioni.
     */
    private void updatePainters() {
        List<Painter<JXMapViewer>> painters = new ArrayList<>();
        if (stopPainter != null) painters.add(stopPainter);
        if (busPainter != null) painters.add(busPainter);
        if (pingPainter != null) painters.add(pingPainter);
        CompoundPainter<JXMapViewer> compoundPainter = new CompoundPainter<>(painters);
        this.setOverlayPainter(compoundPainter);
        repaint();
    }
    
    /**
     * Restituisce i dati del percorso attualmente disegnato.
     *
     * @return oggetto {@link DataGTFS} del percorso
     */
    public DataGTFS getRoutingData() {
        return routingData;
    }

    /**
     * Definisce i dati e il colore del percorso da visualizzare.
     *
     * @param routingData dati del percorso
     * @param colorRouteType colore associato al tipo di linea
     */
    public void setRoutingData(DataGTFS routingData,Color colorRouteType) {
        this.routingData = routingData;
        this.colorRouteType = colorRouteType;
        repaint();
    }
    
    /**
     * Elimina il percorso corrente dalla mappa.
     */
    public void clearRoutingData() {
        if(this.routingData!=null) {
                this.routingData.clear();
                repaint();
        }
    }

    private DataGTFS routingData;
    private Color colorRouteType;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (routingData != null){
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            Path2D p2 = new Path2D.Double();
            first = true;
            for (DataRow d : routingData.dataList()) {
                draw(p2, d, g2);
            }
            //g2.setColor(new Color(28, 23, 255));
            g2.setColor(colorRouteType);
            g2.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(p2);
            g2.dispose();
        }
    }

    private boolean first = true;
    private Point2D prevPoint;

    /**
     * Disegna un segmento del percorso sul painter.
     *
     * @param p2 percorso da popolare
     * @param d  riga dei dati GTFS
     * @param g2 contesto grafico
     */
    private void draw(Path2D p2, DataRow d, Graphics2D g2) {
        Point2D point = convertGeoPositionToPoint(new GeoPosition(Double.parseDouble(d.get("shape_pt_lat")),Double.parseDouble(d.get("shape_pt_lon"))));
        if (first) {
            first = false;
            p2.moveTo(point.getX(), point.getY());
            prevPoint = point;
        } else {
            p2.lineTo(point.getX(), point.getY());
            if (getZoom()<=5 && prevPoint!=null && prevPoint.distance(point) > 35) {
                drawArrow(g2, prevPoint, point);
                prevPoint = point;
            }
        }
    }

    /**
     * Disegna una freccia direzionale fra due punti del percorso.
     */
    private void drawArrow(Graphics2D g2, Point2D from, Point2D to) {
        double angle = Math.atan2(to.getY() - from.getY(), to.getX() - from.getX());
        int arrowSize = 10;
        int arrowWidth = 6;
        double arrowAngle = Math.PI / (6 - (arrowWidth / 10.0));

        double x1 = to.getX() - arrowSize * Math.cos(angle - arrowAngle);
        double y1 = to.getY() - arrowSize * Math.sin(angle - arrowAngle);
        double x2 = to.getX() - arrowSize * Math.cos(angle + arrowAngle);
        double y2 = to.getY() - arrowSize * Math.sin(angle + arrowAngle);

        Path2D arrow = new Path2D.Double();
        arrow.moveTo(to.getX(), to.getY());
        arrow.lineTo(x1, y1);
        arrow.moveTo(to.getX(), to.getY());
        arrow.lineTo(x2, y2);

        g2.setStroke(new BasicStroke(2f));
        g2.setColor(Color.BLACK);
        g2.draw(arrow);
    }
}
