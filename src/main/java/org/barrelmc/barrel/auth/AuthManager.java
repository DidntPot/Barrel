package org.barrelmc.barrel.auth;



import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class AuthManager {
    public static AuthManager instance;
    public final Map<String, String> accessTokens = new HashMap<>();
    public final Map<String, String> band = new HashMap<>();
    public final XboxLogin xboxLogin;
    public final Map<String, Boolean> loginPlayers = new ConcurrentHashMap<>();

    public AuthManager() {
        xboxLogin = new XboxLogin();
    }

    public static AuthManager getInstance() {
        if (instance == null) {
            instance = new AuthManager();
        }
        return instance;
    }
}
