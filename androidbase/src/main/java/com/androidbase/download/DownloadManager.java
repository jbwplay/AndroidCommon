package com.androidbase.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.androidbase.download.downinterface.DownloadStub;
import com.androidbase.download.downinterfaceimpl.DownloadStubImpl;
import com.androidbase.download.downinterfaceimpl.SimpleDownLoadCallBack;
import com.androidbase.utils.AndroidUtils;
import com.androidbase.utils.DigestUtils;
import com.androidbase.utils.StringUtils;
import com.androidbase.utils.javaio.FileUtils;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {

    public static final String TAG = DownloadManager.class.getSimpleName();

    private static volatile DownloadManager sDownloadManager;

    private Map<String, DownloadStub> mDownloaderMap;

    private DownloadConfiguration mConfig;

    private ExecutorService mExecutorService;

    private Handler mMainHandler;

    private File mFolder;

    public static DownloadManager getInstance() {
        if (sDownloadManager == null) {
            synchronized (DownloadManager.class) {
                if (sDownloadManager == null) {
                    sDownloadManager = new DownloadManager();
                }
            }
        }
        return sDownloadManager;
    }

    private DownloadManager() {
        mDownloaderMap = new LinkedHashMap<String, DownloadStub>();
    }

    public void init(Context context) {
        mConfig = new DownloadConfiguration();
        mExecutorService = Executors.newFixedThreadPool(mConfig.getMaxThreadNum());
        mMainHandler = new Handler(Looper.getMainLooper());
        mFolder = new File(AndroidUtils.getContext()
                .getExternalFilesDir(null), "download");
        FileUtils.createOrExistsFolder(mFolder);
    }

    public synchronized void download(String uri, String filename, SimpleDownLoadCallBack downLoadCallBack) {
        download(uri, filename, false, downLoadCallBack);
    }

    public synchronized void download(String uri, String filename, boolean allowSame, SimpleDownLoadCallBack downLoadCallBack) {
        String key = DigestUtils.md5Hex(uri);
        if (check(key)) {
            if (allowSame) {
                // multi download the same file,previous request maybe cancel asyn
                key = UUID.randomUUID().toString() + "-" + key;
                filename = UUID.randomUUID().toString() + "-" + filename;
                DownloadStub downloader = new DownloadStubImpl(uri, mFolder, filename, downLoadCallBack, mExecutorService, key, mConfig);
                mDownloaderMap.put(key, downloader);
                downloader.start();
            }
        } else {
            DownloadStub downloader = new DownloadStubImpl(uri, mFolder, filename, downLoadCallBack, mExecutorService, key, mConfig);
            mDownloaderMap.put(key, downloader);
            downloader.start();
        }
    }

    public synchronized void pause(String key) {
        for (Map.Entry<String, DownloadStub> itemMap : mDownloaderMap.entrySet()) {
            if (StringUtils.startsWithIgnoreCase(itemMap.getKey(), key)) {
                DownloadStub downloader = itemMap.getValue();
                if (downloader != null && downloader.isRunning()) {
                    downloader.pause();
                }
            }
        }
    }

    public synchronized void cancel(String key) {
        for (Map.Entry<String, DownloadStub> itemMap : mDownloaderMap.entrySet()) {
            if (StringUtils.startsWithIgnoreCase(itemMap.getKey(), key)) {
                DownloadStub downloader = itemMap.getValue();
                if (downloader != null) {
                    downloader.cancel();
                }
            }
        }
    }

    public synchronized void deleteKey(String key) {
        if (check(key)) {
            DownloadStub downloader = mDownloaderMap.get(key);
            if (downloader != null) {
                mDownloaderMap.remove(key);
            }
        }
    }

    public synchronized boolean check(String key) {
        return mDownloaderMap.containsKey(key);
    }

    public Handler getMainHandler() {
        return mMainHandler;
    }
}
