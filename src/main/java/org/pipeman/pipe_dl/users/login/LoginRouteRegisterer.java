package org.pipeman.pipe_dl.users.login;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.captcha.CaptchaHelper;
import org.pipeman.pipe_dl.upload_page.UploadPage;
import org.pipeman.pipe_dl.users.User;
import org.pipeman.pipe_dl.util.misc.ModifiableFileHelper;
import org.pipeman.pipe_dl.util.pipe_route.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.pipe_route.RoutePrefixes;
import org.pipeman.pipe_dl.util.response_builder.ResponseBuilder;
import spark.Request;
import spark.Response;

import java.util.HashMap;
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
                .handle((user, request, response) -> {
                    Map<String, String> toReplace = new HashMap<>();
                    toReplace.put("!username", user.name());

                    UploadPage uploadPage = user.getUploadPage();
                    if (uploadPage != null) {
                        toReplace.put("!hide-create", "hidden");
                        toReplace.put("!hide-goto", "");
                        toReplace.put("!up-id", String.valueOf(uploadPage.id()));
                    } else {
                        toReplace.put("!hide-create", "");
                        toReplace.put("!hide-goto", "hidden");
                        toReplace.put("!up-id", "0");
                    }

                    return ModifiableFileHelper.copyFile(Main.config().loggedIn, response, toReplace);
                })
                .buildAndRegister();


        new PipeRouteBuilder(RoutePrefixes.API, "/accounts/login")
                .handle(this::handleLogin).buildAndRegister();
    }

    private String handleLogin(Request request, Response response) {
        ResponseBuilder rb = new ResponseBuilder(request, response, Map.of(
                "session-id", "",
                "captcha-required", true
        ));

        String email = rb.getHeader("email");
        String password = rb.getHeader("password");
        String captchaKey = rb.getHeader("captcha-key");

        rb.haltIfErrors();

        User user = User.getByEmail(email).orElse(null);
        if (user != null && user.authCorrect(email, password)) {
            if (CaptchaHelper.isCaptchaInvalid(captchaKey)) return rb.addInvalidAndReturn("captcha-key");
            return rb.addResponse("session-id", LoginHelper.forceLogin(user)).toString();
        }
        rb.addInvalid("email");
        rb.addInvalid("password");

        return rb.toString();
    }
}

