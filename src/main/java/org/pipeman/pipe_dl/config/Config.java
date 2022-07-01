package org.pipeman.pipe_dl.config;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

public class Config extends ConfigHelper {
    public final int serverPort = this.get("server-port", 80);
    public final String baseApiRoute = this.get("api-route", "/api/");
    public final String baseRoute = this.get("base-route", "/");


    public final String dbUrl = this.get("db-url");
    public final String dbUsername = this.get("db-user");
    public final String dbPassword = this.get("db-password");


    public final Path login = this.get("html-login", Paths.get("deploy/login.html"));
    public final Path loggedIn = this.get("html-logged-in", Paths.get("deploy/logged-in.html"));
    public final Path logout = this.get("html-logout", Paths.get("deploy/logout.html"));
    public final Path index = this.get("html-login", Paths.get("deploy/login.html"));
    public final Path upload = this.get("html-upload", Paths.get("deploy/upload.html"));
    public final Path register = this.get("html-register", Paths.get("deploy/register.html"));
    public final Path downloadPage = this.get("html-download-page", Paths.get("deploy/download-page.html"));


    public final String imagesDir = this.get("images-directory", "images");


    public final String uploadDir = this.get("usercontent-directory", "usercontent/");


    public final int uidGeneratorId = this.get("uid-generator-id", 1);
    public final int uidGeneratorTimestamp = this.get("uid-generator-timestamp-bits", 46);
    public final int uidGeneratorGen = this.get("uid-generator-generator-bits", 6);
    public final int uidGeneratorSequence = this.get("uid-generator-sequence-bits", 11);
    public final Path fileView = this.get("html-file-view", Paths.get("deploy/file-view.html"));


    public static Config fromFile(Path path) {
        return new Config(loadFromFile(path));
    }

    public Config(Properties properties) {
        super(properties);
    }
}
