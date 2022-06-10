package org.pipeman.pipe_dl.upload;

import org.pipeman.pipe_dl.Config;
import org.pipeman.pipe_dl.files.FileHelper;
import org.pipeman.pipe_dl.files.PipeFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RunningUpload {
    private final String filename;
    private final long directoryId;
    private final long uploadPageId;
    private final long id;
    private final long uploaderId;
    private OutputStream os;

    RunningUpload(String filename, long directoryId, long uploadPageId, long id, long uploaderId) throws IOException {
        this.filename = filename;
        this.directoryId = directoryId;
        this.id = id;
        this.uploadPageId = uploadPageId;
        this.uploaderId = uploaderId;
        openFile();
    }

    public void openFile() throws IOException {
        os = new FileOutputStream(Config.Upload.UPLOAD_DIRECTORY + id);
    }

    public void writeToFile(byte[] data) throws IOException {
        os.write(data);
    }

    public void uploadFileData() throws IOException {
        os.close();
        FileHelper.uploadFile(new PipeFile(id, filename, uploadPageId, directoryId, uploaderId, false));
    }

    public void deleteFile() {
        try {
            os.close();
        } catch (Exception ignored) {
        }

        File f = new File(Config.Upload.UPLOAD_DIRECTORY + id);

        //noinspection ResultOfMethodCallIgnored
        f.delete();
    }
}
