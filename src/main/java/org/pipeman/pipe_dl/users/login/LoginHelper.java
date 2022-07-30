package org.pipeman.pipe_dl.users.login;

import org.pipeman.pipe_dl.users.User;
import spark.Request;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class LoginHelper {
    private static final Map<String, User> sessionIDs = new HashMap<>();

    static {
        User.getByEmail("email").ifPresent(user -> sessionIDs.put("1", user));
    }

    public static User getAccountByRequest(Request request) {
        String cookie = request.cookie("login");

        return getAccountBySessionId(cookie == null ? request.headers("auth") : cookie);
    }

    public static User getAccountBySessionId(String id) {
        if (id == null) return null;
        return sessionIDs.get(id);
    }

    public static String forceLogin(User user) {
        String sid = UUID.randomUUID().toString();
        sessionIDs.put(sid, user);
        return sid;
    }

    public static void logout(String sessionID) {
        sessionIDs.remove(sessionID);
    }
}
