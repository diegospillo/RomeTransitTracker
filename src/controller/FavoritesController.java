package controller;

import java.util.List;
import java.util.Set;
import java.util.ArrayList;

import model.ModelManager;
import model.Route;
import model.Stop;
import service.FavoritesManager;
import view.MainView;

/**
 * Controller responsible for handling the view of favourite lines and stops.
 * It converts the stored identifiers into user friendly labels that can be
 * displayed in the {@link MainView} list.
 */
public class FavoritesController {
    private final MainView mainView;
    private final FavoritesManager favoritesManager;
    private final ModelManager modelManager;

    /**
     * Crea il controller associato alla vista principale.
     *
     * @param mainView finestra principale su cui visualizzare i preferiti
     */
    public FavoritesController(MainView mainView) {
        this.mainView = mainView;
        this.favoritesManager = FavoritesManager.getInstance();
        this.modelManager = ModelManager.getInstance();
    }

    /**
     * Populate the favourites list in the {@link MainView} based on the
     * selected type.
     *
     * @param showLines true to show favourite lines, false for favourite stops
     */
    public void showFavorites(boolean showLines) {
        mainView.get_modelFavorites().clear();
        List<String> labels = showLines ? buildLineLabels() : buildStopLabels();
        for (String l : labels) {
            mainView.get_modelFavorites().addElement(l);
        }
    }

    /**
     * Costruisce le etichette da mostrare per le linee preferite.
     *
     * @return elenco formattato di linee
     */
    private List<String> buildLineLabels() {
        Set<String> lines = favoritesManager.getFavoriteLines();
        List<String> labels = new ArrayList<>();
        for (String id : lines) {
            Route r = modelManager.getRoutes().get(id);
            String name = (r != null) ? r.getLongName() : "";
            labels.add("[LINEA]" + "(" + id + ")/" + name);
        }
        return labels;
    }

    /**
     * Costruisce le etichette da mostrare per le fermate preferite.
     *
     * @return elenco formattato di fermate
     */
    private List<String> buildStopLabels() {
        Set<String> stops = favoritesManager.getFavoriteStops();
        List<String> labels = new ArrayList<>();
        for (String id : stops) {
            Stop s = modelManager.getStops().get(id);
            String name = (s != null) ? s.getName() : "";
            labels.add("[STOP]" + "(" + id + ")/" + name);
        }
        return labels;
    }
}