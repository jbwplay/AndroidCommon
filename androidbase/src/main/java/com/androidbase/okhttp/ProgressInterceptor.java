package com.androidbase.okhttp;

import java.io.IOException;

import androidx.annotation.NonNull;
import okhttp3.Interceptor;
import okhttp3.Response;

public class ProgressInterceptor implements Interceptor {
    private ResponProgressListener listener;

    public ProgressInterceptor(final ResponProgressListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public Response intercept(final Chain chain) throws IOException {
        Response response = chain.proceed(chain.request());
        return response.newBuilder()
                .body(new ProgressResponseBody(response.body(), listener))
                .build();
    }
}
