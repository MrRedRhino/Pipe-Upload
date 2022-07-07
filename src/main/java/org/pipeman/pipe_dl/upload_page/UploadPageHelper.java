package org.pipeman.pipe_dl.upload_page;

import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.pipeman.pipe_dl.DB;

public class UploadPageHelper {
    public static UploadPage getFromId(long id) {
        return DB.jdbi().withHandle(handle -> {
            handle.registerRowMapper(ConstructorMapper.factory(UploadPage.class));
            return handle.createQuery("SELECT * FROM upload_pages WHERE id = (?)")
                    .bind(0, id)
                    .mapTo(UploadPage.class)
                    .list().get(0);
        });
    }
}
