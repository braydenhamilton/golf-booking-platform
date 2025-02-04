package com.golf.teetimecoreapi.session;

import com.golf.model.User;

import java.util.HashMap;
import java.util.Map;

public class UserSessionStore {
    private static final Map<String, User> userSessions = new HashMap<>();

    public static void addUserSession(User user) {
        userSessions.put(user.getGolfNZMemberId(), user);
    }

    public static User getUserSession() {
        // For simplicity, return the first user in the session store
        // In a real application, you should use a more secure method to manage sessions
        return userSessions.values().stream().findFirst().orElse(null);
    }
}