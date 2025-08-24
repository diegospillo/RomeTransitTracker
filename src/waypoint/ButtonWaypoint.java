package waypoint;

import java.awt.Cursor;
import java.awt.Dimension;
import java.util.Objects;
import javax.swing.ImageIcon;
import javax.swing.JButton;

/**
 * Bottone personalizzato usato come marker sulla mappa.
 */
public class ButtonWaypoint extends JButton {

    /**
     * Crea un bottone waypoint caricando un'icona dalla risorsa specificata.
     *
     * @param filename percorso dell'icona
     * @param dim      dimensione del bottone
     */
    public ButtonWaypoint(String filename, int dim) {
        setContentAreaFilled(false);
        setIcon(new ImageIcon(Objects.requireNonNull(getClass().getResource(filename))));
        setCursor(new Cursor(Cursor.HAND_CURSOR));
        setSize(new Dimension(dim, dim));
    }
}
