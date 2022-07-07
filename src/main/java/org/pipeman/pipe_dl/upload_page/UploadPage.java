package org.pipeman.pipe_dl.upload_page;

import java.beans.ConstructorProperties;

public class UploadPage {

    private final String name;
    private final long id;
    private final long ownerId;
    private final long rootDirId;
    private final long gbFree;

    @ConstructorProperties({"name", "id", "owner_id", "root_dir_id", "gb_free"})
    public UploadPage(String name, long id, long ownerId, long rootDirId, long gbFree) {
        this.name = name;
        this.id = id;
        this.ownerId = ownerId;
        this.rootDirId = rootDirId;
        this.gbFree = gbFree;
    }

    public long gbFree() {
        return gbFree;
    }
}
