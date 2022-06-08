package org.pipeman.pipe_dl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.sql.Connection;
import java.sql.SQLException;

public class DB {
    private static HikariDataSource ds;

    public static void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Config.Database.URL);
        config.setUsername(Config.Database.USERNAME);
        config.setPassword(Config.Database.PASSWORD);
        ds = new HikariDataSource(config);
    }

    public static HikariDataSource ds() {
        return ds;
    }
}
