package net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import view.MainView;

/** Utility to check if network is reachable to decide between online and offline mode. */
public class ConnectivityUtil {
	private static boolean offlineMode = false;
	private static MainView mainview;

    private static final String TEST_URL = "https://romamobilita.it";
    
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
    
    public static void setInstanceMainView(MainView instanceMainview) {
    	mainview = instanceMainview;
    }

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
    
    public static boolean isOfflineMode() {
	    return offlineMode;
	}

	public static void setOfflineMode(boolean stato) {
	    offlineMode = stato;
	}
}
