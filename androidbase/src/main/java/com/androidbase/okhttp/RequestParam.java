package com.androidbase.okhttp;

import com.androidbase.utils.MiscUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParam {
    public final Map<String, String> headers;
    public final Map<String, String> queries;
    public final Map<String, String> forms;
    public final Map<String, Object> jsons;
    public final List<MultiPartFile> parts;

    public RequestParam() {
        headers = new HashMap<String, String>();
        queries = new HashMap<String, String>();
        forms = new HashMap<String, String>();
        parts = new ArrayList<MultiPartFile>();
        jsons = new HashMap<String, Object>();
    }

    public RequestParam header(String key, String value) {
        MiscUtils.notEmpty(key, "key must not be null or empty.");
        if (value != null) {
            this.headers.put(key, value);
        }
        return this;
    }

    public RequestParam headers(Map<String, String> headers) {
        if (headers != null) {
            for (final Map.Entry<String, String> entry : headers.entrySet()) {
                header(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public RequestParam query(String key, String value) {
        MiscUtils.notEmpty(key, "key must not be null or empty.");
        if (value != null) {
            this.queries.put(key, value);
        }
        return this;
    }

    public RequestParam queries(Map<String, String> queries) {
        if (queries != null) {
            for (final Map.Entry<String, String> entry : queries.entrySet()) {
                query(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public RequestParam form(String key, String value) {
        MiscUtils.notEmpty(key, "key must not be null or empty.");
        if (value != null) {
            this.forms.put(key, value);
        }
        return this;
    }

    public RequestParam forms(Map<String, String> forms) {
        if (forms != null) {
            for (final Map.Entry<String, String> entry : forms.entrySet()) {
                form(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public RequestParam file(String key, File file) {
        return file(key, file, OkHttpRequest.APPLICATION_OCTET_STREAM, file.getName());
    }

    public RequestParam file(String key, File file, String contentType) {
        return file(key, file, contentType, file.getName());
    }

    public RequestParam file(String key, File file, String contentType, String fileName) {
        MiscUtils.notEmpty(key, "key must not be null or empty.");
        MiscUtils.notNull(file, "file must not be null.");
        MultiPartFile part = new MultiPartFile(key, file, contentType, fileName);
        return part(part);
    }

    public RequestParam part(final MultiPartFile part) {
        MiscUtils.notNull(part, "part must not be null.");
        this.parts.add(part);
        return this;
    }

    public RequestParam json(String key, Object value) {
        MiscUtils.notEmpty(key, "key must not be null or empty.");
        if (value != null) {
            this.jsons.put(key, value);
        }
        return this;
    }

    public RequestParam jsons(Map<String, Object> jsons) {
        if (jsons != null) {
            for (final Map.Entry<String, Object> entry : jsons.entrySet()) {
                json(entry.getKey(), entry.getValue());
            }
        }
        return this;
    }

    public Map<String, String> forms() {
        return forms;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public List<MultiPartFile> parts() {
        return parts;
    }

    public Map<String, String> queries() {
        return queries;
    }

    public Map<String, Object> jsons() {
        return jsons;
    }
}
