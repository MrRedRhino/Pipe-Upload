package org.pipeman.pipe_dl.upload_page;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.files.FileHelper;
import org.pipeman.pipe_dl.files.PipeFile;
import org.pipeman.pipe_dl.util.ModifiableFileHelper;
import org.pipeman.pipe_dl.util.pipe_route.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.pipe_route.RouteUtil;
import spark.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

public class UploadPageRouteRegisterer {
    public UploadPageRouteRegisterer() {
        registerRoutes();
    }

    private void registerRoutes() {
        new PipeRouteBuilder("/files/:file-id")
                .handle((request, response) -> {
                    PipeFile folderFile = FileHelper.getFile(request.params("file-id"));
                    if (folderFile == null) return RouteUtil.msg("Not found", response, 404);

                    if (!folderFile.isFolder()) return viewFile(folderFile, response);

                    StringBuilder fileTableBuilder = new StringBuilder();
                    for (PipeFile file : FileHelper.listDir(folderFile.id())) {
                        addTableEntry(fileTableBuilder, file);
                    }

                    return ModifiableFileHelper.copyFile(Main.config().downloadPage, response, Map.of(
                            "!title", folderFile.name(),
                            "!table-content", fileTableBuilder.toString()
                    ));
                })
                .buildAndRegister();


        new PipeRouteBuilder("/files/:file-id/*")
                .handle((request, response) -> {
                    if (request.splat().length == 0) return RouteUtil.msg("Not found", response, 404);
                    String action = request.splat()[0];
                    PipeFile file = FileHelper.getFile(request.params("file-id"));
                    if (file == null) return RouteUtil.msg("Not found", response, 404);

                    if (action.equals("download")) {
                        response.header("Content-Disposition", "attachment;filename=" + file.name());
                        response.header("Content-Length", String.valueOf(file.size()));

                        try {
                            Files.copy(file.toJavaFile().toPath(), response.raw().getOutputStream());
                        } catch (IOException ignored) {
                        }
                        return "";
                    }

                    if (action.equals("stream")) {
                        response.header("Content-Length", String.valueOf(file.size()));
                        response.status(206);

                        try {
                            Files.copy(file.toJavaFile().toPath(), response.raw().getOutputStream());
                        } catch (IOException ignored) {
                        }
                        return "";
                    }

                    return RouteUtil.msg("Unknown action", response, 400);
                })
                .buildAndRegister();
    }

    private void addTableEntry(StringBuilder builder, PipeFile file) {
        builder.append("<tr><td><img src=\"").append(file.isFolder() ? "/images/folder-icon-64.png" : "/images/file" +
                "-icon-64.png");
        builder.append("\" height=\"20px\" alt=\"Folder\"></td>\n");
        builder.append("<td><a href=\"").append("/files/").append(file.id()).append("\">");
        builder.append(file.name()).append(file.isFolder() ? "/" : "").append("</a></td>\n");
        builder.append("<td>").append(file.stringSize()).append("</td>");
        builder.append("</tr>\n");
    }


    private String viewFile(PipeFile file, Response response) {
        Map<String, String> replacements = new HashMap<>(Map.of(
                "!name", file.name(),
                "!file-size", file.stringSize(),
                "!download-url", "/files/" + file.id() + "/download",
                "!stream-url", "/files/" + file.id() + "/stream"
        ));

        switch (file.name().substring(file.name().lastIndexOf("."))) {
            case ".jpg", ".jpeg", ".png", ".gif" ->
                    replacements.put("!extra-content",
                            "<img src=\"/files/" + file.id() + "/stream\" alt=\"" + file.name() + "\">");

            case ".mp3", ".wav", ".flac", ".ogg" ->
                    replacements.put("!extra-content", "<audio controls><source src=\"/files/" + file.id() + "/stream" +
                            "\" type=\"audio/mpeg\"></audio>");

            case ".mp4", ".webm", ".mkv", ".avi" ->
                    replacements.put("!extra-content", "<video controls><source src=\"/files/" + file.id() + "/stream" +
                            "\" type=\"video/mp4\"></video>");

            default -> replacements.put("!extra-content", "");
        }

        return ModifiableFileHelper.copyFile(Main.config().fileView, response, replacements);
    }
}
