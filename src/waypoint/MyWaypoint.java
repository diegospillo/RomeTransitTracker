package waypoint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

/**
 * Waypoint personalizzato con informazioni aggiuntive e pulsante associato.
 */
public class MyWaypoint extends DefaultWaypoint {

    /**
     * Restituisce il tipo di punto rappresentato.
     *
     * @return tipo del waypoint
     */
    public PointType getPointType() {
        return pointType;
    }

    /**
     * Imposta il tipo del waypoint.
     *
     * @param pointType nuovo tipo
     */
    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }

    /**
     * @return nome associato al waypoint
     */
    public String getName() {
        return name;
    }

    /**
     * Imposta il nome del waypoint.
     *
     * @param name nuovo nome
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return bottone grafico associato
     */
    public JButton getButton() {
        return button;
    }

    /**
     * Imposta il bottone associato.
     *
     * @param button componente grafico
     */
    public void setButton(JButton button) {
        this.button = button;
    }

    /**
     * @return indice del waypoint nella lista visualizzata
     */
    public Integer getIndex(){
        return index;
    }

    /**
     * Costruttore completo per inizializzare un waypoint.
     *
     * @param index     posizione nell'elenco
     * @param name      nome del punto
     * @param pointType tipo di waypoint
     * @param event     listener per la selezione
     * @param coord     coordinate geografiche
     */
    public MyWaypoint(Integer index, String name, PointType pointType, EventWaypoint event, GeoPosition coord) {
        super(coord);
        this.index = index;
        this.name = name;
        this.pointType = pointType;
        initButton(event);
    }

    /** Costruttore senza argomenti. */
    public MyWaypoint() {
    }

    private String name;
    private JButton button;
    private PointType pointType;
    private Integer index;

    /**
     * Inizializza il bottone grafico in base al tipo di waypoint.
     *
     * @param event listener da notificare alla selezione
     */
    private void initButton(EventWaypoint event) {
        switch (pointType) {
            case END:
                button = new ButtonWaypoint("/icon/arrivom.png",25);
                break;
            case START:
                button = new ButtonWaypoint("/icon/pin.png",24);
                break;
            case STOPS:
                button = new ButtonWaypoint("/icon/stop.png",16);
                break;
            case BUS:
                button = new ButtonWaypoint("/icon/bus.png",24);
                break;
            case METRO:
                button = new ButtonWaypoint("/icon/metro.png",24);
                break;
            case TRAM:
                button = new ButtonWaypoint("/icon/tram.png",24);
                break;
            case PING:
                button = new ButtonWaypoint("/icon/pin.png",24);

        }
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.setOpaque(false);
        if(pointType != PointType.PING){
                button.addActionListener(new ActionListener() {
                    @Override
                    public void actionPerformed(ActionEvent ae) {
                        System.out.println(MyWaypoint.this.index);
                        event.selected(MyWaypoint.this);
                    }
                });
        }
    }

    /**
     * Tipologie di waypoint supportate.
     */
    public static enum PointType {
        START, END, STOPS, BUS, METRO, TRAM, PING
    }
}
