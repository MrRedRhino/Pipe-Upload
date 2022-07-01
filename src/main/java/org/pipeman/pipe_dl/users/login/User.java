package org.pipeman.pipe_dl.users.login;

import org.pipeman.pipe_dl.util.PasswordSpicer;

import java.beans.ConstructorProperties;

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
