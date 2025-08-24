package waypoint;

/**
 * Listener per la selezione di un waypoint sulla mappa.
 */
public interface EventWaypoint {

    /**
     * Invocato quando un waypoint viene selezionato.
     *
     * @param waypoint waypoint selezionato
     */
    public void selected(MyWaypoint waypoint);
}
