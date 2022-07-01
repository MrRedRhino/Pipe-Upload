package org.pipeman.pipe_dl.util.sql;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

public abstract class Statement {
    protected String query = "";

    private void appendQuery(String append) {
        query += append;
    }

    protected abstract int applyTo(PreparedStatement statement, int startIndex) throws SQLException;

    public abstract Where where(String column, String operator, String column2);

    public List<?> execute(Connection c) throws SQLException {
        PreparedStatement out = c.prepareStatement(query);
        applyTo(out, 1);

        return null;
    }
}
