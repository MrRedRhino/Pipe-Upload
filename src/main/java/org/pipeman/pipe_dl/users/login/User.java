package org.pipeman.pipe_dl.users.login;

import org.pipeman.pipe_dl.util.PasswordSpicer;

import java.beans.ConstructorProperties;
import java.sql.ResultSet;

public class User {
    long id;
    String name;
    String password;
    String email;

    @ConstructorProperties({"id", "name", "password", "email"})
    public User(long id, String name, String password, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public long id() {
        return id;
    }

    public User(ResultSet resultSet) {
        try {
            if (resultSet.next()) {
                int i = 1;
                this.id = resultSet.getLong(i++);
                this.name = resultSet.getString(i++);
                this.password = resultSet.getString(i++);
                this.email = resultSet.getString(i);
            }
        } catch (Exception ignored) {}
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
