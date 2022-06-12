package org.pipeman.pipe_dl.download;

import org.pipeman.pipe_dl.Config;
import org.pipeman.pipe_dl.files.FileHelper;
import org.pipeman.pipe_dl.files.PipeFile;
import org.pipeman.pipe_dl.util.ModifiableFileHelper;
import org.pipeman.pipe_dl.util.routes.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.routes.RouteUtil;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

public class DownloadRouteRegisterer {
    public DownloadRouteRegisterer() {
        registerRoutes();
    }

    private void registerRoutes() {
        new PipeRouteBuilder("/files/:folder")
                .handle((request, response) -> {
                    String folder = request.params("folder");
                    if (folder == null) return RouteUtil.msg("Not found", response, 404);

                    PipeFile folderFile = FileHelper.getFile(Long.parseLong(folder));
                    if (folderFile == null) return RouteUtil.msg("Not found", response, 404);

                    if (!folderFile.isFolder()) return handleDownload(request, response);

                    StringBuilder builder = new StringBuilder();
                    for (PipeFile file : FileHelper.listDir(folderFile.id())) {
                        // <li><a href="">something</a></li>
                        builder.append("<li><a href=\"")
                                .append(file.id()).append("\">").append(file.name())
                                .append("</a></li>");
                    }

                    ModifiableFileHelper.copyFile(Config.HtmlFiles.FILE_INDEX, response, Map.of(
                            "!dir_name", folderFile.name(),
                            "!list_content", builder.toString()
                    ));

                    return "";
                }).buildAndRegister(); // wss://gateway.discord.gg/?encoding=json&v=9&compress=zlib-stream
    }

    private String handleDownload(Request request, Response response) throws IOException {
        long id;
        try {
            id = Long.parseLong(request.params("folder"));
        } catch (Exception ignored) {
            return RouteUtil.msg("File id invalid", response, 400);
        }

        PipeFile file = FileHelper.getFile(id);
        if (file == null) return RouteUtil.msg("File not found", response, 400);

        response.header("Content-Disposition", "attachment;filename=" + file.name());
        Files.copy(file.toJavaFile().toPath(), response.raw().getOutputStream());
        return "";
    }
}
