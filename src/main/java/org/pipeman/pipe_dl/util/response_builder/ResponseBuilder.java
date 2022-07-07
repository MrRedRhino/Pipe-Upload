package org.pipeman.pipe_dl.util.response_builder;

import org.json.JSONObject;
import spark.Request;
import spark.Response;
import spark.Spark;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ResponseBuilder {
    private final Request request;
    private final List<String> errors = new ArrayList<>();
    private final Map<String, String> responses = new HashMap<>();
    private final Response response;

    public ResponseBuilder(Request request, Response response, Map<String, String> defaultResponses) {
        this.request = request;
        this.response = response;
        responses.putAll(defaultResponses);
    }

    public ResponseBuilder(Request request, Response response) {
        this.request = request;
        this.response = response;
    }

    public Long getHeaderLong(String headerName) {
        String s = request.headers(headerName);
        if (s == null) {
            addMissing(headerName);
            return null;
        }
        try {
            return Long.parseLong(s);
        } catch (NumberFormatException e) {
            addInvalid(headerName);
            return null;
        }
    }

    public String getHeader(String headerName) {
        String s = request.headers(headerName);
        if (s == null) {
            addMissing(headerName);
        } else {
            return s;
        }
        return null;
    }

    public void addMissing(String what) {
        errors.add("missing-" + what);
    }

    public void addInvalid(String what) {
        errors.add("invalid-" + what);
    }

    public String addInvalidAndReturn(String what) {
        addInvalid(what);
        return toString();
    }

    public String addMissingAndReturn(String what) {
        addMissing(what);
        return toString();
    }

    public void setResponse(String key, String value) {
        responses.put(key, value);
    }

    public void haltIfErrors() {
        if (errors.size() == 0) return;
        Spark.halt(400, toString());
    }

    @Override
    public String toString() {
        JSONObject out = new JSONObject();
        if (errors.size() > 0) {
            response.status(400);
            out.put("errors", errors);
            out.put("hint", "Read the api docs at: https://"); // TODO docs
        }
        responses.forEach(out::put);
        return out.toString();
    }
}

