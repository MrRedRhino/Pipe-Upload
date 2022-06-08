package org.pipeman.pipe_dl;

import de.mkammerer.snowflakeid.structure.Structure;
import org.pipeman.pipe_dl.login.LoginHandler;
import org.pipeman.pipe_dl.upload.UploadManager;
import org.pipeman.pipe_dl.util.routes.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.uid.UID;
import spark.*;

public class Main {
    public static LoginHandler loginHandler;
    public static UploadManager uploadManager;
    public static final UID uid = new UID(1, new Structure(46, 6, 11));

    public static void main(String[] args) {

        Spark.port(Config.SERVER_PORT);
        Spark.init();

        new PipeRouteBuilder("/")
                .handle(Config.HtmlFiles.INDEX)
                .buildAndRegister();

        loginHandler = new LoginHandler();
        uploadManager = new UploadManager();

        DB.connect();

        Spark.after("*", (request, response) -> {

        });
    }
}
