package com.androidbase.download.downinterface;

public interface ConnectTask extends Runnable {

    boolean isConnecting();

    boolean isConnected();

    boolean isCanceled();

    boolean isFailed();

    void cancel();
}
