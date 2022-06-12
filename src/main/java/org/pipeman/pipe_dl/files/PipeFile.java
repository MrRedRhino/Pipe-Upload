package org.pipeman.pipe_dl.files;

import org.pipeman.pipe_dl.Config;

import java.beans.ConstructorProperties;
import java.io.File;

public class PipeFile {
    private final long id;
    private final String name;
    private final long pageId;
    private final long directoryId;
    private final long creatorId;
    private final boolean isFolder;

    @ConstructorProperties({"id", "name", "page_id", "directory_id", "creator_id", "is_folder"})
    public PipeFile(long id, String name, long pageId, long directoryId, long creatorId, boolean isFolder) {
        this.id = id;
        this.name = name;
        this.pageId = pageId;
        this.directoryId = directoryId;
        this.creatorId = creatorId;
        this.isFolder = isFolder;
    }

    public File toJavaFile() {
        return new File(Config.Upload.UPLOAD_DIRECTORY + id);
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
}
