package org.pipeman.pipe_dl.login;

import org.json.JSONObject;
import org.pipeman.pipe_dl.Config;
import org.pipeman.pipe_dl.util.ModifiableFileHelper;
import org.pipeman.pipe_dl.util.routes.*;

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
                .handle(Config.HtmlFiles.LOGIN)
                .buildAndRegister();


        new PipeRouteBuilder("/accounts/logout")
                .handle(Config.HtmlFiles.LOGOUT)
                .handle((request, response) -> {
                    String cookie = request.cookie("login");
                    if (cookie != null) {
                        AccountHelper.logout(cookie);
                        response.removeCookie("login");
                    }
                    return "";
                }).buildAndRegister();


        new PipeRouteBuilder("/accounts/logged-in")
                .checkAuth()
                .handle((request, response) -> {
                    Account account = AccountHelper.getAccountByRequest(request);
                    ModifiableFileHelper.copyFile(Config.HtmlFiles.LOGGED_IN, response,
                            Map.of("!username", account.name));

                    return "";
                }).buildAndRegister();


        new PipeRouteBuilder("/accounts/login")
                .routePrefix(RoutePrefixes.API)
                .handle((request, response) -> {

                    String password = request.headers("password");
                    String email = request.headers("email");

                    if (email == null || password == null) {
                        return RouteUtil.msg("Invalid login", response, 400);
                    } else {
                        String sessionID = AccountHelper.tryToLogin(email, password);
                        if (sessionID != null) {
                            return new JSONObject(Map.of("session-id", sessionID)).toString();
                        } else {
                            return RouteUtil.msg("Invalid login", response, 400);
                        }
                    }
                }).buildAndRegister();
    }
}

