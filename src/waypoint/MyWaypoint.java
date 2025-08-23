package waypoint;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import javax.swing.JButton;
import org.jxmapviewer.viewer.DefaultWaypoint;
import org.jxmapviewer.viewer.GeoPosition;

public class MyWaypoint extends DefaultWaypoint {

    public PointType getPointType() {
        return pointType;
    }

    public void setPointType(PointType pointType) {
        this.pointType = pointType;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public JButton getButton() {
        return button;
    }

    public void setButton(JButton button) {
        this.button = button;
    }

    public Integer getIndex(){
        return index;
    }

    public MyWaypoint(Integer index, String name, PointType pointType, EventWaypoint event, GeoPosition coord) {
        super(coord);
        this.index = index;
        this.name = name;
        this.pointType = pointType;
        initButton(event);
    }

    public MyWaypoint() {
    }

    private String name;
    private JButton button;
    private PointType pointType;
    private Integer index;

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

    public static enum PointType {
        START, END, STOPS, BUS, METRO, TRAM, PING
    }
}
