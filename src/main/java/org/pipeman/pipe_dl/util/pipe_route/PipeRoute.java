package org.pipeman.pipe_dl.util.pipe_route;

import spark.Route;

import java.nio.file.Path;

public class PipeRoute {
    final RequestMethod requestMethod;
    final boolean checkAuth;
    final Route handler;
    final AuthorizedRoute authHandler;
    final Path filePath;
    final String path;

    public PipeRoute(RequestMethod requestMethod, boolean checkAuth, Route handler, AuthorizedRoute authHandler, String path, Path filePath,
                     String routePrefix) {
        this.requestMethod = requestMethod;
        this.checkAuth = checkAuth;
        this.handler = handler;
        this.authHandler = authHandler;
        this.filePath = filePath;
        this.path = routePrefix + (path.startsWith("/") ? path.substring(1) : path);
    }

    public void register() {
        RouteRegisterer.register(this);
    }
}
