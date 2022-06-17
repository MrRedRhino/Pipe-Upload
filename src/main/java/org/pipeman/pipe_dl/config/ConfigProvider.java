package org.pipeman.pipe_dl.config;

import java.nio.file.Path;

public class ConfigProvider {
    private Config config;
    private final Path source;

    public ConfigProvider(Path path) {
        this.source = path;
        config = Config.fromFile(path);
    }

    public void forceSave() {
        config.store(source);
    }

    public void reload() {
        this.config = Config.fromFile(source);
    }

    public Config getConfig() {
        return config;
    }
}
