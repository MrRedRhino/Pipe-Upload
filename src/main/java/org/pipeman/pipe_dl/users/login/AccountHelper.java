package org.pipeman.pipe_dl.users.login;

import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import spark.Request;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.pipeman.pipe_dl.DB.jdbi;


public class AccountHelper {
    private static final List<SessionID> sessionIDs = new ArrayList<>();

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
        for (SessionID sessionID : sessionIDs) {
            if (id.equals(sessionID.id())) {
                return sessionID.user();
            }
        }
        return null;
    }

    public static String tryToLogin(String email, String password) {
        User user = getAccountByEmail(email);

        if (user != null && user.authCorrect(email, password)) {
            String sid = UUID.randomUUID().toString();
            AccountHelper.sessionIDs.add(new SessionID(sid, user));
            return sid;
        }
        return null;
    }

    public static void logout(String sessionID) {
        sessionIDs.removeIf(id -> id.id().equals(sessionID));
    }

    record SessionID(String id, User user) {
    }

}
