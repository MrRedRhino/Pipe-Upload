package org.pipeman.pipe_dl.util.routes;

import org.pipeman.pipe_dl.login.AccountHelper;
import spark.Route;
import spark.Spark;

import java.nio.file.Files;


public class RouteRegisterer {

    public static void register(PipeRoute route) {
        Route r = (request, response) -> {
            if (route.checkAuth) {
                if (!AccountHelper.checkAuth(request)) {
                    response.header("Location", "/accounts/login");
                    return RouteUtil.msg("Unauthorized", response, 302);
                }
            }

            if (route.filePath != null) Files.copy(route.filePath, response.raw().getOutputStream());

            if (route.handler != null) {
                return route.handler.handle(request, response);
            }

            return "";
        };

        switch (route.requestMethod) {
            case GET -> Spark.get(route.path, r);
            case DELETE -> Spark.delete(route.path, r);
            case OPTIONS -> Spark.options(route.path, r);
            case PATCH -> Spark.patch(route.path, r);
            case POST -> Spark.post(route.path, r);
            case PUT -> Spark.put(route.path, r);
        }
    }
}
