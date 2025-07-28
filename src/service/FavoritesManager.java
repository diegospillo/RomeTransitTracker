package service;

import java.io.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple manager for storing favourite lines and stops.
 * Favourites are persisted to a small local file.
 */
public class FavoritesManager {
    private static final FavoritesManager INSTANCE = new FavoritesManager();
    private static final String FILE_PATH = "favorites.txt";

    private final Set<String> favoriteLines = new LinkedHashSet<>();
    private final Set<String> favoriteStops = new LinkedHashSet<>();

    private FavoritesManager() {
        loadFavorites();
    }

    public static FavoritesManager getInstance() {
        return INSTANCE;
    }

    public void addLine(String lineId) {
        if (lineId != null && !lineId.isEmpty()) {
            favoriteLines.add(lineId);
            saveFavorites();
        }
    }

    public void addStop(String stopId) {
        if (stopId != null && !stopId.isEmpty()) {
            favoriteStops.add(stopId);
            saveFavorites();
        }
    }

    public Set<String> getFavoriteLines() {
        return new LinkedHashSet<>(favoriteLines);
    }

    public Set<String> getFavoriteStops() {
        return new LinkedHashSet<>(favoriteStops);
    }

    private void loadFavorites() {
        try (BufferedReader br = new BufferedReader(new FileReader(FILE_PATH))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.startsWith("L:")) {
                    favoriteLines.add(line.substring(2));
                } else if (line.startsWith("S:")) {
                    favoriteStops.add(line.substring(2));
                }
            }
        } catch (IOException ignore) {
            // File may not exist on first run
        }
    }

    private void saveFavorites() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(FILE_PATH))) {
            for (String l : favoriteLines) {
                pw.println("L:" + l);
            }
            for (String s : favoriteStops) {
                pw.println("S:" + s);
            }
        } catch (IOException ignore) {
            // Ignore write errors for simplicity
        }
    }
}