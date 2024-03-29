package org.pipeman.pipe_dl;

import de.mkammerer.snowflakeid.structure.Structure;
import org.jdbi.v3.core.mapper.reflect.ConstructorMapper;
import org.pipeman.pipe_dl.config.Config;
import org.pipeman.pipe_dl.config.ConfigProvider;
import org.pipeman.pipe_dl.files.FileApiRouteRegisterer;
import org.pipeman.pipe_dl.pipe_file.PipeFile;
import org.pipeman.pipe_dl.upload.UploadRouteRegisterer;
import org.pipeman.pipe_dl.upload_page.UploadPage;
import org.pipeman.pipe_dl.upload_page.UploadPageRouteRegisterer;
import org.pipeman.pipe_dl.users.User;
import org.pipeman.pipe_dl.users.login.LoginRouteRegisterer;
import org.pipeman.pipe_dl.users.registration.RegistrationRouteRegisterer;
import org.pipeman.pipe_dl.util.pipe_route.PipeRoute;
import org.pipeman.pipe_dl.util.uid.UID;
import spark.Spark;

import java.nio.file.Paths;

import static org.pipeman.pipe_dl.DB.jdbi;

public class Main {
    public static LoginRouteRegisterer loginRouteRegisterer;
    public static UploadRouteRegisterer uploadRouteRegisterer;
    public static UploadPageRouteRegisterer uploadPageRouteRegisterer;
    public static RegistrationRouteRegisterer registrationRouteRegisterer;
    public static FileApiRouteRegisterer fileApiRouteRegisterer;
    public static ConfigProvider configProvider;

    public static UID uid;

    public static void main(String[] args) {
        configProvider = new ConfigProvider(Paths.get("config.properties"));
        configProvider.forceSave();

        uid = new UID(config().uidGeneratorId, new Structure(
                config().uidGeneratorTimestamp,
                config().uidGeneratorGen,
                config().uidGeneratorSequence));

        Spark.externalStaticFileLocation("static");
        Spark.port(config().serverPort);
        Spark.init();

        PipeRoute.builder("/")
                .handle(config().index)
                .buildAndRegister();

        loginRouteRegisterer = new LoginRouteRegisterer();
        uploadRouteRegisterer = new UploadRouteRegisterer();
        uploadPageRouteRegisterer = new UploadPageRouteRegisterer();
        registrationRouteRegisterer = new RegistrationRouteRegisterer();
        fileApiRouteRegisterer = new FileApiRouteRegisterer();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            DB.INSTANCE.disconnect();
            Spark.stop();
        }));

        jdbi().registerRowMapper(ConstructorMapper.factory(User.class))
                .registerRowMapper(ConstructorMapper.factory(UploadPage.class))
                .registerRowMapper(ConstructorMapper.factory(PipeFile.class))
                .registerRowMapper(ConstructorMapper.factory(PipeFile.DeletedFile.class));
    }

    public static Config config() {
        return configProvider.getConfig();
    }
}
