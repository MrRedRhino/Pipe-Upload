package org.pipeman.pipe_dl.files;

import org.pipeman.pipe_dl.Main;

import java.beans.ConstructorProperties;
import java.io.File;

public class PipeFile {
    private final long id;
    private final String name;
    private final long pageId;
    private final long directoryId;
    private final long creatorId;
    private final boolean isFolder;
    private final long size;

    @ConstructorProperties({"id", "name", "page_id", "directory_id", "creator_id", "is_folder", "size"})
    public PipeFile(long id, String name, long pageId, long directoryId, long creatorId, boolean isFolder, long size) {
        this.id = id;
        this.name = name;
        this.pageId = pageId;
        this.directoryId = directoryId;
        this.creatorId = creatorId;
        this.isFolder = isFolder;
        this.size = size;
    }

    public File toJavaFile() {
        return new File(Main.config().uploadDir + id);
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

    public long directoryId() {
        return directoryId;
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
}
