package org.pipeman.pipe_dl.util.routes;

import spark.Route;

import java.nio.file.Path;

public class PipeRoute {
    RequestMethod requestMethod;
    boolean checkAuth;
    Route handler;
    Path filePath;
    String path;

    public PipeRoute(RequestMethod requestMethod, boolean checkAuth, Route handler, String path, Path filePath,
                     String routePrefix) {
        this.requestMethod = requestMethod;
        this.checkAuth = checkAuth;
        this.handler = handler;
        this.filePath = filePath;
        this.path = routePrefix + (path.startsWith("/") ? path.substring(1) : path);
    }

    public void register() {
        RouteRegisterer.register(this);
    }
}
