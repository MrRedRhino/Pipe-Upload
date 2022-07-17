package org.pipeman.pipe_dl.files;

import org.pipeman.pipe_dl.pipe_file.FileHelper;
import org.pipeman.pipe_dl.pipe_file.PipeFile;
import org.pipeman.pipe_dl.util.pipe_route.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.pipe_route.RoutePrefixes;
import org.pipeman.pipe_dl.util.response_builder.ResponseBuilder;
import spark.Response;

import java.io.IOException;
import java.nio.file.Files;

public class FileApiRouteRegisterer {
    public FileApiRouteRegisterer() {
        registerRoutes();
    }

    private void registerRoutes() {
        new PipeRouteBuilder(RoutePrefixes.API, "/files/*")
                .handle((request, response) -> {
                    ResponseBuilder rb = new ResponseBuilder(request, response);
                    // delete > delete
                    // download > get
                    // move > patch
                    // rename > patch

                    if (request.splat().length == 0) return rb.addInvalidAndReturn("file-id");
                    PipeFile file = FileHelper.getFile(request.splat()[0]);
                    if (file == null) return rb.addInvalidAndReturn("file-id");

                    String action = request.queryParams("action");
                    if (action == null) return rb.addMissingAndReturn("action");

                    switch (action) {
                        case "delete" -> file.delete();
                        case "download" -> handleDownload(file, response);
                        case "move" -> {
                            Long newId = rb.getHeaderLong("new-directory-id");
                            rb.haltIfErrors();
                            PipeFile newFile = PipeFile.get(newId).orElse(null);
                            if (newFile == null) return rb.addInvalidAndReturn("new-directory-id");

                            file.adopt(newFile.parent()).save();
                        }
                        case "rename" -> {
                            String newName = rb.getHeader("new-filename");
                            rb.haltIfErrors();
                            file.name(newName).save();
                        }
                    }

                    return rb.toString();
                }).buildAndRegister();
    }

    private void handleDownload(PipeFile file, Response response) throws IOException {
        response.header("Content-Disposition", "attachment;filename=" + file.name());
        response.header("Content-Length", "" + file.size());
        Files.copy(file.toJavaFile().toPath(), response.raw().getOutputStream());
    }
}
