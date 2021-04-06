package com.androidbase.okhttp;

public interface RequestProgressListener {
    void onRequestProgress(long bytesWritten, long contentLength, long networkSpeed);
}
