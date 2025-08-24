package service;

import java.io.*;
import java.nio.file.*;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Gestisce l'elenco di linee e fermate preferite dell'utente.
 * <p>
 * Le informazioni vengono serializzate su un piccolo file locale per ogni
 * utente, consentendo di mantenere le preferenze tra le sessioni
 * dell'applicazione Rome Transit Tracker.
 */
public class FavoritesManager {
    private static final FavoritesManager INSTANCE = new FavoritesManager();
    private static final String FILE_PREFIX = "favorites_";
    private static final Path DATA_DIR = Paths.get("user_data"); // cartella per i dati

    private final Set<String> favoriteLines = new LinkedHashSet<>();
    private final Set<String> favoriteStops = new LinkedHashSet<>();
    private String currentUser;

    /**
     * Costruttore privato per implementare il pattern Singleton.
     * Carica eventuali preferenze già salvate sul file system.
     */
    private FavoritesManager() {
        loadFavorites();
    }

    /**
     * Imposta l'utente corrente e carica le relative preferenze dal disco.
     *
     * @param username nome dell'utente loggato
     */
    public synchronized void setCurrentUser(String username) {
        this.currentUser = username;
        loadFavorites();
    }

    /**
     * Restituisce l'istanza unica del manager.
     *
     * @return singleton di {@code FavoritesManager}
     */
    public static FavoritesManager getInstance() {
        return INSTANCE;
    }

    /**
     * Aggiunge una linea alle preferite e persiste la modifica su disco.
     *
     * @param lineId identificativo della linea
     */
    public void addLine(String lineId) {
        if (lineId != null && !lineId.isEmpty()) {
            favoriteLines.add(lineId);
            saveFavorites();
        }
    }

    /**
     * Aggiunge una fermata alle preferite e aggiorna il file di
     * persistenza.
     *
     * @param stopId identificativo della fermata
     */
    public void addStop(String stopId) {
        if (stopId != null && !stopId.isEmpty()) {
            favoriteStops.add(stopId);
            saveFavorites();
        }
    }

    /**
     * Rimuove una linea dalle preferite (se presente) e salva il nuovo
     * stato sul file.
     *
     * @param lineId identificativo della linea da rimuovere
     */
    public void removeLine(String lineId) {
        if (lineId != null && !lineId.isEmpty()) {
            boolean changed = favoriteLines.remove(lineId);
            if (changed) {
                saveFavorites();
            }
        }
    }

    /**
     * Rimuove una fermata dalle preferite (se presente) e aggiorna il
     * file di persistenza.
     *
     * @param stopId identificativo della fermata da rimuovere
     */
    public void removeStop(String stopId) {
        if (stopId != null && !stopId.isEmpty()) {
            boolean changed = favoriteStops.remove(stopId);
            if (changed) {
                saveFavorites();
            }
        }
    }

    /**
     * Restituisce l'elenco delle linee preferite dell'utente corrente.
     *
     * @return insieme immutabile degli ID delle linee preferite
     */
    public Set<String> getFavoriteLines() {
        return new LinkedHashSet<>(favoriteLines);
    }

    /**
     * Restituisce l'elenco delle fermate preferite dell'utente corrente.
     *
     * @return insieme immutabile degli ID delle fermate preferite
     */
    public Set<String> getFavoriteStops() {
        return new LinkedHashSet<>(favoriteStops);
    }

    /**
     * Carica da file le preferenze dell'utente corrente, popolando gli
     * insiemi interni.
     */
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

    /**
     * Scrive su disco lo stato corrente delle preferenze dell'utente.
     */
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

    /**
     * Costruisce il percorso del file di preferenze relativo all'utente
     * corrente.
     *
     * @return percorso del file oppure {@code null} se nessun utente è
     *         impostato
     */
    private Path getFilePath() {
        return (currentUser == null) ? null : DATA_DIR.resolve(FILE_PREFIX + currentUser + ".txt");
    }
}
