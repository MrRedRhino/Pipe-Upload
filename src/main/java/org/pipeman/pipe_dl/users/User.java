package org.pipeman.pipe_dl.users;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.util.security.PasswordSpicer;

import java.beans.ConstructorProperties;
import java.util.Optional;

import static org.pipeman.pipe_dl.DB.jdbi;

public class User {
    private final long id;
    private String name;
    private String password;
    private final String email;

    @ConstructorProperties({"id", "name", "password", "email"})
    public User(long id, String name, String password, String email) {
        this.id = id;
        this.name = name;
        this.password = password;
        this.email = email;
    }

    public User(String name, String password, String email) {
        this(Main.uid.newUID(), name, password, email);
    }

    public static Optional<User> getByEmail(String email) {
        if (email == null) return Optional.empty();
        return jdbi().withHandle(handle -> handle.createQuery("SELECT * FROM users WHERE email = (?)")
                .bind(0, email)
                .mapTo(User.class).findFirst());
    }

    public static Optional<User> getById(long id) {
        return jdbi().withHandle(handle -> handle.createQuery("SELECT * FROM users WHERE id = (?)")
                .bind(0, id)
                .mapTo(User.class).findFirst());
    }

    public long id() {
        return id;
    }

    public String name() {
        return name;
    }

    public User name(String newName) {
        name = newName;
        return this;
    }

    public User password(String newPassword) {
        password = newPassword;
        return this;
    }

    public boolean authCorrect(String email, String password) {
        if (this.email.equals(email.toLowerCase())) {
            return PasswordSpicer.hashAndSpice(password, this).equals(this.password);
        }
        return false;
    }

    public void save() {
        jdbi().useHandle(handle -> handle.createUpdate(
                "INSERT INTO users (id, name, password, email) VALUES ((?), (?), (?), (?))" +
                        " ON CONFLICT (id) DO UPDATE SET name = (?), password = (?), email = (?)")
                .bind(0, id)
                .bind(1, name)
                .bind(2, password)
                .bind(3, email)
                .bind(4, name)
                .bind(5, password)
                .bind(6, email)
                .execute());
    }
}
