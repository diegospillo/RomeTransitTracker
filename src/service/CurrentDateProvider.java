package service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * CurrentDateProvider → Fornisce la data corrente in formato "yyyyMMdd"
 */
public class CurrentDateProvider {
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");

    /**
     * Restituisce la data corrente nel formato "yyyyMMdd"
     * @return data corrente come stringa (es. "20250730")
     */
    public static String getCurrentDateFormatted() {
        return LocalDate.now().format(FORMATTER);
    }
}
