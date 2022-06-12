package org.pipeman.pipe_dl.files;

import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;

import java.util.List;

import static org.pipeman.pipe_dl.DB.jdbi;

public class FileHelper {
    public static PipeFile getFile(long id) {
        return jdbi().withHandle(handle -> {
            handle.registerRowMapper(ConstructorMapper.factory(PipeFile.class));
            return handle.createQuery("SELECT * FROM files WHERE id = (?) LIMIT 1")
                    .bind(0, id)
                    .mapTo(PipeFile.class).list().get(0);
        });
    }


    public static List<PipeFile> listDir(long directoryId) {
        return jdbi().withHandle(handle -> {
            handle.registerRowMapper(ConstructorMapper.factory(PipeFile.class));
            return handle.createQuery("SELECT * FROM files WHERE directory_id = (?)")
                    .bind(0, directoryId)
                    .mapTo(PipeFile.class)
                    .list();
        });
    }


    public static void uploadFile(PipeFile file) {
        jdbi().useHandle(handle -> handle.createUpdate("INSERT INTO files (id, name, page_id, directory_id, " +
                        "creator_id, is_folder) VALUES ((?), (?), (?), (?), (?), (?))")
                .bind(0, file.id())
                .bind(1, file.name())
                .bind(2, file.pageId())
                .bind(3, file.directoryId())
                .bind(4, file.creatorId())
                .bind(5, file.isFolder())
                .execute());
    }
}
