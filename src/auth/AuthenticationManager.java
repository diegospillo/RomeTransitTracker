package auth;

/**
 * Gestisce l'autenticazione degli utenti dell'applicazione memorizzando
 * le credenziali tramite un {@link FileUserRepository}.
 */
public class AuthenticationManager implements AuthenticationService {
    private final FileUserRepository repo;

    /**
     * Crea un gestore che utilizza il repository specificato.
     *
     * @param repo implementazione per la persistenza delle credenziali
     */
    public AuthenticationManager(FileUserRepository repo) {
        this.repo = repo;
    }

    /** {@inheritDoc} */
    @Override
    public void register(String username, char[] password) {
        if (repo.usernameExists(username)) {
            throw new IllegalStateException("Username già esistente");
        }
        String hash = MD5Util.md5(new String(password));
        repo.createUser(username, hash);
    }

    /** {@inheritDoc} */
    @Override
    public boolean login(String username, char[] password) {
        var user = repo.findByUsername(username);
        if (user == null) return false;
        String hash = MD5Util.md5(new String(password));
        return user.md5Hash().equals(hash);
    }
}
