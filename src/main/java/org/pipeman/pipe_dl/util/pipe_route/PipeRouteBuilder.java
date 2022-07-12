package org.pipeman.pipe_dl.util.pipe_route;

import spark.Route;

import java.nio.file.Path;

public class PipeRouteBuilder {
    private RequestMethod requestMethod = RequestMethod.GET;
    private boolean checkAuth = false;
    private Route handler;
    private Path filePath;
    private final String path;
    private String routePrefix;
    private AuthorizedRoute authHandler;
    private boolean forUser = false;

    public PipeRouteBuilder(String path) {
        this(RoutePrefixes.DEFAULT, path);
    }

    public PipeRouteBuilder(RoutePrefixes prefix, String path) {
        routePrefix(prefix);
        this.path = path;
    }

    public PipeRouteBuilder acceptMethod(RequestMethod method) {
        requestMethod = method;
        return this;
    }

    public PipeRouteBuilder routePrefix(RoutePrefixes prefix) {
        routePrefix = prefix.route;
        forUser = prefix.forUser;
        return this;
    }

    public PipeRouteBuilder checkAuth(boolean check) {
        checkAuth = check;
        return this;
    }

    public PipeRouteBuilder checkAuth() {
        checkAuth = true;
        return this;
    }

    public PipeRouteBuilder handle(Route route) {
        this.handler = route;
        return this;
    }

    public PipeRouteBuilder handle(AuthorizedRoute route) {
        this.checkAuth();
        this.authHandler = route;
        return this.checkAuth(true);
    }

    public PipeRouteBuilder handle(Path path) {
        this.filePath = path;
        return this;
    }

    public PipeRoute build() {
        return new PipeRoute(requestMethod, checkAuth, handler, authHandler, path, filePath, routePrefix, forUser);
    }

    public void buildAndRegister() {
        build().register();
    }
}
