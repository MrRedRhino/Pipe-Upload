package org.pipeman.pipe_dl.files;

import org.json.JSONArray;
import org.json.JSONObject;
import org.pipeman.pipe_dl.pipe_file.FileHelper;
import org.pipeman.pipe_dl.pipe_file.PipeFile;
import org.pipeman.pipe_dl.users.User;
import org.pipeman.pipe_dl.util.pipe_route.PipeRoute;
import org.pipeman.pipe_dl.util.pipe_route.RequestMethod;
import org.pipeman.pipe_dl.util.pipe_route.RoutePrefixes;
import org.pipeman.pipe_dl.util.response_builder.ResponseBuilder;
import spark.Request;
import spark.Response;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;

public class FileApiRouteRegisterer {
    public FileApiRouteRegisterer() {
        registerRoutes();
    }

    private void registerRoutes() {
        PipeRoute.builder(RoutePrefixes.API, "/files/create-file")
                .acceptMethod(RequestMethod.POST)
                .handle((user, request, response) -> createFileOrFolder(user, request, response, true))
                .buildAndRegister();

        PipeRoute.builder(RoutePrefixes.API, "/files/create-folder")
                .acceptMethod(RequestMethod.POST)
                .handle((user, request, response) -> createFileOrFolder(user, request, response, false))
                .buildAndRegister();

        PipeRoute.builder(RoutePrefixes.API, "/files/*")
                .acceptMethod(RequestMethod.POST)
                .handle((user, request, response) -> {
                    ResponseBuilder rb = new ResponseBuilder(request, response);

                    if (request.splat().length == 0) return rb.addMissingAndReturn("file-id");
                    PipeFile file = FileHelper.getFile(request.splat()[0]);
                    if (file == null) return rb.addInvalidAndReturn("file-id");

                    String action = request.queryParams("action");

                    /*
                    Actions:
                    - delete
                    - move
                    - rename
                    - write
                    - close
                    - copy files and folders (TODO)
                     */

                    switch (action) {
                        case "delete" -> file.delete();
                        case "move" -> {
                            Long newId = rb.getHeaderLong("new-directory-id");
                            rb.haltIfErrors();
                            PipeFile newFile = PipeFile.get(newId).orElse(null);
                            if (newFile == null) return rb.addInvalidAndReturn("new-directory-id");
                            if (!newFile.isFolder()) return rb.addInvalidAndReturn("new-directory-type");

                            file.setPath(newFile.path());
                            file.save();
                        }
                        case "rename" -> {
                            String newName = rb.getHeader("new-filename");
                            rb.haltIfErrors();
                            file.name(newName).save();
                        }
                        case "copy" -> {

                        }
                    }

                    return rb.toString();
                }).buildAndRegister();

        PipeRoute.builder(RoutePrefixes.API, "/files/*")
                .handle((request, response) -> {
                    ResponseBuilder rb = new ResponseBuilder(request, response);

                    if (request.splat().length == 0) return rb.addMissingAndReturn("file-id");
                    PipeFile file = FileHelper.getFile(request.splat()[0]);
                    if (file == null) return rb.addInvalidAndReturn("file-id");

                    if (file.isFolder()) {
                        JSONArray fileList = new JSONArray();
                        for (PipeFile child : PipeFile.getChildren(file.id())) {
                            fileList.put(serializeFile(child));
                        }
                        return rb.addResponse("files", fileList).toString();
                    } else {
                        handleDownload(file, response);
                    }
                    return "";
                }).buildAndRegister();
    }

    private JSONObject serializeFile(PipeFile file) {
        JSONObject out = new JSONObject();
        out.put("id", file.id());
        out.put("name", file.name());
        out.put("is-folder", file.isFolder());
        out.put("page-id", file.pageId());
        out.put("creator", file.creatorId());
        out.put("size", file.size());
        out.put("size-formatted", file.stringSize());
        out.put("path", file.path());
        return out;
    }

    private void handleDownload(PipeFile file, Response response) throws IOException {
        response.header("Content-Disposition", "attachment;filename=" + file.name());
        response.header("Content-Length", "" + file.size());
        Files.copy(file.toJavaFile().toPath(), response.raw().getOutputStream());
    }

    private String createFileOrFolder(User user, Request request, Response response, boolean file) throws Exception {
        ResponseBuilder rb = new ResponseBuilder(request, response, Map.of("file-id", ""));
        String name = rb.getHeader("filename");
        Long folderID = rb.getHeaderLong("folder-id");
        rb.haltIfErrors();
        Optional<PipeFile> folder = PipeFile.get(folderID);
        if (folder.isEmpty()) return rb.addInvalidAndReturn("folder-id");

        PipeFile newFile;
        if (file) newFile = folder.get().createChildFile(name, user.id(), 0);
        else newFile = folder.get().createChildFolder(name, user.id());

        if (newFile == null) return rb.addInvalidAndReturn("folder-id");
        newFile.save();
        rb.addResponse("file-id", newFile.id());

        return rb.toString();
    }
}
