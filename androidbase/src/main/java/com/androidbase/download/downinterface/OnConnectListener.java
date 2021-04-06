package com.androidbase.download.downinterface;

import com.androidbase.download.DownloadException;

public interface OnConnectListener {

    void onConnecting();

    void onConnected(long time, long length, boolean isAcceptRanges);

    void onConnectCanceled();

    void onConnectFailed(DownloadException de);

}