package org.pipeman.pipe_dl.util.pipe_route;

import spark.Response;

public class RouteUtil {
    public static String msg(String message, Response response, int status) {
        response.status(status);
        return msg(message);
    }

    public static String msg(String message) {
        StringBuilder out = new StringBuilder();

        for (char c : message.toCharArray()) {
            if (c == '"') {
                out.append("\\\"");
            } else {
                out.append(c);
            }
        }
        return "{\"message\": \"" + out + "\"}";
//        return "{\"message\": \"" + message.replaceAll("\"", "\\\"") + "\"}"; // {"message": "   "}
    }
}
