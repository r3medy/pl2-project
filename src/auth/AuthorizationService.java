package auth;

import users.*;
import enums.*;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public class AuthorizationService {
    private final Map<UserType, EnumSet<Actions>> permissions;

    public AuthorizationService() {
        permissions = new HashMap<>();
        permissions.put(UserType.ADMIN, EnumSet.allOf(Actions.class));
        permissions.put(UserType.INVENTORY, EnumSet.of(Actions.VIEW_INVENTORY, Actions.UPDATE_INVENTORY));
        permissions.put(UserType.MARKETING, EnumSet.of(Actions.MARKETING_CAMPAIGN));
        permissions.put(UserType.SALES, EnumSet.of(Actions.SALES_REPORT));
    }

    public boolean canPerformAction(User user, Actions action) {
        if (user == null) throw new IllegalArgumentException("User cannot be null.");
        EnumSet<Actions> allowedActions = permissions.get(user.getUserType());
        return allowedActions != null && allowedActions.contains(action);
    }
}
