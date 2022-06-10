package org.pipeman.pipe_dl;

import de.mkammerer.snowflakeid.structure.Structure;
import org.pipeman.pipe_dl.download.DownloadRouteRegisterer;
import org.pipeman.pipe_dl.login.LoginRouteRegisterer;
import org.pipeman.pipe_dl.upload.UploadRouteRegisterer;
import org.pipeman.pipe_dl.upload_page.UploadPageRouteRegisterer;
import org.pipeman.pipe_dl.util.routes.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.uid.UID;
import spark.*;

public class Main {
    public static LoginRouteRegisterer loginRouteRegisterer;
    public static UploadRouteRegisterer uploadRouteRegisterer;
    public static UploadPageRouteRegisterer uploadPageRouteRegisterer;
    public static DownloadRouteRegisterer downloadRouteRegisterer;

    public static final UID uid = new UID(1, new Structure(46, 6, 11));

    public static void main(String[] args) {

        Spark.port(Config.SERVER_PORT);
        Spark.init();

        new PipeRouteBuilder("/")
                .handle(Config.HtmlFiles.INDEX)
                .buildAndRegister();

        loginRouteRegisterer = new LoginRouteRegisterer();
        uploadRouteRegisterer = new UploadRouteRegisterer();
        uploadPageRouteRegisterer = new UploadPageRouteRegisterer();
        downloadRouteRegisterer = new DownloadRouteRegisterer();

        DB.connect();
    }
}
