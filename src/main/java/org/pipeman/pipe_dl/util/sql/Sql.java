package org.pipeman.pipe_dl.util.sql;

import org.pipeman.pipe_dl.DB;

import java.sql.SQLException;
import java.util.List;

public class Sql {
    public static SelectStatement select(String what) {
        return new SelectStatement(what);
    }

    public static void main(String[] args) throws SQLException {
        List<?> out = Sql.select("*").from("users")
                .where("name", "=", args[0])
                .and("password", "=", args[1])
                .execute(DB.connection());
    }
}
