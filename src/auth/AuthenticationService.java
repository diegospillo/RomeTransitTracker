package auth;

/**
 * Servizio che definisce le operazioni di autenticazione degli utenti.
 */
public interface AuthenticationService {

    /**
     * Registra un nuovo utente persistendo le sue credenziali.
     *
     * @param username nome utente da creare
     * @param password password in chiaro da convertire e salvare
     */
    void register(String username, char[] password);

    /**
     * Verifica le credenziali di accesso di un utente esistente.
     *
     * @param username nome utente
     * @param password password fornita
     * @return {@code true} se l'autenticazione ha successo, altrimenti {@code false}
     */
    boolean login(String username, char[] password);
}
