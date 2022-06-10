package org.pipeman.pipe_dl.files;

import org.pipeman.pipe_dl.DB;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FileHelper {
    public static PipeFile getFile(long id) {
        try {
            try (Connection connection = DB.ds().getConnection();
                 PreparedStatement statement = connection.prepareStatement("SELECT * FROM files WHERE id = (?)")) {

                statement.setLong(1, id);

                try (ResultSet resultSet = statement.executeQuery()) {
                    if (resultSet.next()) {
                        return new PipeFile(
                                resultSet.getLong(1),
                                resultSet.getString(2),
                                resultSet.getLong(3),
                                resultSet.getLong(4),
                                resultSet.getLong(5),
                                resultSet.getBoolean(6)
                        );
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }


    public static List<PipeFile> listDir(long directoryId) {
        List<PipeFile> out = new ArrayList<>();
        try {
            try (Connection connection = DB.ds().getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "SELECT * FROM files WHERE directory_id = (?)")) {

                statement.setLong(1, directoryId);

                try (ResultSet resultSet = statement.executeQuery()) {
                    while (resultSet.next()) {
                        out.add(new PipeFile(
                                resultSet.getLong(1),
                                resultSet.getString(2),
                                resultSet.getLong(3),
                                resultSet.getLong(4),
                                resultSet.getLong(5),
                                resultSet.getBoolean(6)
                        ));
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return out;
    }


    public static void uploadFile(PipeFile file) {
        try {
            try (Connection connection = DB.ds().getConnection();
                 PreparedStatement statement = connection.prepareStatement(
                         "INSERT INTO files (id, name, page_id, directory_id, creator_id, is_folder) " +
                                 "VALUES ((?), (?), (?), (?), (?), (?))"
                 )) {

                statement.setLong(1, file.id());
                statement.setString(2, file.name());
                statement.setLong(3, file.pageId());
                statement.setLong(4, file.directoryId());
                statement.setLong(5, file.creatorId());
                statement.setBoolean(6, file.isFolder());

                statement.execute();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
