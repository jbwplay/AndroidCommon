package com.androidbase.download.downinterfaceimpl;

import com.androidbase.download.DownloadInfo;
import com.androidbase.download.downinterface.OnDownloadListener;
import com.androidbase.room.ThreadInfo;
import com.androidbase.room.ThreadInfoDataSource;
import com.androidbase.utils.AndroidUtils;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

public class MultiDownloadTask extends DownloadTaskImpl {

    public MultiDownloadTask(DownloadInfo downloadInfo, ThreadInfo threadInfo, OnDownloadListener listener) {
        super(downloadInfo, threadInfo, listener);
    }

    @Override
    protected void insertIntoDB(ThreadInfo info) {
        insertOrUpdateDb(info);
    }

    @Override
    protected int getResponseCode() {
        return HttpURLConnection.HTTP_PARTIAL;
    }

    @Override
    protected void updateDB(ThreadInfo info) {
        insertOrUpdateDb(info);
    }

    @Override
    protected Map<String, String> getHttpHeaders(ThreadInfo info) {
        Map<String, String> headers = new HashMap<String, String>();
        long start = info.getStartoffset() + info.getFinished();
        long end = info.getEndoffset();
        headers.put("Range", "bytes=" + start + "-" + (end == mDownloadInfo.getLength() ? "" : end));
        return headers;
    }

    @Override
    protected RandomAccessFile getFile(File dir, String name, long offset) throws
            IOException {
        File file = new File(dir, name);
        RandomAccessFile raf = new RandomAccessFile(file, "rwd");
        raf.seek(offset);
        return raf;
    }

    protected void insertOrUpdateDb(ThreadInfo info) {
        ThreadInfoDataSource.getInstance(AndroidUtils.getContext())
                .insertOrUpdateDebugInfo(info);
    }
}