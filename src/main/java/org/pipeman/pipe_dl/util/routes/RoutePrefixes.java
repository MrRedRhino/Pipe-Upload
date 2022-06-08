package org.pipeman.pipe_dl.util.routes;

public enum RoutePrefixes {
    API("/api/"),
    DEFAULT("/");

    final String route;

    RoutePrefixes(String prefix) {
        route = prefix;
    }
}
