package com.androidbase.download.downinterface;

import com.androidbase.download.DownloadException;

public interface OnDownloadListener {

    void onDownloadProgress(long finished, long length);

    void onDownloadPaused();

    void onDownloadCanceled();

    void onDownloadCompleted();

    void onDownloadFailed(DownloadException de);

}