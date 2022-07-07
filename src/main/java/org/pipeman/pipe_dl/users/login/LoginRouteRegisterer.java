package org.pipeman.pipe_dl.users.login;

import org.json.JSONObject;
import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.util.ModifiableFileHelper;
import org.pipeman.pipe_dl.util.pipe_route.*;

import java.util.Map;

public class LoginRouteRegisterer {

    public LoginRouteRegisterer() {
        registerRoutes();
    }

    private void registerRoutes() {

        new PipeRouteBuilder("/").handle((request, response) -> {
            response.redirect("/accounts/login");
            return "";
        }).buildAndRegister();


        new PipeRouteBuilder("/accounts/login")
                .handle(Main.config().login)
                .buildAndRegister();


        new PipeRouteBuilder("/accounts/logout")
                .handle(Main.config().logout)
                .handle((request, response) -> {
                    String cookie = request.cookie("login");
                    if (cookie != null) {
                        LoginHelper.logout(cookie);
                        response.removeCookie("login");
                    }
                    return "";
                }).buildAndRegister();


        new PipeRouteBuilder("/accounts/logged-in")
                .handle((user, request, response) -> ModifiableFileHelper.copyFile(Main.config().loggedIn, response, Map.of("!username", user.name())))
                .buildAndRegister();


        new PipeRouteBuilder(RoutePrefixes.API, "/accounts/login")
                .handle((request, response) -> {

                    String password = request.headers("password");
                    String email = request.headers("email");

                    if (email == null || password == null) {
                        return RouteUtil.msg("Invalid login", response, 400);
                    } else {
                        String sessionID = LoginHelper.tryToLogin(email, password);
                        if (sessionID != null) {
                            return new JSONObject(Map.of("session-id", sessionID)).toString();
                        } else {
                            return RouteUtil.msg("Invalid login", response, 400);
                        }
                    }
                }).buildAndRegister();
    }
}

