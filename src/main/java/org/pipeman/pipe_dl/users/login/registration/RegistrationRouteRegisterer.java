package org.pipeman.pipe_dl.users.login.registration;

import org.pipeman.pipe_dl.Config;
import org.pipeman.pipe_dl.util.routes.PipeRouteBuilder;

public class RegistrationRouteRegisterer {
    public RegistrationRouteRegisterer() {
        registerRoutes();
    }

    private void registerRoutes() {
        new PipeRouteBuilder("/accounts/register")
                .handle(Config.HtmlFiles.REGISTER)
                .buildAndRegister();
    }
}
