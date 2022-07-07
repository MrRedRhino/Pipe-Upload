package org.pipeman.pipe_dl.users.registration;

import org.json.JSONObject;
import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.captcha.CaptchaHelper;
import org.pipeman.pipe_dl.users.User;
import org.pipeman.pipe_dl.users.login.LoginHelper;
import org.pipeman.pipe_dl.util.pipe_route.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.pipe_route.RequestMethod;
import org.pipeman.pipe_dl.util.pipe_route.RoutePrefixes;
import org.pipeman.pipe_dl.util.pipe_route.RouteUtil;
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
        String email = request.headers("email");
        String password = request.headers("password");
        String username = request.headers("username");
        String captchaKey = request.headers("captcha-key");

        if (email == null || password == null || username == null) {
            return RouteUtil.msg("Required headers are missing: 'email', 'password', 'username'", response, 400);
        }

        if (User.getByEmail(email).isPresent()) {
            return RouteUtil.msg("An account with that email already exists", response, 400);
        }

        if (!CaptchaHelper.captchaValid(captchaKey)) {
            return RouteUtil.msg("Captcha invalid", response, 400);
        }

        User user = new User(username, password, email);
        user.password(PasswordSpicer.hashAndSpice(password, user));
        user.save();

        return new JSONObject(Map.of("session-id", LoginHelper.genSessionId(user))).toString();
    }
}
