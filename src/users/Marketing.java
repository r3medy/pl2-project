package users;

import enums.*;

public class Marketing extends User {
    public Marketing(String name, String username, String password) {
        super(name, username, password);
    }

    public Marketing(int userId, String name, String username, String password) {
        super(userId, name, username, password);
    }

    @Override
    public String getUserType() {
        return UserType.MARKETING.name();
    }
}
