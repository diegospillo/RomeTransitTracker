package auth;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * Repository su file per la persistenza degli utenti registrati.
 */
public class FileUserRepository {
    private final Path filePath;

    /**
     * Inizializza il repository creando cartella e file se assenti.
     *
     * @param filename nome del file su disco dove memorizzare gli utenti
     */
    public FileUserRepository(String filename) {
        Path dir = Paths.get("user_data");  // cartella per i dati
        try {
            if (!Files.exists(dir)) {
                Files.createDirectories(dir);  // crea la cartella se non esiste
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore creazione cartella user_data", e);
        }

        this.filePath = dir.resolve(filename);
        try {
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Errore inizializzazione file utenti", e);
        }
    }

    /**
     * Verifica se esiste già un utente con lo username fornito.
     *
     * @param username nome utente da cercare
     * @return {@code true} se lo username è presente
     */
    public synchronized boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

    /**
     * Crea un nuovo record utente persistendolo su file.
     *
     * @param username nome utente
     * @param md5Hash  hash MD5 della password
     * @throws IllegalStateException se lo username è già presente
     */
    public synchronized void createUser(String username, String md5Hash) {
        if (usernameExists(username)) {
            throw new IllegalStateException("Username già esistente");
        }
        String line = username + ";" + md5Hash;
        try (BufferedWriter bw = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND)) {
            bw.write(line);
            bw.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Errore scrittura utente", e);
        }
    }

    /**
     * Recupera le informazioni di un utente dal file.
     *
     * @param username nome utente
     * @return record dell'utente oppure {@code null} se non esiste
     */
    public synchronized UserRecord findByUsername(String username) {
        try (BufferedReader br = Files.newBufferedReader(filePath)) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(";");
                if (parts.length == 2 && parts[0].equals(username)) {
                    return new UserRecord(parts[0], parts[1]);
                }
            }
            return null;
        } catch (IOException e) {
            throw new RuntimeException("Errore lettura file utenti", e);
        }
    }

    /**
     * Record che rappresenta un utente memorizzato.
     *
     * @param username nome dell'utente
     * @param md5Hash  hash MD5 della password
     */
    public record UserRecord(String username, String md5Hash) {}
}
