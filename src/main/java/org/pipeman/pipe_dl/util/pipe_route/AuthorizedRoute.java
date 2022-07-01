package org.pipeman.pipe_dl.util.pipe_route;

import org.pipeman.pipe_dl.users.login.User;
import spark.Request;
import spark.Response;

@FunctionalInterface
public interface AuthorizedRoute {
    Object handle(User user, Request request, Response response) throws Exception;
}
