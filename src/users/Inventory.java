package users;

import enums.*;

public class Inventory extends User {
    public Inventory(String name, String username, String password) {
        super(name, username, password);
    }
    
    public Inventory(int userId, String name, String username, String password) {
        super(userId, name, username, password);
    }

    @Override
    public UserType getUserType() {
        return UserType.INVENTORY;
    }
}
