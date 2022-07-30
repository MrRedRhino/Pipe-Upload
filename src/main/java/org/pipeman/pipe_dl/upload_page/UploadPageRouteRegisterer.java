package org.pipeman.pipe_dl.upload_page;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.pipe_file.FileHelper;
import org.pipeman.pipe_dl.pipe_file.PipeFile;
import org.pipeman.pipe_dl.util.misc.ModifiableFileHelper;
import org.pipeman.pipe_dl.util.pipe_route.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.pipe_route.RequestMethod;
import org.pipeman.pipe_dl.util.pipe_route.RoutePrefixes;
import org.pipeman.pipe_dl.util.pipe_route.RouteUtil;
import org.pipeman.pipe_dl.util.response_builder.ResponseBuilder;
import spark.Response;

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
                    PipeFile.get(folderFile.parent()).ifPresent(f -> addTableEntry(
                            fileTableBuilder, f.name("..")));

                    for (PipeFile file : PipeFile.getChildren(folderFile.id())) {
                        addTableEntry(fileTableBuilder, file);
                    }

                    return ModifiableFileHelper.copyFile(Main.config().downloadPage, response, Map.of(
                            "!title", folderFile.name(),
                            "!table-content", fileTableBuilder.toString()
                    ));
                })
                .buildAndRegister();

        new PipeRouteBuilder(RoutePrefixes.API, "/upload-pages/create")
                .acceptMethod(RequestMethod.PUT)
                .handle((user, request, response) -> {
                    ResponseBuilder rb = new ResponseBuilder(request, response, Map.of("new-id", ""));
                    String name = rb.getHeader("name");
                    rb.haltIfErrors();

                    if (user.getUploadPage() != null) return rb.addInvalidAndReturn("upload-page-count");

                    UploadPage page = UploadPage.createUploadPage(name, user.id(), 1_073_741_824L * 100); // 100 GB
                    if (page == null) return RouteUtil.msg("Creating the upload page failed. " +
                                                           "We are sorry for the inconvenience. " +
                                                           "Please report this issue.", response, 500);

                    rb.addResponse("new-id", page.id());
                    return rb.toString();
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

        switch (file.extension()) {
            case ".jpg", ".jpeg", ".png", ".gif" -> replacements.put("!extra-content",
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
