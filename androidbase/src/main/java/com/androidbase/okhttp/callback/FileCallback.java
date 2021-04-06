package com.androidbase.okhttp.callback;

import java.io.File;

public abstract class FileCallback implements HttpCallback<File> {

    @Override
    public void onRequestProgress(long bytesWritten, long contentLength, long networkSpeed) {

    }

    @Override
    public void onResponseProgress(long bytesRead, long contentLength, boolean done) {

    }
}
