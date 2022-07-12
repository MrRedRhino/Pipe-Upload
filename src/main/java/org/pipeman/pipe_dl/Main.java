package org.pipeman.pipe_dl;

import de.mkammerer.snowflakeid.structure.Structure;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.pipeman.pipe_dl.config.Config;
import org.pipeman.pipe_dl.config.ConfigProvider;
import org.pipeman.pipe_dl.download.DownloadRouteRegisterer;
import org.pipeman.pipe_dl.files.PipeFile;
import org.pipeman.pipe_dl.upload.UploadRouteRegisterer;
import org.pipeman.pipe_dl.upload_page.UploadPage;
import org.pipeman.pipe_dl.upload_page.UploadPageRouteRegisterer;
import org.pipeman.pipe_dl.users.User;
import org.pipeman.pipe_dl.users.login.LoginRouteRegisterer;
import org.pipeman.pipe_dl.users.registration.RegistrationRouteRegisterer;
import org.pipeman.pipe_dl.util.pipe_route.PipeRouteBuilder;
import org.pipeman.pipe_dl.util.uid.UID;
import spark.Spark;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.pipeman.pipe_dl.DB.jdbi;

public class Main {
    public static LoginRouteRegisterer loginRouteRegisterer;
    public static UploadRouteRegisterer uploadRouteRegisterer;
    public static UploadPageRouteRegisterer uploadPageRouteRegisterer;
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
        uploadPageRouteRegisterer = new UploadPageRouteRegisterer();
        downloadRouteRegisterer = new DownloadRouteRegisterer();
        registrationRouteRegisterer = new RegistrationRouteRegisterer();

        DB.connect();
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DB.disconnect();
            Spark.stop();
        }));

        jdbi().registerRowMapper(ConstructorMapper.factory(User.class))
                .registerRowMapper(ConstructorMapper.factory(UploadPage.class))
                .registerRowMapper(ConstructorMapper.factory(PipeFile.class));
    }

    public static Config config() {
        return configProvider.getConfig();
    }
}
