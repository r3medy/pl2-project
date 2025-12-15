package managers;

import java.util.ArrayList;
import java.util.List;
import users.*;

public class UsersManager {
    private List<User> users;

    public UsersManager() {
        this.users = FileManager.loadUsers();
    }

    public boolean addUser(User user) {
        if (user == null) return false;
        this.users.add(user);
        return FileManager.saveUsers(this.users);
    }

    public boolean removeUser(User user) {
        if (user == null) return false;
        boolean removed = this.users.removeIf(u -> u.getUserId() == user.getUserId());
        if (removed) return FileManager.saveUsers(this.users);
        return false;
    }

    public User findUserById(int userId) {
        for (User u : this.users) {
            if (u.getUserId() == userId) return u;
        }
        return null;
    }

    public User findUserByUsername(String username) {
        if (username == null) return null;
        for (User u : this.users) {
            if (u.getUsername().equalsIgnoreCase(username)) return u;
        }
        return null;
    }

    public List<User> getUsers() {
        return new ArrayList<>(this.users);
    }
}
