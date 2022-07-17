package org.pipeman.pipe_dl.pipe_file;

public class FileHelper {
    public static PipeFile getFile(String id) {
        if (id == null) return null;
        try {
            return PipeFile.get(Long.parseLong(id)).orElse(null);
        } catch (NumberFormatException ignored) {
            return null;
        }
    }
}
