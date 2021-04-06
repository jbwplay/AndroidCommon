package com.androidbase.okhttp.transformer;

import java.io.IOException;

import okhttp3.Response;

public interface HttpTransformer<T> {
    T transform(Response response) throws IOException;
}
