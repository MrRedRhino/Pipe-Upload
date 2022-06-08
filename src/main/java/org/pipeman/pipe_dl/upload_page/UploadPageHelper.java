package org.pipeman.pipe_dl.upload_page;

import org.pipeman.pipe_dl.util.DBHelper;

import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UploadPageHelper {
    public static UploadPage fetch(long id) {
        return new DBHelper<UploadPage>().execInConn(connection -> {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM upload_pages WHERE id = (?)");
            statement.setLong(1, id);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new UploadPage(resultSet.getString(1), resultSet.getLong(2));
                }
            }
            return null;
        }, Throwable::printStackTrace);
    }

}
