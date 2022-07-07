package org.pipeman.pipe_dl.download_page;

import static org.pipeman.pipe_dl.DB.jdbi;

public class UploadPageHelper {
    public static UploadPage get(long id) {
        return jdbi().withHandle(handle -> handle.createQuery("SELECT * FROM upload_pages WHERE id = (?)")
                .bind(0, id)
                .mapTo(UploadPage.class)
                .list().get(0));
    }

    public static void update(UploadPage uploadPage) {
        jdbi().useHandle(handle -> handle.createUpdate("UPDATE upload_pages " +
                "SET name = (?), owner_id = (?), root_dir_id = (?), total_bytes = (?), used_bytes = (?) WHERE id = (?)")
                .bind(0, uploadPage.name())
                .bind(1, uploadPage.ownerId())
                .bind(2, uploadPage.rootDirId())
                .bind(3, uploadPage.totalBytes())
                .bind(4, uploadPage.usedBytesChain())
                .bind(5, uploadPage.id())
                .execute()
        );
    }
}
