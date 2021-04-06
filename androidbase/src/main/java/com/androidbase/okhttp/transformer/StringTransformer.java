package com.androidbase.okhttp.transformer;

import java.io.IOException;

import okhttp3.Response;

public class StringTransformer implements HttpTransformer<String> {
    @Override
    public String transform(final Response response) throws IOException {
        return response.body().string();
    }
}
