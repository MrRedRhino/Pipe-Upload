package org.pipeman.pipe_dl.download_page;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.files.FileHelper;
import org.pipeman.pipe_dl.files.PipeFile;
import org.pipeman.pipe_dl.util.ModifiableFileHelper;
import org.pipeman.pipe_dl.util.routes.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.routes.RouteUtil;

import java.util.Map;

public class DownloadPageRouteRegisterer {
    public DownloadPageRouteRegisterer() {
        registerRoutes();
    }

    private void registerRoutes() {
        new PipeRouteBuilder("/files/:folder")
                .handle((request, response) -> {
                    PipeFile folderFile = FileHelper.getFile(request.params("folder"));
                    if (folderFile == null) return RouteUtil.msg("Not found", response, 404);

                    if (!folderFile.isFolder()) return RouteUtil.msg("Not a folder", response, 400);

                    StringBuilder fileTableBuilder = new StringBuilder();
                    for (PipeFile file : FileHelper.listDir(folderFile.id())) {
                        addTableEntry(fileTableBuilder, file);
                    }

                    ModifiableFileHelper.copyFile(Main.config().downloadPage, response, Map.of(
                            "!title", folderFile.name(),
                            "!table-content", fileTableBuilder.toString()
                    ));

                    return "";
                })
                .buildAndRegister();
    }

    private void addTableEntry(StringBuilder builder, PipeFile file) {
        builder.append("<tr><td><img src=\"").append(file.isFolder() ? "/images/folder-icon-64.png" : "/images/file-icon-64.png");
        builder.append("\" height=\"20px\" alt=\"Folder\"></td>\n");
        builder.append("<td><a href=\"").append("link-to-file");
        builder.append("\">").append(file.name()).append(file.isFolder() ? "/" : "").append("</a></td>\n");
        builder.append("<td>").append("42 GB").append("</td>");
        builder.append("</tr>\n");
    }



    /*
    <tr>
        <td><img src="file-icon.svg" height="20px" alt="Folder"></td>
        <td><a href="">video.mp4</a> </td>
        <td>4.2 TB</td>
    </tr>
    <tr>
        <td><img src="folder-icon.svg" height="20px" alt="Folder"></td>
        <td><a href="">music/</a></td>
        <td>42 GB</td>
    </tr>
     */
}
