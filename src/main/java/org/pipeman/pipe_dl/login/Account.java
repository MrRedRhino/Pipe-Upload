package org.pipeman.pipe_dl.login;

import org.pipeman.pipe_dl.util.PasswordSpicer;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Account {
    long id;
    String name;
    String password;
    String email;

    public Account(long id, String name, String password, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public long id() {
        return id;
    }

    public static Account fromResultSet(ResultSet resultSet) throws SQLException {
        if (resultSet.next()) {
            int i = 1;
            long id = resultSet.getLong(i++);
            String name = resultSet.getString(i++);
            String password = resultSet.getString(i++);
            String email = resultSet.getString(i);
            return new Account(id, name, password, email);
        }
        return null;
    }

    public boolean authCorrect(String email, String password) {

        if (this.email.equals(email.toLowerCase())) {
            PasswordSpicer.applyPepper(password);
            PasswordSpicer.applySalt(password);
            PasswordSpicer.hash(password);

            return password.equals(this.password);
        }
        return false;
    }
}
