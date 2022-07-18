package org.pipeman.pipe_dl.pipe_file;

import org.jdbi.v3.core.statement.UnableToExecuteStatementException;
import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.util.misc.Utils;

import java.beans.ConstructorProperties;
import java.io.File;
import java.util.List;
import java.util.Optional;

import static org.pipeman.pipe_dl.DB.jdbi;

public class PipeFile {
    private final long id;
    private final long pageId;
    private final long creatorId;
    private final boolean isFolder;
    private final long size;
    private String name;
    private final String path;

    @ConstructorProperties({"id", "name", "page_id", "creator_id", "is_folder", "size", "path"})
    public PipeFile(long id, String name, long pageId, long creatorId, boolean isFolder, long size, String path) {
        this.id = id;
        this.name = name;
        this.pageId = pageId;
        this.path = path;
        this.creatorId = creatorId;
        this.isFolder = isFolder;
        this.size = size;
    }

    public static Optional<PipeFile> get(long id) {
        return jdbi().withHandle(handle -> handle.createQuery("SELECT * FROM files WHERE id = (?) LIMIT 1")
                .bind(0, id)
                .mapTo(PipeFile.class).findFirst());
    }

    public static List<PipeFile> getChildren(long id) {
        return jdbi().withHandle(handle -> handle.createQuery(
                "SELECT * FROM files WHERE path ~ '*." + id + ".*{1}' ORDER BY name")
                .mapTo(PipeFile.class).list());
    }

    public PipeFile createChildFolder(String name, long creatorId) {
        long newId = Main.uid.newUID();
        return new PipeFile(newId, name, pageId(), creatorId, false, 0, path() + "." + newId);
    }

    public PipeFile createChildFile(String name, long creatorId, long fileSize) {
        long newId = Main.uid.newUID();
        return new PipeFile(newId, name, pageId(), creatorId, false, fileSize, path() + "." + newId);
    }

    @Deprecated
    public static PipeFile createFile(long id, String name, long pageId, long creatorId, long parent, long size) {
        PipeFile parentFile = PipeFile.get(parent).orElse(null);
        if (parentFile == null || !parentFile.isFolder()) return null;

        return new PipeFile(id, name, pageId, creatorId, false, size, parentFile.path() + "." + id);
    }

    public File toJavaFile() {
        return new File(Main.config().uploadDir + id());
    }

    public long id() {
        return id;
    }

    public long pageId() {
        return pageId;
    }

    public String name() {
        return name;
    }

    public PipeFile name(String newName) {
        this.name = newName;
        return this;
    }

    public PipeFile adopt(long newParentId) {
        return this;
    }

    public String path() {
        return path;
    }

    public long parent() {
        long[] ancestors = ancestors();
        return ancestors[Math.max(ancestors.length - 2, 0)];
    }

    public long[] ancestors() {
        return Utils.parseToLongArray(path().split("\\."));
    }

    public long creatorId() {
        return creatorId;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public String stringSize() {
        long size = size();
        if (size < 1024) return size + " B";
        if (size < 1024 * 1024) return (size / 1024) + " KB";
        if (size < 1024 * 1024 * 1024) return (size / 1024 / 1024) + " MB";
        return (size / 1024 / 1024 / 1024) + " GB";
    }

    public long size() {
        return size;
    }

    public String extension() {
        int lastIndexOfDot = name().lastIndexOf(".");
        return lastIndexOfDot == -1 ? name() : name().substring(lastIndexOfDot);
    }

    public void delete() {
        final List<DeletedFile> deletedFiles = jdbi().withHandle(handle ->
                handle.createQuery("DELETE FROM files WHERE path <@ (?)::ltree RETURNING id, is_folder")
                        .bind(0, path())
                        .mapTo(DeletedFile.class)
                        .list()
        );
        for (DeletedFile f : deletedFiles) {
            if (!f.isFolder()) FileDeleter.deleteFile(f.id());
        }
    }

    public void save() throws FileTooBigException {
        try {
            jdbi().useHandle(handle -> handle.createUpdate("""
                            INSERT INTO files (id, name, page_id, creator_id, is_folder, size, path)
                            VALUES ((?), (?), (?), (?), (?), (?), (?)::ltree)
                            ON CONFLICT (id) DO UPDATE SET id         = (?),
                                                           name       = (?),
                                                           page_id    = (?),
                                                           creator_id = (?),
                                                           is_folder  = (?),
                                                           size       = (?),
                                                           path       = (?)::ltree
                                                           """)
                    .bind(0, id())
                    .bind(1, name())
                    .bind(2, pageId())
                    .bind(3, creatorId())
                    .bind(4, isFolder())
                    .bind(5, size())
                    .bind(6, path())
                    .bind(7, id())
                    .bind(8, name())
                    .bind(9, pageId())
                    .bind(10, creatorId())
                    .bind(11, isFolder())
                    .bind(12, size())
                    .bind(13, path())
                    .execute());
        } catch (UnableToExecuteStatementException e) {
            if (e.getCause().getMessage().equalsIgnoreCase("ERROR: Available storage space exceeded."))
                throw new PipeFile.FileTooBigException();
            else e.printStackTrace();
        }
    }

    @Override
    public String toString() {
        return "PipeFile{" +
               "id=" + id +
               ", pageId=" + pageId +
               ", isFolder=" + isFolder +
               ", name='" + name + '\'' +
               ", path='" + path + '\'' +
               '}';
    }

    public static class FileTooBigException extends Exception {
    }

    public record DeletedFile(long id, boolean isFolder) {
        @ConstructorProperties({"id", "is_folder"})
        public DeletedFile {
        }
    }
}
