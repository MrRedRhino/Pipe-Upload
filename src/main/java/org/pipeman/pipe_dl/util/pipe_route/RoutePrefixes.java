package org.pipeman.pipe_dl.util.pipe_route;

import org.pipeman.pipe_dl.Main;

public enum RoutePrefixes {
    API(Main.config().baseApiRoute, false),
    DEFAULT(Main.config().baseRoute, true);

    final String route;
    final boolean forUser;

    RoutePrefixes(String prefix, boolean forUser) {
        route = prefix;
        this.forUser = forUser;
    }
}
