package org.pipeman.pipe_dl.upload;

import org.pipeman.pipe_dl.Config;
import org.pipeman.pipe_dl.upload_page.UploadPage;
import org.pipeman.pipe_dl.upload_page.UploadPageHelper;
import org.pipeman.pipe_dl.util.routes.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.routes.RequestMethod;
import org.pipeman.pipe_dl.util.routes.RoutePrefixes;
import org.pipeman.pipe_dl.util.routes.RouteUtil;
import spark.Request;
import spark.Response;

import java.io.*;

public class UploadManager {
    public UploadManager() {
        registerRoutes();
        //noinspection ResultOfMethodCallIgnored
        new File(Config.Upload.UPLOAD_DIRECTORY).mkdirs();
    }

    private void registerRoutes() {
        new PipeRouteBuilder("/upload")
                .handle(Config.HtmlFiles.UPLOAD)
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


    private String startUpload(Request request, Response response) throws IOException {
        String filename = request.headers("filename");
        String directory = request.headers("folder-id");
        String page = request.headers("upload-page-id");

        if (filename == null || filename.isBlank() || directory == null || directory.isBlank() || page == null || page.isBlank()) {
            return RouteUtil.msg("Request headers 'filename', 'upload-page-id' and 'directory' are required",
                    response, 400);
        }

        long uploadPageId;
        try {
            uploadPageId = Long.parseLong(page);
        } catch (NumberFormatException ignored) {
            return RouteUtil.msg("Header 'upload-page-id' invalid");
        }

        UploadPage uploadPage = UploadPageHelper.fetch(uploadPageId);
        if (uploadPage == null) {
            return RouteUtil.msg("Upload page not found", response, 400);
        }

        String uploadId = UploadHelper.createUpload(filename, directory, uploadPage);
        return "{\"upload-id\": \"" + uploadId + "\"}";
    }

    private String upload(Request request, Response response) throws IOException {
        String uploadId = request.params("id");
        if (uploadId == null) return RouteUtil.msg("Parameter upload ID is missing.", response, 400);

        byte[] body = request.raw().getInputStream().readAllBytes();
        if (body.length > 2_097_152) return RouteUtil.msg("Body too large, only 2 MiB allowed", response, 400);

        if (!UploadHelper.writeToUpload(uploadId, body)) {
            return RouteUtil.msg("Upload not found", response, 404);
        }
        return "";
    }

    private String finishUpload(Request request, Response response) throws IOException {
        String uploadId = request.params("id");
        if (uploadId == null) return RouteUtil.msg("Parameter upload ID is missing.", response, 400);

        if (!UploadHelper.finishUpload(uploadId)) {
            return RouteUtil.msg("Upload not found", response, 404);
        }
        return "";
    }

    private String cancelUpload(Request request, Response response) {
        String uploadId = request.params("id");
        if (uploadId == null) return RouteUtil.msg("Parameter upload ID is missing.", response, 400);

        if (!UploadHelper.cancelUpload(uploadId)) {
            return RouteUtil.msg("Upload not found", response, 404);
        }
        return "";
    }
}
