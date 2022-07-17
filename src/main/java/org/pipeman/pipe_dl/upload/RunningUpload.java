package org.pipeman.pipe_dl.upload;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.upload_page.UploadPage;
import org.pipeman.pipe_dl.pipe_file.PipeFile;

import java.io.*;
import java.util.Optional;

public class RunningUpload {
    private final String filename;
    private final long directoryId;
    private final long uploadPageId;
    private final long id;
    private final long uploaderId;
    private OutputStream os;
    private long fileSize = 0;
    private final long maxSize;

    RunningUpload(String filename, long directoryId, long uploadPageId, long id, long uploaderId) throws IOException {
        this.filename = filename;
        this.directoryId = directoryId;
        this.id = id;
        this.uploadPageId = uploadPageId;
        this.uploaderId = uploaderId;

        Optional<UploadPage> page = UploadPage.get(uploadPageId);
        if (page.isEmpty()) {
            maxSize = 0;
            return;
        }
        maxSize = page.get().totalBytes() - page.get().usedBytes();

        openFile();
    }

    public void openFile() throws FileNotFoundException {
        os = new FileOutputStream(Main.config().uploadDir + id);
    }

    public void writeToFile(byte[] data) throws IOException, PipeFile.FileTooBigException {
        if (fileSize + data.length > maxSize) {
            throw new PipeFile.FileTooBigException();
        }

        os.write(data);
        fileSize += data.length;
    }

    public void uploadFileData() throws IOException, PipeFile.FileTooBigException {
        os.close();
        PipeFile f = PipeFile.createFile(id, filename, uploadPageId, uploaderId, directoryId, fileSize);
        if (f != null) f.save();
        else deleteFile();
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
