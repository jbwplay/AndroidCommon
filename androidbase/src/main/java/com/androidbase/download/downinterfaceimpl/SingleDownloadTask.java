package com.androidbase.download.downinterfaceimpl;


import com.androidbase.download.DownloadInfo;
import com.androidbase.download.downinterface.OnDownloadListener;
import com.androidbase.room.ThreadInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Map;

public class SingleDownloadTask extends DownloadTaskImpl {

    public SingleDownloadTask(DownloadInfo mDownloadInfo, ThreadInfo mThreadInfo, OnDownloadListener mOnDownloadListener) {
        super(mDownloadInfo, mThreadInfo, mOnDownloadListener);
    }

    @Override
    protected void insertIntoDB(ThreadInfo info) {
        // don't support range, only restart download
    }

    @Override
    protected int getResponseCode() {
        return HttpURLConnection.HTTP_OK;
    }

    @Override
    protected void updateDB(ThreadInfo info) {
        // don't support range, only restart download
    }

    @Override
    protected Map<String, String> getHttpHeaders(ThreadInfo info) {
        // simply return null
        return null;
    }

    @Override
    protected RandomAccessFile getFile(File dir, String name, long offset) throws
            IOException {
        File file = new File(dir, name);
        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
        raf.seek(0);
        return raf;
    }
}

