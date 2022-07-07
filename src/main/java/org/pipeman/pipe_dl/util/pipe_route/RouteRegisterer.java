package org.pipeman.pipe_dl.util.pipe_route;

import org.pipeman.pipe_dl.users.login.AccountHelper;
import org.pipeman.pipe_dl.users.login.User;
import org.pipeman.pipe_dl.util.response_builder.ResponseBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import spark.Route;
import spark.Spark;

import java.nio.file.Files;


public class RouteRegisterer {
    private static final Logger LOGGER = LoggerFactory.getLogger(RouteRegisterer.class);

    public static void register(PipeRoute route) {
        LOGGER.info("Registering " + route.requestMethod + "-route: " + route.path + (route.checkAuth ? " with authorization" : ""));

        Route r = (request, response) -> {
            User user = null;
            if (route.checkAuth) {
                user = AccountHelper.getAccountByRequest(request);
                if (user == null) {
                    if (route.forUser) {
                        response.header("Location", "/accounts/login");
                        response.status(302);
                        return "<h1>Unauthorized</h1>";
                    } else {
                        return new ResponseBuilder(request, response).addInvalidAndReturn("authorization");
                    }
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
