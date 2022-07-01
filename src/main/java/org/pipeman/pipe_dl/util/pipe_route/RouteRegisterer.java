package org.pipeman.pipe_dl.util.pipe_route;

import org.pipeman.pipe_dl.users.login.AccountHelper;
import org.pipeman.pipe_dl.users.login.User;
import spark.Route;
import spark.Spark;

import java.nio.file.Files;


public class RouteRegisterer {

    public static void register(PipeRoute route) {
        Route r = (request, response) -> {
            User user = null;
            if (route.checkAuth) {
                user = AccountHelper.getAccountByRequest(request);
                if (user == null) {
                    response.header("Location", "/accounts/login");
                    return RouteUtil.msg("Unauthorized", response, 302);
                }
            }

            if (route.filePath != null) {
                Files.copy(route.filePath, response.raw().getOutputStream());
                return "";
            }

            if (route.checkAuth) {
                return route.authHandler.handle(user, request, response);
            } else {
                return route.handler.handle(request, response);
            }
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
