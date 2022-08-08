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
    final boolean forUser;

    public PipeRoute(RequestMethod requestMethod, boolean checkAuth, Route handler, AuthorizedRoute authHandler, String path, Path filePath,
                     String routePrefix, boolean forUser) {
        this.requestMethod = requestMethod;
        this.checkAuth = checkAuth;
        this.handler = handler;
        this.authHandler = authHandler;
        this.filePath = filePath;
        this.forUser = forUser;
        this.path = routePrefix + (path.startsWith("/") ? path.substring(1) : path);
    }

    public static PipeRouteBuilder builder(String path) {
        return new PipeRouteBuilder(path);
    }

    public static PipeRouteBuilder builder(RoutePrefixes prefix, String path) {
        return new PipeRouteBuilder(prefix, path);
    }

    public void register() {
        RouteRegisterer.register(this);
    }

    public static class PipeRouteBuilder {
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
}
