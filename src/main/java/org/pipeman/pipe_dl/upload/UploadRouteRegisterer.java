package org.pipeman.pipe_dl.upload;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.files.FileHelper;
import org.pipeman.pipe_dl.files.PipeFile;
import org.pipeman.pipe_dl.users.login.User;
import org.pipeman.pipe_dl.users.login.AccountHelper;
import org.pipeman.pipe_dl.util.routes.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.routes.RequestMethod;
import org.pipeman.pipe_dl.util.routes.RoutePrefixes;
import org.pipeman.pipe_dl.util.routes.RouteUtil;
import spark.Request;
import spark.Response;

import java.io.*;

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


    private String startUpload(Request request, Response response) throws IOException {
        String filename = request.headers("filename");
        String directory = request.headers("folder-id");

        if (filename == null || filename.isBlank() || directory == null || directory.isBlank()) {
            return RouteUtil.msg("Request headers 'filename' and 'folder-id' are required", response, 400);
        }

        User user = AccountHelper.getAccountByRequest(request);
        if (user == null) {
            return RouteUtil.msg("Authorisation incorrect", response, 400);
        }

        PipeFile dir = FileHelper.getFile(directory);
        if (dir == null) return RouteUtil.msg("Directory not found", response, 400);
        if (!dir.isFolder()) return RouteUtil.msg("Folder-id is not a directory", response, 400);

        String uploadId = UploadHelper.createUpload(filename, dir, user.id());
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
