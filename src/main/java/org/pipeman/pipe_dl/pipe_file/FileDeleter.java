package org.pipeman.pipe_dl.pipe_file;

import org.pipeman.pipe_dl.Main;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;

public class FileDeleter {
    private static final Logger LOGGER = LoggerFactory.getLogger(FileDeleter.class);

    public static void deleteFile(long id) {
        String path = Main.config().uploadDir + id;
        if (!new File(path).delete()) {
            LOGGER.error("Could not delete file! (Id: " + id + ", Path: " + path);
        }
    }
}
