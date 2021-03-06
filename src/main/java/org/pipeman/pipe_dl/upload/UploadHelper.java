package org.pipeman.pipe_dl.upload;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.pipe_file.PipeFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UploadHelper {
    private static final Map<String, RunningUpload> uploads = new HashMap<>();


    public static String createUpload(String filename, PipeFile path, long uploaderId) throws IOException {
        String id = UUID.randomUUID().toString();
        uploads.put(id, new RunningUpload(filename, path.parent(), path.pageId(), Main.uid.newUID(), uploaderId));
        return id;
    }


    public static boolean writeToUpload(String uploadID, byte[] data) throws IOException, PipeFile.FileTooBigException {
        RunningUpload upload = uploads.get(uploadID);
        if (upload == null) return false;

        upload.writeToFile(data);
        return true;
    }


    public static boolean finishUpload(String uploadID) throws IOException, PipeFile.FileTooBigException {
        RunningUpload upload = uploads.get(uploadID);
        if (upload == null) return false;

        upload.uploadFileData();
        uploads.remove(uploadID);
        return true;
    }


    public static boolean cancelUpload(String uploadID) {
        RunningUpload upload = uploads.get(uploadID);
        if (upload == null) return false;

        upload.deleteFile();
        uploads.remove(uploadID);
        return true;
    }
}
