package auth;

/**
 * Basic implementation of {@link AuthenticationService} that stores users in a
 * simple file via {@link FileUserRepository}.
 */
public class AuthenticationManager implements AuthenticationService {
    private final FileUserRepository repo;

    public AuthenticationManager(FileUserRepository repo) {
        this.repo = repo;
    }

    @Override
    public void register(String username, char[] password) {
        if (repo.usernameExists(username)) {
            throw new IllegalStateException("Username già esistente");
        }
        String hash = MD5Util.md5(new String(password));
        repo.createUser(username, hash);
    }

    @Override
    public boolean login(String username, char[] password) {
        var user = repo.findByUsername(username);
        if (user == null) return false;
        String hash = MD5Util.md5(new String(password));
        return user.md5Hash().equals(hash);
    }
}
