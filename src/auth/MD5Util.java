package auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Utilità per calcolare l'hash MD5 di stringhe.
 */
public class MD5Util {

    /**
     * Calcola l'hash MD5 della stringa fornita.
     *
     * @param input testo di cui generare l'hash
     * @return rappresentazione esadecimale dell'hash
     * @throws RuntimeException se l'algoritmo MD5 non è disponibile
     */
    public static String md5(String input) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] digest = md.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) {
                sb.append(String.format("%02x", b)); // formato esadecimale
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 non supportato", e);
        }
    }
}
