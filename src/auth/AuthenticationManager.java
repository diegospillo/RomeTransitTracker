package auth;

public class AuthenticationManager {
    private final FileUserRepository repo;

    public AuthenticationManager(FileUserRepository repo) {
        this.repo = repo;
    }

    public void register(String username, char[] password) {
        if (repo.usernameExists(username)) {
            throw new IllegalStateException("Username già esistente");
        }
        String hash = MD5Util.md5(new String(password));
        repo.createUser(username, hash);
    }

    public boolean login(String username, char[] password) {
        var user = repo.findByUsername(username);
        if (user == null) return false;
        String hash = MD5Util.md5(new String(password));
        return user.md5Hash().equals(hash);
    }
}
