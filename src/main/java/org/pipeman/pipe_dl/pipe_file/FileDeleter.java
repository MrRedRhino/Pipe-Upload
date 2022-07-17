package org.pipeman.pipe_dl.pipe_file;

import org.pipeman.pipe_dl.Main;

import java.io.File;

public class FileDeleter {
    public static void deleteFile(long id) {
        //noinspection ResultOfMethodCallIgnored
        new File(Main.config().uploadDir + id).delete();
    }
}
