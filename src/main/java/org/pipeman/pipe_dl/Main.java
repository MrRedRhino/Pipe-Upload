package org.pipeman.pipe_dl;

import de.mkammerer.snowflakeid.structure.Structure;
import org.pipeman.pipe_dl.config.Config;
import org.pipeman.pipe_dl.config.ConfigProvider;
import org.pipeman.pipe_dl.download.DownloadRouteRegisterer;
import org.pipeman.pipe_dl.download_page.DownloadPageRouteRegisterer;
import org.pipeman.pipe_dl.upload.UploadRouteRegisterer;
import org.pipeman.pipe_dl.users.login.LoginRouteRegisterer;
import org.pipeman.pipe_dl.users.login.registration.RegistrationRouteRegisterer;
import org.pipeman.pipe_dl.util.routes.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.uid.UID;
import spark.Spark;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static LoginRouteRegisterer loginRouteRegisterer;
    public static UploadRouteRegisterer uploadRouteRegisterer;
    public static DownloadPageRouteRegisterer downloadPageRouteRegisterer;
    public static DownloadRouteRegisterer downloadRouteRegisterer;
    public static RegistrationRouteRegisterer registrationRouteRegisterer;
    public static ConfigProvider configProvider;

    public static UID uid;

    public static void main(String[] args) {
        configProvider = new ConfigProvider(Paths.get("config.properties"));
        configProvider.forceSave();

        uid = new UID(config().uidGeneratorId, new Structure(
                config().uidGeneratorTimestamp,
                config().uidGeneratorGen,
                config().uidGeneratorSequence));
        
        Spark.port(config().serverPort);
        Spark.init();

        new PipeRouteBuilder("/images/:file")
                .handle((request, response) -> {
                    String file = request.params("file");
                    if (file == null) Spark.halt(404);

                    File f = new File(Main.config().imagesDir + "/" + file);
                    if (!f.exists()) Spark.halt(404);
                    if (!f.getParentFile().getName().equals(Main.config().imagesDir)) Spark.halt(404);
                    Files.copy(f.toPath(), response.raw().getOutputStream());
                    return "";
                }).buildAndRegister();

        new PipeRouteBuilder("/")
                .handle(config().index)
                .buildAndRegister();

        loginRouteRegisterer = new LoginRouteRegisterer();
        uploadRouteRegisterer = new UploadRouteRegisterer();
        downloadPageRouteRegisterer = new DownloadPageRouteRegisterer();
        downloadRouteRegisterer = new DownloadRouteRegisterer();
        registrationRouteRegisterer = new RegistrationRouteRegisterer();

        Runtime.getRuntime().addShutdownHook(new Thread(DB::disconnect));
        Runtime.getRuntime().addShutdownHook(new Thread(Spark::stop));
        DB.connect();
    }

    public static Config config() {
        return configProvider.getConfig();
    }
}
