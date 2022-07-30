package org.pipeman.pipe_dl;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.jdbi.v3.core.Jdbi;

public class DB {
    public static final DB INSTANCE = new DB();
    private final Jdbi jdbi;
    private final HikariDataSource ds;

    public DB() {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(Main.config().dbUrl);
        config.setUsername(Main.config().dbUsername);
        config.setPassword(Main.config().dbPassword);
        ds = new HikariDataSource(config);
        jdbi = Jdbi.create(ds);
    }

    public static Jdbi jdbi() {
        return INSTANCE.jdbi;
    }

    public void disconnect() {
        if (ds != null) ds.close();
    }
}
