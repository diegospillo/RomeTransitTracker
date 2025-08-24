package service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Fornisce la data corrente formattata secondo lo standard {@code yyyyMMdd}.
 */
public class CurrentDateProvider {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Restituisce la data odierna nel formato richiesto.
     *
     * @return data corrente come stringa (es. {@code 20250730})
     */
    public static String getCurrentDateFormatted() {
        return LocalDate.now().format(FORMATTER);
    }
}
