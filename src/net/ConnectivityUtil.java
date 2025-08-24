package net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import view.MainView;

/**
 * Verifica la connettività di rete per passare tra modalità online e offline.
 */
public class ConnectivityUtil {
        private static boolean offlineMode = false;
        private static MainView mainview;

    private static final String TEST_URL = "https://romamobilita.it";

    /**
     * Controlla la raggiungibilità della rete e aggiorna l'interfaccia in
     * modalità offline se necessario.
     */
    public static void checkConnectivityAndSwitchMode() {
        boolean online = ConnectivityUtil.isOnline();
        if (!online) {
            if (!offlineMode) {
                offlineMode = true;
                javax.swing.SwingUtilities.invokeLater(() -> {
                    javax.swing.JOptionPane.showMessageDialog(null,
                            "Connessione non disponibile. Passaggio alla modalità offline.",
                            "Modalità Offline",
                            javax.swing.JOptionPane.WARNING_MESSAGE);
                });
                mainview.get_offlinePanel().setVisible(true);
            }
        } else {
            offlineMode = false;
            mainview.get_offlinePanel().setVisible(false);
        }
    }

    /**
     * Imposta l'istanza della view principale su cui riflettere la modalità.
     *
     * @param instanceMainview vista principale dell'applicazione
     */
    public static void setInstanceMainView(MainView instanceMainview) {
        mainview = instanceMainview;
    }

    /**
     * Effettua una richiesta HTTP per verificare la raggiungibilità del sito.
     *
     * @return {@code true} se la connessione è disponibile
     */
    private static boolean isOnline() {
        try {
            HttpURLConnection conn = (HttpURLConnection) new URL(TEST_URL).openConnection();
            conn.setConnectTimeout(2000);
            conn.setReadTimeout(2000);
            conn.setRequestMethod("HEAD");
            int code = conn.getResponseCode();
            return code >= 200 && code < 400;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * @return {@code true} se l'applicazione è in modalità offline
     */
    public static boolean isOfflineMode() {
            return offlineMode;
        }

        /**
         * Imposta manualmente la modalità offline.
         *
         * @param stato {@code true} per abilitare l'offline
         */
        public static void setOfflineMode(boolean stato) {
            offlineMode = stato;
        }
}
