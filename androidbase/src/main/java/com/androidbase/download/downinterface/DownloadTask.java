package com.androidbase.download.downinterface;

public interface DownloadTask extends Runnable {

    boolean isDownloading();

    boolean isPaused();

    boolean isCanceled();

    boolean isComplete();

    boolean isFailed();

    void pause();

    void cancel();
}
