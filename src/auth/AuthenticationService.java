package auth;

public interface AuthenticationService {
    void register(String username, char[] password);
    boolean login(String username, char[] password);
}
