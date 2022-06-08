package org.pipeman.pipe_dl.upload;

import org.pipeman.pipe_dl.Config;
import org.pipeman.pipe_dl.upload_page.UploadPage;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class RunningUpload {
    private final String filename;
    private final String path;
    private final UploadPage uploadPage;
    private final long id;
    private OutputStream os;

    RunningUpload(String filename, String path, UploadPage uploadPage, long id) throws IOException {
        this.filename = filename;
        this.path = path;
        this.id = id;
        this.uploadPage = uploadPage;
        openFile();
    }

    public void openFile() throws IOException {
        os = new FileOutputStream(Config.Upload.UPLOAD_DIRECTORY + uploadPage.id() + "/" + id);
    }

    public void writeToFile(byte[] data) throws IOException {
        os.write(data);
    }

    public void uploadFileData() throws IOException {
        os.close();

    }

    public void deleteFile() {
        try {
            os.close();
        } catch (Exception ignored) {
        }

        File f = new File(Config.Upload.UPLOAD_DIRECTORY + id);
        f.delete();
    }
}
