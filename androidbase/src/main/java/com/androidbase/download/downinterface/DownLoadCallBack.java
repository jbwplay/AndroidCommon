package com.androidbase.download.downinterface;

import com.androidbase.download.DownloadException;

import java.io.File;

public interface DownLoadCallBack {

    void onConnected(long total, boolean isRangeSupport);

    void onProgress(long finished, long total, int progress);

    void onCompleted(File downloadfile);

    void onFailed(DownloadException e);

    void onPaused(File downloadfile);
}
