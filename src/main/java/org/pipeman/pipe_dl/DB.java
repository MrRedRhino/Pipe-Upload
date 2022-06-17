package org.pipeman.pipe_dl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

public class DB {
    private static Jdbi jdbi;
    private static HikariDataSource ds;

    public static void connect() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Main.config().dbUrl);
        config.setUsername(Main.config().dbUsername);
        config.setPassword(Main.config().dbPassword);
        ds = new HikariDataSource(config);
        jdbi = Jdbi.create(ds);
    }

    public static Jdbi jdbi() {
        return jdbi;
    }

    public static HikariDataSource ds() {
        return ds;
    }
}
