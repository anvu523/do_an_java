package com.drinkstore.factory;

import com.drinkstore.model.Role;
import com.drinkstore.model.User;
import com.drinkstore.util.PasswordUtil;

public final class UserFactory {
    private UserFactory() {
    }

    public static User createUser(String username, String rawPassword, Role role, boolean active) {
        User user = new User();
        user.setUsername(username);
        user.setPasswordHash(PasswordUtil.sha256(rawPassword));
        user.setRole(role);
        user.setActive(active);
        return user;
    }
}
