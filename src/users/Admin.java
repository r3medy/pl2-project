package users;

import enums.*;

public class Admin extends User {
    public Admin(String name, String username, String password) {
        super(name, username, password);
    }

    public Admin(int userId, String name, String username, String password) {
        super(userId, name, username, password);
    }

    @Override
    public UserType getUserType() {
        return UserType.ADMIN;
    }
}
