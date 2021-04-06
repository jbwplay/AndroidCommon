package com.androidbase.okhttp.callback;

import com.androidbase.okhttp.RequestProgressListener;
import com.androidbase.okhttp.ResponProgressListener;

public interface HttpCallback<T> extends RequestProgressListener, ResponProgressListener {

    void handleResponse(T response);

    boolean handleException(Throwable throwable);
}
