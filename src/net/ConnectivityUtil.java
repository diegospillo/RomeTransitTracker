package net;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

/** Utility to check if network is reachable to decide between online and offline mode. */
public class ConnectivityUtil {
    private static final String TEST_URL = "https://romamobilita.it";

    public static boolean isOnline() {
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
}
