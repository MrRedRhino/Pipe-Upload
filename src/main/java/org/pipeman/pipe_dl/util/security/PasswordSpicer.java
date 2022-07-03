package org.pipeman.pipe_dl.util.security;

import org.pipeman.pipe_dl.users.login.User;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;

public class PasswordSpicer {
    private static final String pepper = getPepper();

    public static String applySalt(String password, User user) {
        String salt = SaltGen.gen(user); // this class is not on GitHub
        return salt + password;
    }

    public static String applyPepper(String password) {
        return password + getPepper();
    }

    public static String hash(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            digest.reset();

            StringBuilder result = new StringBuilder();
            for (byte b : digest.digest(password.getBytes())) {
                result.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
            }

            return result.toString();
        } catch (Exception ignored) {
        }
        return "";
    }

    public static String hashAndSpice(String password, User user) {
        password = applySalt(password, user);
        password = applyPepper(password);
        return hash(password);
    }

    private static String getPepper() {
        if (pepper == null) {
            Path pepperPath = Path.of("oh-so-secret.txt"); // I just realised, sharing this filename does not make a lot of sense

            try {
                if (!pepperPath.toFile().exists()) {
                    Files.write(pepperPath, "verySecretPepper".getBytes());
                }

                return Files.readString(pepperPath);
            } catch (Exception e) {
                throw new RuntimeException("Could not read pepper: ", e);
            }
        }
        return pepper;
    }
}
