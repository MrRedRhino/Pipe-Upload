package org.pipeman.pipe_dl.util.sql;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;

public class Where extends Statement {
    private final ArrayList<String> comparisons = new ArrayList<>();
    private final Statement parent;

    public Where(Statement parent, String column, String operator, String column2) {
        comparisons.add(" " + column + " " + operator + " " + column2);
        this.parent = parent;
    }

    public Where and(String column, String operator, String column2) {
        comparisons.add(" " + column + " " + operator + " " + column2);
        return this;
    }

    @Override
    protected int applyTo(PreparedStatement statement, int i) throws SQLException {
        parent.applyTo(statement, i);
        for (String s : comparisons) {
            statement.setString(i, s);
            i++;
        }
        return i;
    }

    @Override
    public Where where(String column, String operator, String column2) {
        return null;
    }
    // Sql.select("*").from("users").where(
}
