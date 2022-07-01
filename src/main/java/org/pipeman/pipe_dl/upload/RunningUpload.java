package org.pipeman.pipe_dl.upload;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.files.FileHelper;
import org.pipeman.pipe_dl.files.PipeFile;

import java.io.*;

public class RunningUpload {
    private final String filename;
    private final long directoryId;
    private final long uploadPageId;
    private final long id;
    private final long uploaderId;
    private OutputStream os;
    private long fileSize = 0;

    RunningUpload(String filename, long directoryId, long uploadPageId, long id, long uploaderId) throws IOException {
        this.filename = filename;
        this.directoryId = directoryId;
        this.id = id;
        this.uploadPageId = uploadPageId;
        this.uploaderId = uploaderId;
        openFile();
    }

    public void openFile() throws FileNotFoundException {
        os = new FileOutputStream(Main.config().uploadDir + id);
    }

    public void writeToFile(byte[] data) throws IOException {
        os.write(data);
        fileSize += data.length;
    }

    public void uploadFileData() throws IOException {
        os.close();
        FileHelper.uploadFile(new PipeFile(id, filename, uploadPageId, directoryId, uploaderId, false, fileSize));
    }

    public void deleteFile() {
        try {
            os.close();
        } catch (Exception ignored) {
        }

        File f = new File(Main.config().uploadDir + id);

        //noinspection ResultOfMethodCallIgnored
        f.delete();
    }
}
