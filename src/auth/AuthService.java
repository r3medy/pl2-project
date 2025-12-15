package auth;
import users.*;
import java.util.List;

public class AuthService {
    private List<User> users;

    public AuthService(List<User> users) {
        this.users = users;
    }

    public User login(String username, String password) {
        if (username == null || password == null || username.isEmpty() || password.isEmpty())
            throw new IllegalArgumentException("Login failed: username or password is empty.");

        username = username.trim();
        password = password.trim();
        for (User usernow : users) {
            if (usernow.getUsername().equals(username) && usernow.verifyPassword(password)) {
                // TODO: redirect to user's dashboard
                System.out.println("Login successful for user: " + username);
                return usernow;
            }
        }
        throw new IllegalArgumentException("Login failed: incorrect credentials for user: " + username);
    }
}


