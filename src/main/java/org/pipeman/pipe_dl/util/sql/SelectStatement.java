package org.pipeman.pipe_dl.util.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SelectStatement extends Statement {
    private final String columns;
    private String table;

    public SelectStatement(String what) {
        columns = what;
    }

    public SelectStatement from(String table) {
        this.table = table;
        return this;
    }

    @Override
    protected int applyTo(PreparedStatement statement, int startIndex) throws SQLException {
        statement.setString(startIndex, columns);
        statement.setString(startIndex + 1, table);
        return startIndex + 2;
    }

    @Override
    public Where where(String column, String operator, String column2) {
        return new Where(this, column, operator, column2);
    }
}
