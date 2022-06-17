package org.pipeman.pipe_dl.download;

import org.pipeman.pipe_dl.files.FileHelper;
import org.pipeman.pipe_dl.files.PipeFile;
import org.pipeman.pipe_dl.util.routes.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.routes.RoutePrefixes;
import org.pipeman.pipe_dl.util.routes.RouteUtil;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.nio.file.Files;

public class DownloadRouteRegisterer {
    public DownloadRouteRegisterer() {
        registerRoutes();
    }

    private void registerRoutes() {
        new PipeRouteBuilder("/download/:file")
                .routePrefix(RoutePrefixes.API)
                .handle((request, response) -> {
                    PipeFile folderFile = FileHelper.getFile(request.params("file"));
                    if (folderFile == null) return RouteUtil.msg("Not found", response, 404);

                    if (!folderFile.isFolder()) return handleDownload(request, response);

                    return "";
                }).buildAndRegister(); // wss://gateway.discord.gg/?encoding=json&v=9&compress=zlib-stream
    }

    private String handleDownload(Request request, Response response) throws IOException {
        PipeFile file = FileHelper.getFile(request.params("folder"));
        if (file == null) return RouteUtil.msg("File not found", response, 400);

        response.header("Content-Disposition", "attachment;filename=" + file.name());
        Files.copy(file.toJavaFile().toPath(), response.raw().getOutputStream());
        return "";
    }
}
