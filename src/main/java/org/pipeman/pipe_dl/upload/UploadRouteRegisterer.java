package org.pipeman.pipe_dl.upload;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.files.FileHelper;
import org.pipeman.pipe_dl.files.PipeFile;
import org.pipeman.pipe_dl.users.login.User;
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

        new PipeRouteBuilder("/upload/create")
                .routePrefix(RoutePrefixes.API)
                .handle(this::startUpload)
                .acceptMethod(RequestMethod.POST)
                .buildAndRegister();

        new PipeRouteBuilder("/upload/upload/:id")
                .routePrefix(RoutePrefixes.API)
                .handle(this::upload)
                .acceptMethod(RequestMethod.POST)
                .buildAndRegister();

        new PipeRouteBuilder("/upload/finish/:id")
                .routePrefix(RoutePrefixes.API)
                .handle(this::finishUpload)
                .acceptMethod(RequestMethod.POST)
                .buildAndRegister();

        new PipeRouteBuilder("/upload/cancel/:id")
                .routePrefix(RoutePrefixes.API)
                .handle(this::cancelUpload)
                .acceptMethod(RequestMethod.POST)
                .buildAndRegister();
    }


    private String startUpload(User user, Request request, Response response) throws IOException {
        ResponseBuilder rb = new ResponseBuilder(request, response, Map.of("upload-id", ""));

        String filename = rb.getHeader("filename");
        String directory = rb.getHeader("folder-id");

        rb.haltIfErrors();

        PipeFile dir = FileHelper.getFile(directory);
        if (dir == null) return rb.addInvalidAndReturn("folder-id");

        if (!dir.isFolder()) return rb.addInvalidAndReturn("folder-id");
        rb.setResponse("upload-id", UploadHelper.createUpload(filename, dir, user.id()));
        return rb.ret();
    }

    private String upload(Request request, Response response) throws IOException {
        ResponseBuilder rb = new ResponseBuilder(request, response);
        String uploadId = request.params("id");
        if (uploadId == null) return rb.addMissingAndReturn("upload-id");

        if (request.bodyAsBytes().length > 2_097_152) return rb.addInvalidAndReturn("body-size");

        if (!UploadHelper.writeToUpload(uploadId, request.bodyAsBytes())) rb.addInvalid("upload-id");
        return rb.ret();
    }

    private String finishUpload(Request request, Response response) throws IOException {
        ResponseBuilder rb = new ResponseBuilder(request, response);
        String uploadId = request.params("id");
        if (uploadId == null) return rb.addMissingAndReturn("upload-id");

        if (!UploadHelper.finishUpload(uploadId)) return rb.addInvalidAndReturn("upload-id");
        return rb.ret();
    }

    private String cancelUpload(Request request, Response response) {
        ResponseBuilder rb = new ResponseBuilder(request, response);
        String uploadId = request.params("id");
        if (uploadId == null) return rb.addMissingAndReturn("upload-id");

        if (!UploadHelper.cancelUpload(uploadId)) return rb.addInvalidAndReturn("upload-id");
        return rb.ret();
    }
}
