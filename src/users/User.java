package users;
import enums.*;

public abstract class User {
    private static int idCounter = 0;
    protected int userId;
    protected String name;
    protected String username;
    protected String password;

    public User(int userId, String name, String username, String password) {
        if(userId <= 0) throw new IllegalArgumentException("User ID must be greater than 0");
        if(name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        if(username == null || username.trim().isEmpty()) throw new IllegalArgumentException("Username cannot be empty");
        if(password == null || password.trim().isEmpty()) throw new IllegalArgumentException("Password cannot be empty");

        this.userId = userId;
        this.name = name;
        this.username = username;
        this.password = password;
    }

    public User(String name, String username, String password) {
        this(++idCounter, name, username, password);
    }

    // -- Methods
    public boolean verifyPassword(String inputPassword) {
        if(inputPassword == null || inputPassword.trim().isEmpty()) throw new IllegalArgumentException("Password cannot be empty");
        return this.password.equals(inputPassword);
    }

    public void userInformation() {
        System.out.println("User ID     :: " + userId);
        System.out.println("Name        :: " + name);
        System.out.println("Username    :: " + username);
        System.out.println("User Type   :: " + this.getUserType());
    }

    public abstract String getUserType();

    // -- Getters
    public int getUserId() { return userId; }
    public String getName() { return name; }
    public String getUsername() { return username; }

    // -- Setters
    public void setName(String name) {
        if(name == null || name.trim().isEmpty()) throw new IllegalArgumentException("Name cannot be empty");
        this.name = name;
    }

    public void setUsername(String username) {
        if(username == null || username.trim().isEmpty()) throw new IllegalArgumentException("Username cannot be empty");
        this.username = username;
    }

    public void setPassword(String password) {
        if(password == null || password.trim().isEmpty()) throw new IllegalArgumentException("Password cannot be empty");
        this.password = password;
    }
}
