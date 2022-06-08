package org.pipeman.pipe_dl.util;

import com.zaxxer.hikari.HikariDataSource;
import org.pipeman.pipe_dl.DB;

import java.sql.Connection;
import java.util.function.Consumer;

public class DBHelper<T> {
    public T execInConn(ConnectionConsumer<T> consumer) throws Exception {
        try (HikariDataSource ds = DB.ds(); Connection connection = ds.getConnection()) {
            return consumer.accept(connection);
        }
    }

    public T execInConn(ConnectionConsumer<T> consumer, Consumer<Exception> onException) {
        try {
            return execInConn(consumer);
        } catch (Exception e) {
            onException.accept(e);
        }
        return null;
    }

    @FunctionalInterface
    public interface ConnectionConsumer<T> {
        T accept(Connection connection) throws Exception;
    }
}
