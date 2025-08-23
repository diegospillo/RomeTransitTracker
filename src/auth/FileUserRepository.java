package auth;

import java.io.*;
import java.nio.file.*;
import java.util.*;

public class FileUserRepository {
    private final Path filePath;

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

    public synchronized boolean usernameExists(String username) {
        return findByUsername(username) != null;
    }

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

    public record UserRecord(String username, String md5Hash) {}
}
