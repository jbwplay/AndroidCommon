package com.androidbase.okhttp.callback;

public abstract class StringCallback implements HttpCallback<String> {

    @Override
    public void onRequestProgress(long bytesWritten, long contentLength, long networkSpeed) {

    }

    @Override
    public void onResponseProgress(long bytesRead, long contentLength, boolean done) {

    }
}
