package org.pipeman.pipe_dl.captcha;

import org.json.JSONObject;
import org.pipeman.pipe_dl.Main;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CaptchaHelper {
    public static boolean isCaptchaInvalid(String captchaResponse) {
        if (captchaResponse == null || captchaResponse.isBlank()) return true;
        try {
            String body = "response=" + captchaResponse + "&secret=" + Main.config().hCaptchaKey;

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(new URI("https://hcaptcha.com/siteverify"))
                    .POST(HttpRequest.BodyPublishers.ofString(body))
                    .headers("Content-Type", "application/x-www-form-urlencoded")
                    .build();

            String response = HttpClient.newHttpClient().send(request, HttpResponse.BodyHandlers.ofString()).body();
            return !new JSONObject(response).getBoolean("success");

        } catch (Exception ignored) {
        }

        return true;
    }
}
