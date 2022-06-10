package org.pipeman.pipe_dl.login;

import org.pipeman.pipe_dl.DB;
import spark.Request;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;


public class AccountHelper {
    private static final List<SessionID> sessionIDs = new ArrayList<>();

    public static Account getAccountByEmail(String email) {
        try {
            try (Connection connection = DB.ds().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM users WHERE email = (?)")) {
                statement.setString(1, email);

                try (ResultSet resultSet = statement.executeQuery()) {
                    return Account.fromResultSet(resultSet);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Account getAccountByRequest(Request request) {
        String cookie = request.cookie("login");
        String header = request.headers("auth");

        if (cookie == null) {
            return getAccountBySessionId(header);
        } else {
            return getAccountBySessionId(cookie);
        }
    }

    private static Account getAccountBySessionId(String id) {
        if (id == null) return null;
        for (SessionID sessionID : sessionIDs) {
            if (id.equals(sessionID.id())) {
                return sessionID.account();
            }
        }
        return null;
    }

    public static boolean checkAuth(Request request) {
        String cookie = request.cookie("login");
        if (cookie == null) return false;
        for (SessionID sessionID : sessionIDs) {
            if (sessionID.id().equals(cookie)) {
                return true;
            }
        }
        return false;
    }

    public static String tryToLogin(String email, String password) {
        Account account = getAccountByEmail(email);

        if (account != null && account.authCorrect(email, password)) {
            String sid = UUID.randomUUID().toString();
            AccountHelper.sessionIDs.add(new SessionID(sid, account));
            return sid;
        }
        return null;
    }

    public static boolean logout(String sessionID) {
        return sessionIDs.removeIf(id -> id.id().equals(sessionID));
    }

    record SessionID(String id, Account account) { }

}
