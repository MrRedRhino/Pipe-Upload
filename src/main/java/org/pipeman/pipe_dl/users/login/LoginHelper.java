package org.pipeman.pipe_dl.users.login;

import org.pipeman.pipe_dl.users.User;
import spark.Request;

import java.util.*;

public class LoginHelper {
    private static final Map<String, User> sessionIDs = new HashMap<>();

    public static User getAccountByRequest(Request request) {
        String cookie = request.cookie("login");

        return getAccountBySessionId(cookie == null ? request.headers("auth") : cookie);
    }

    public static User getAccountBySessionId(String id) {
        if (id == null) return null;
        return sessionIDs.get(id);
    }

    public static String tryToLogin(String email, String password) {
        Optional<User> user = User.getByEmail(email);

        if (user.isPresent() && user.get().authCorrect(email, password)) {
            return genSessionId(user.get());
        }
        return null;
    }

    public static String genSessionId(User user) {
        String sid = UUID.randomUUID().toString();
        sessionIDs.put(sid, user);
        return sid;
    }

    public static void logout(String sessionID) {
        sessionIDs.remove(sessionID);
    }
}
