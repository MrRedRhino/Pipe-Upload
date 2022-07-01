package org.pipeman.pipe_dl.util.pipe_route;

import org.pipeman.pipe_dl.Main;

public enum RoutePrefixes {
    API(Main.config().baseApiRoute),
    DEFAULT(Main.config().baseRoute);

    final String route;

    RoutePrefixes(String prefix) {
        route = prefix;
    }
}
