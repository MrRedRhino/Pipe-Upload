package org.pipeman.pipe_dl.users.login;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.captcha.CaptchaHelper;
import org.pipeman.pipe_dl.util.ModifiableFileHelper;
import org.pipeman.pipe_dl.util.pipe_route.*;
import org.pipeman.pipe_dl.util.response_builder.ResponseBuilder;

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
                    ResponseBuilder rb = new ResponseBuilder(request, response, Map.of("session-id", ""));

                    String captchaKey = rb.getHeader("captcha-key");
                    String email = rb.getHeader("email");
                    String password = rb.getHeader("password");

                    rb.haltIfErrors();
                    if (CaptchaHelper.isCaptchaValid(captchaKey)) return rb.addInvalidAndReturn("captcha-key");

                    String sessionID = LoginHelper.tryToLogin(email, password);
                    if (sessionID != null) rb.setResponse("session-id", sessionID);
                    return rb.toString();
                }).buildAndRegister();
    }
}

