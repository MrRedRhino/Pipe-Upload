package org.pipeman.pipe_dl.download_page;

import org.pipeman.pipe_dl.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UploadPageHelper {
    public static UploadPage fetch(long id) {
        try {
            try (Connection connection = DB.ds().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM upload_pages WHERE id = (?)"
                 )) {

                statement.setLong(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    return new UploadPage(resultSet.getString(1), resultSet.getLong(2));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
