package app;

/**
 * Punto di ingresso principale dell'applicazione Rome Transit Tracker.
 * Avvia il processo di inizializzazione delegandolo alla classe
 * {@link AppLauncher}.
 */
public class Main {

    /**
     * Avvia l'applicazione passando i parametri della riga di comando
     * al lanciatore dell'interfaccia grafica.
     *
     * @param args argomenti della riga di comando forniti all'avvio
     */
    public static void main(String[] args) {
        AppLauncher.launchApp(args);
    }
}
