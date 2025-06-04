package waypoint;

import java.awt.Cursor;
import java.awt.Dimension;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JButton;

public class ButtonWaypoint extends JButton {

    public ButtonWaypoint(String filename, int dim) {
        setContentAreaFilled(false);
        setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(filename))));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setSize(new Dimension(dim, dim));
    }
}
