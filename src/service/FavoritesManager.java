package service;

import java.io.*;
import java.nio.file.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Simple manager for storing favourite lines and stops.
 * Favourites are persisted to a small local file per user.
 */
public class FavoritesManager {
    private static final FavoritesManager INSTANCE = new FavoritesManager();
    private static final String FILE_PREFIX = "favorites_";
    private static final Path DATA_DIR = Paths.get("user_data"); // cartella per i dati

    private final Set<String> favoriteLines = new LinkedHashSet<>();
    private final Set<String> favoriteStops = new LinkedHashSet<>();
    private String currentUser;

    private FavoritesManager() {
        loadFavorites();
    }

    public synchronized void setCurrentUser(String username) {
        this.currentUser = username;
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

    /** Remove a line from favourites (if present) and persist to file. */
    public void removeLine(String lineId) {
        if (lineId != null && !lineId.isEmpty()) {
            boolean changed = favoriteLines.remove(lineId);
            if (changed) {
                saveFavorites();
            }
        }
    }

    /** Remove a stop from favourites (if present) and persist to file. */
    public void removeStop(String stopId) {
        if (stopId != null && !stopId.isEmpty()) {
            boolean changed = favoriteStops.remove(stopId);
            if (changed) {
                saveFavorites();
            }
        }
    }

    public Set<String> getFavoriteLines() {
        return new LinkedHashSet<>(favoriteLines);
    }

    public Set<String> getFavoriteStops() {
        return new LinkedHashSet<>(favoriteStops);
    }

    private void loadFavorites() {
        favoriteLines.clear();
        favoriteStops.clear();
        Path path = getFilePath();
        if (path == null) return;
        if (!Files.exists(path)) return;

        try (BufferedReader br = Files.newBufferedReader(path)) {
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
        Path path = getFilePath();
        if (path == null) return;

        try (BufferedWriter bw = Files.newBufferedWriter(path,
                StandardOpenOption.CREATE,
                StandardOpenOption.TRUNCATE_EXISTING)) {

            for (String l : favoriteLines) {
                bw.write("L:" + l);
                bw.newLine();
            }
            for (String s : favoriteStops) {
                bw.write("S:" + s);
                bw.newLine();
            }
        } catch (IOException ignore) {
            // Ignore write errors for simplicity
        }
    }

    private Path getFilePath() {
        return (currentUser == null) ? null : DATA_DIR.resolve(FILE_PREFIX + currentUser + ".txt");
    }
}
