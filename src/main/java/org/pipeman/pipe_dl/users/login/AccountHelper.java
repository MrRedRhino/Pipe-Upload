package org.pipeman.pipe_dl.users.login;

import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import spark.Request;

import java.util.*;

import static org.pipeman.pipe_dl.DB.jdbi;


public class AccountHelper {
    private static final Map<String, User> sessionIDs = new HashMap<>();

    public static User getAccountByEmail(String email) {
        if (email == null) return null;
        return jdbi().withHandle(handle -> {
            handle.registerRowMapper(ConstructorMapper.factory(User.class));
            return handle.createQuery("SELECT * FROM users WHERE email = (?)")
                    .bind(0, email)
                    .mapTo(User.class).list().get(0);
        });
    }

    public static User getAccountByRequest(Request request) {
        String cookie = request.cookie("login");
        String header = request.headers("auth");

        return getAccountBySessionId(cookie == null ? header : cookie);
    }

    private static User getAccountBySessionId(String id) {
        if (id == null) return null;
        return sessionIDs.get(id);
    }

    public static String tryToLogin(String email, String password) {
        User user = getAccountByEmail(email);

        if (user != null && user.authCorrect(email, password)) {
            String sid = UUID.randomUUID().toString();
            sessionIDs.put(sid, user);
            return sid;
        }
        return null;
    }

    public static void logout(String sessionID) {
        sessionIDs.remove(sessionID);
    }
}
