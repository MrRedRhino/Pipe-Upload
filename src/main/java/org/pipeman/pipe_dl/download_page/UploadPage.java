package org.pipeman.pipe_dl.download_page;

import org.pipeman.pipe_dl.util.misc.ChainLong;

import java.beans.ConstructorProperties;
import java.util.Optional;

import static org.pipeman.pipe_dl.DB.jdbi;

public class UploadPage {
    private String name;
    private final long id;
    private final long ownerId;
    private final long rootDirId;
    private final long totalBytes;
    private long usedBytes;

    @ConstructorProperties({"name", "id", "owner_id", "root_dir_id", "total_bytes", "used_bytes"})
    public UploadPage(String name, long id, long ownerId, long rootDirId, long totalBytes, long usedBytes) {
        this.name = name;
        this.id = id;
        this.ownerId = ownerId;
        this.rootDirId = rootDirId;
        this.totalBytes = totalBytes;
        this.usedBytes = usedBytes;
    }

    public long totalBytes() {
        return totalBytes;
    }

    public void usedBytes(long usedBytes) {
        this.usedBytes = usedBytes;
    }

    public ChainLong<UploadPage> usedBytesChain() {
        return new ChainLong<>(this, usedBytes, this::usedBytes);
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

    public static Optional<UploadPage> get(long id) {
        return jdbi().withHandle(handle -> handle.createQuery("SELECT * FROM upload_pages WHERE id = (?)")
                .bind(0, id)
                .mapTo(UploadPage.class)
                .findFirst());
    }

    public void save() {
        jdbi().useHandle(handle -> handle.createUpdate(
                "INSERT INTO upload_pages (name, id, owner_id, root_dir_id, total_bytes, used_bytes) VALUES ((?), (?), (?), (?), (?), (?)) " +
                        "ON CONFLICT (id) DO UPDATE SET name = (?), owner_id = (?), root_dir_id = (?), total_bytes = (?), used_bytes = (?)")
                .bind(0, name)
                .bind(1, ownerId)
                .bind(2, rootDirId)
                .bind(3, totalBytes)
                .bind(4, usedBytes)
                .bind(5, id)
                .bind(6, name)
                .bind(7, ownerId)
                .bind(8, rootDirId)
                .bind(9, totalBytes)
                .bind(10, usedBytes)
                .bind(11, id)
                .execute()
        );
    }
}
