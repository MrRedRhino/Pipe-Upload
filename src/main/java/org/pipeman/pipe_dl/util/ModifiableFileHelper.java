package org.pipeman.pipe_dl.util;

import spark.Response;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class ModifiableFileHelper {
    public static String copyFile(Path path, Response response, Map<String, String> replacements) {
        try {
            String content = Files.readString(path);
            for (Map.Entry<String, String> e : replacements.entrySet()) {
                content = content.replace(e.getKey(), e.getValue());
            }
            response.raw().getOutputStream().write(content.getBytes());

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        return "";
    }
}
