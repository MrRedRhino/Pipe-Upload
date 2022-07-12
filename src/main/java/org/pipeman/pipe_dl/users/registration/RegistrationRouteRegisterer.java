package org.pipeman.pipe_dl.users.registration;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.captcha.CaptchaHelper;
import org.pipeman.pipe_dl.users.User;
import org.pipeman.pipe_dl.users.login.LoginHelper;
import org.pipeman.pipe_dl.util.pipe_route.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.pipe_route.RequestMethod;
import org.pipeman.pipe_dl.util.pipe_route.RoutePrefixes;
import org.pipeman.pipe_dl.util.response_builder.ResponseBuilder;
import org.pipeman.pipe_dl.util.security.PasswordSpicer;
import spark.Request;
import spark.Response;

import java.util.Map;

public class RegistrationRouteRegisterer {
    public RegistrationRouteRegisterer() {
        registerRoutes();
    }

    private void registerRoutes() {
        new PipeRouteBuilder("/accounts/register")
                .handle(Main.config().register)
                .buildAndRegister();

        new PipeRouteBuilder(RoutePrefixes.API, "/accounts/register")
                .acceptMethod(RequestMethod.POST)
                .handle(this::handleRegistration)
                .buildAndRegister();
    }


    private String handleRegistration(Request request, Response response) {
        ResponseBuilder rb = new ResponseBuilder(request, response, Map.of("session-id", ""));
        String captchaKey = rb.getHeader("captcha-key");
        String email = rb.getHeader("email");
        String password = rb.getHeader("password");
        String username = rb.getHeader("username");

        rb.haltIfErrors();

        if (email.isBlank() || User.getByEmail(email).isPresent()) return rb.addInvalidAndReturn("email");
        if (password.isBlank()) return rb.addInvalidAndReturn("password");
        if (username.isBlank()) return rb.addInvalidAndReturn("username");

        if (CaptchaHelper.isCaptchaInvalid(captchaKey)) return rb.addInvalidAndReturn("captcha-key");

        User user = new User(username, password, email);
        user.password(PasswordSpicer.hashAndSpice(password, user)).save();

        rb.addResponse("session-id", LoginHelper.forceLogin(user));
        return rb.toString();
    }
}
