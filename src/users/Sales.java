package users;

import enums.*;

public class Sales extends User {
    public Sales(String name, String username, String password) {
        super(name, username, password);
    }

    public Sales(int userId, String name, String username, String password) {
        super(userId, name, username, password);
    }

    @Override
    public UserType getUserType() {
        return UserType.SALES;
    }
}
