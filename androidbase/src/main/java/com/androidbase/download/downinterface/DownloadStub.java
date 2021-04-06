package com.androidbase.download.downinterface;

public interface DownloadStub {

    void start();

    void pause();

    void cancel();

    boolean isRunning();
}
