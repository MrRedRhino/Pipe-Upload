package org.pipeman.pipe_dl.upload;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.files.FileHelper;
import org.pipeman.pipe_dl.files.PipeFile;
import org.pipeman.pipe_dl.users.User;
import org.pipeman.pipe_dl.util.pipe_route.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.pipe_route.RequestMethod;
import org.pipeman.pipe_dl.util.pipe_route.RoutePrefixes;
import org.pipeman.pipe_dl.util.response_builder.ResponseBuilder;
import spark.Request;
import spark.Response;

import java.io.*;
import java.util.Map;

public class UploadRouteRegisterer {
    public UploadRouteRegisterer() {
        registerRoutes();
        //noinspection ResultOfMethodCallIgnored
        new File(Main.config().uploadDir).mkdirs();
    }

    private void registerRoutes() {
        new PipeRouteBuilder("/upload")
                .handle(Main.config().upload)
                .buildAndRegister();

        new PipeRouteBuilder(RoutePrefixes.API, "/upload/create")
                .handle(this::startUpload)
                .acceptMethod(RequestMethod.POST)
                .buildAndRegister();

        new PipeRouteBuilder(RoutePrefixes.API, "/upload/upload/:id")
                .handle(this::upload)
                .acceptMethod(RequestMethod.POST)
                .buildAndRegister();

        new PipeRouteBuilder(RoutePrefixes.API, "/upload/finish/:id")
                .handle(this::finishUpload)
                .acceptMethod(RequestMethod.POST)
                .buildAndRegister();

        new PipeRouteBuilder(RoutePrefixes.API, "/upload/cancel/:id")
                .handle(this::cancelUpload)
                .acceptMethod(RequestMethod.POST)
                .buildAndRegister();
    }


    private String startUpload(User user, Request request, Response response) throws IOException {
        ResponseBuilder rb = new ResponseBuilder(request, response, Map.of("upload-id", ""));

        String filename = rb.getHeader("filename");
        String directory = rb.getHeader("folder-id");
        if (filename.length() > 30 || filename.isBlank()) rb.addInvalid("filename");

        rb.haltIfErrors();

        PipeFile dir = FileHelper.getFile(directory);
        if (dir == null || !dir.isFolder()) return rb.addInvalidAndReturn("folder-id");

        rb.addResponse("upload-id", UploadHelper.createUpload(filename, dir, user.id()));
        return rb.toString();
    }

    private String upload(Request request, Response response) throws IOException {
        ResponseBuilder rb = new ResponseBuilder(request, response);
        String uploadId = request.params("id");
        if (uploadId == null) return rb.addMissingAndReturn("upload-id");

        if (request.bodyAsBytes().length > 2_097_152) return rb.addInvalidAndReturn("body-size");

        try {
            if (!UploadHelper.writeToUpload(uploadId, request.bodyAsBytes())) return rb.addInvalidAndReturn("upload-id");
        } catch (RunningUpload.FileTooBigException e) {
            UploadHelper.cancelUpload(uploadId);
            return rb.addInvalidAndReturn("file-size");
        }
        return rb.toString();
    }

    private String finishUpload(Request request, Response response) throws IOException {
        ResponseBuilder rb = new ResponseBuilder(request, response);
        String uploadId = request.params("id");
        if (uploadId == null) return rb.addMissingAndReturn("upload-id");

        if (!UploadHelper.finishUpload(uploadId)) return rb.addInvalidAndReturn("upload-id");
        return rb.toString();
    }

    private String cancelUpload(Request request, Response response) {
        ResponseBuilder rb = new ResponseBuilder(request, response);
        String uploadId = request.params("id");
        if (uploadId == null) return rb.addMissingAndReturn("upload-id");

        if (!UploadHelper.cancelUpload(uploadId)) return rb.addInvalidAndReturn("upload-id");
        return rb.toString();
    }
}
