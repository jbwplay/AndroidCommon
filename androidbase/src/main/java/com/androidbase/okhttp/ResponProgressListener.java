package com.androidbase.okhttp;

public interface ResponProgressListener {

    void onResponseProgress(long bytesRead, long contentLength, boolean done);
}
