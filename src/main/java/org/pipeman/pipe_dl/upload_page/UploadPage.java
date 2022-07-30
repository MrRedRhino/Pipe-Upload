package org.pipeman.pipe_dl.upload_page;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.pipe_file.PipeFile;

import java.beans.ConstructorProperties;
import java.util.Optional;

import static org.pipeman.pipe_dl.DB.jdbi;

public class UploadPage {
    private final String name;
    private final long id;
    private final long ownerId;
    private final long rootDirId;
    private final long totalBytes;
    private final long usedBytes;

    @ConstructorProperties({"name", "id", "owner_id", "root_dir_id", "total_bytes", "used_bytes"})
    public UploadPage(String name, long id, long ownerId, long rootDirId, long totalBytes, long usedBytes) {
        this.name = name;
        this.id = id;
        this.ownerId = ownerId;
        this.rootDirId = rootDirId;
        this.totalBytes = totalBytes;
        this.usedBytes = usedBytes;
    }

    public static Optional<UploadPage> get(long id) {
        return jdbi().withHandle(handle -> handle.createQuery("SELECT * FROM upload_pages WHERE id = (?)")
                .bind(0, id)
                .mapTo(UploadPage.class)
                .findFirst());
    }

    public static UploadPage createUploadPage(String name, long ownerId, long totalBytes) {
        long id = Main.uid.newUID();
        try {
            new PipeFile(id, "Root", id, ownerId, true, 0, id + "").save();
        } catch (PipeFile.FileTooBigException ignored) {
            return null;
        }
        UploadPage newPage = new UploadPage(name, id, ownerId, id, totalBytes, 0);
        newPage.save();
        return newPage;
    }

    public long totalBytes() {
        return totalBytes;
    }

    public long usedBytes() {
        return usedBytes;
    }

    public String name() {
        return name;
    }

    public long id() {
        return id;
    }

    public long rootDirId() {
        return rootDirId;
    }

    public long ownerId() {
        return ownerId;
    }

    public void save() {
        jdbi().useHandle(handle -> handle.createUpdate(
                        """
                                INSERT INTO upload_pages (name, id, owner_id, root_dir_id, total_bytes, used_bytes)
                                VALUES ((?), (?), (?), (?), (?), (?))
                                ON CONFLICT (id) DO UPDATE SET name        = (?),
                                                               owner_id    = (?),
                                                               root_dir_id = (?),
                                                               total_bytes = (?),
                                                               used_bytes  = (?)
                                """)
                .bind(0, name())
                .bind(1, id())
                .bind(2, ownerId())
                .bind(3, rootDirId())
                .bind(4, totalBytes())
                .bind(5, usedBytes())
                .bind(6, name())
                .bind(7, ownerId())
                .bind(8, rootDirId())
                .bind(9, totalBytes())
                .bind(10, usedBytes())
                .execute()
        );
    }
}
