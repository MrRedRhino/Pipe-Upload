package org.pipeman.pipe_dl.users.login.registration;

import org.pipeman.pipe_dl.Main;
import org.pipeman.pipe_dl.util.pipe_route.PipeRouteBuilder;

public class RegistrationRouteRegisterer {
    public RegistrationRouteRegisterer() {
        registerRoutes();
    }

    private void registerRoutes() {
        new PipeRouteBuilder("/accounts/register")
                .handle(Main.config().register)
                .buildAndRegister();
    }
}
