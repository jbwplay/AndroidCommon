package com.androidbase.okhttp;

import okhttp3.Response;

public class OkHttpException extends Exception {
    private final int code;
    private final String message;
    private final transient Response response;

    public OkHttpException(Response response) {
        super("HTTP " + response.code() + " " + response.message());
        this.code = response.code();
        this.message = response.message();
        this.response = response;
    }

    /**
     * HTTP status code.
     */
    public int code() {
        return code;
    }

    /**
     * HTTP status message.
     */
    public String message() {
        return message;
    }

    /**
     * The full HTTP response. This may be null if the exception was serialized.
     */
    public Response response() {
        return response;
    }
}
