package com.androidbase.download.downinterfaceimpl;

import android.os.Process;

import com.androidbase.download.DownLoadConstants;
import com.androidbase.download.DownloadException;
import com.androidbase.download.DownloadStatus;
import com.androidbase.download.downinterface.ConnectTask;
import com.androidbase.download.downinterface.OnConnectListener;
import com.androidbase.utils.StringUtils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class ConnectTaskImpl implements ConnectTask {
    private final static int MAXREDIRECTTIMES = 3;
    private String mUri;
    private final OnConnectListener mOnConnectListener;

    private volatile int mStatus;

    private volatile long mStartTime;

    public ConnectTaskImpl(String uri, OnConnectListener listener) {
        this.mUri = uri;
        this.mOnConnectListener = listener;
    }

    @Override
    public boolean isConnecting() {
        return mStatus == DownloadStatus.STATUS_CONNECTING;
    }

    @Override
    public boolean isConnected() {
        return mStatus == DownloadStatus.STATUS_CONNECTED;
    }

    @Override
    public boolean isCanceled() {
        return mStatus == DownloadStatus.STATUS_CANCELED;
    }

    @Override
    public boolean isFailed() {
        return mStatus == DownloadStatus.STATUS_FAILED;
    }

    public void cancel() {
        mStatus = DownloadStatus.STATUS_CANCELED;
    }

    @Override
    public void run() {
        Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
        mStatus = DownloadStatus.STATUS_CONNECTING;
        mOnConnectListener.onConnecting();
        try {
            executeConnection();
        } catch (DownloadException e) {
            handleDownloadException(e);
        }
    }

    private void executeConnection() throws DownloadException {
        checkCanceled();
        final URL url;
        HttpURLConnection httpConnection = null;
        mStartTime = System.currentTimeMillis();
        try {
            url = new URL(mUri);
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setConnectTimeout(DownLoadConstants.HTTP.CONNECT_TIME_OUT);
            httpConnection.setReadTimeout(DownLoadConstants.HTTP.READ_TIME_OUT);
            httpConnection.setRequestMethod(DownLoadConstants.HTTP.GET);
            httpConnection.setRequestProperty("Range", "bytes=" + 0 + "-");
            int responseCode = httpConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                parseResponse(httpConnection, false);
            } else if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                parseResponse(httpConnection, true);
            } else {
                if (DownLoadConstants.isRedirection(responseCode)) {
                    int redirectTimes = 0;
                    while (DownLoadConstants.isRedirection(responseCode)) {
                        String location = httpConnection.getHeaderField("Location");
                        if (location == null) {
                            throw new DownloadException(DownloadStatus.STATUS_FAILED, "Location is null");
                        }
                        httpConnection.disconnect();
                        httpConnection = (HttpURLConnection) new URL(location).openConnection();
                        httpConnection.setConnectTimeout(DownLoadConstants.HTTP.CONNECT_TIME_OUT);
                        httpConnection.setReadTimeout(DownLoadConstants.HTTP.READ_TIME_OUT);
                        httpConnection.setRequestMethod(DownLoadConstants.HTTP.GET);
                        httpConnection.setRequestProperty("Range", "bytes=" + 0 + "-");
                        responseCode = httpConnection.getResponseCode();
                        redirectTimes++;
                        if (redirectTimes >= 3) {
                            throw new DownloadException(DownloadStatus.STATUS_FAILED, "Max redirection done");
                        }
                    }
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        parseResponse(httpConnection, false);
                    } else if (responseCode == HttpURLConnection.HTTP_PARTIAL) {
                        parseResponse(httpConnection, true);
                    } else {
                        throw new DownloadException(DownloadStatus.STATUS_FAILED, "UnSupported " + "response code:" + responseCode);
                    }
                } else {
                    throw new DownloadException(DownloadStatus.STATUS_FAILED, "UnSupported " + "response code:" + responseCode);
                }
            }
        } catch (MalformedURLException e) {
            throw new DownloadException(DownloadStatus.STATUS_FAILED, "Bad url.", e);
        } catch (ProtocolException e) {
            throw new DownloadException(DownloadStatus.STATUS_FAILED, "Protocol error", e);
        } catch (IOException e) {
            throw new DownloadException(DownloadStatus.STATUS_FAILED, "IO error", e);
        } finally {
            if (httpConnection != null) {
                httpConnection.disconnect();
            }
        }
    }

    private void parseResponse(HttpURLConnection httpConnection, boolean isAcceptRanges) throws
            DownloadException {
        final long length;
        String contentLength = httpConnection.getHeaderField("Content-Length");
        if (StringUtils.isEmpty(contentLength) || contentLength.equals("0") || contentLength
                .equals("-1")) {
            length = httpConnection.getContentLength();
        } else {
            length = Long.parseLong(contentLength);
        }

        if (isAcceptRanges && length <= 0) {
            throw new DownloadException(DownloadStatus.STATUS_FAILED, "length <= 0");
        }

        checkCanceled();

        mStatus = DownloadStatus.STATUS_CONNECTED;
        final long timeDelta = System.currentTimeMillis() - mStartTime;
        mOnConnectListener.onConnected(timeDelta, length, isAcceptRanges);
    }

    private void checkCanceled() throws DownloadException {
        if (isCanceled()) {
            throw new DownloadException(DownloadStatus.STATUS_CANCELED, "Download " + "paused or cancel!");
        }
    }

    private void handleDownloadException(DownloadException e) {
        switch (e.getErrorCode()) {
            case DownloadStatus.STATUS_FAILED:
                synchronized (mOnConnectListener) {
                    mStatus = DownloadStatus.STATUS_FAILED;
                    mOnConnectListener.onConnectFailed(e);
                }
                break;

            case DownloadStatus.STATUS_CANCELED:
                synchronized (mOnConnectListener) {
                    mStatus = DownloadStatus.STATUS_CANCELED;
                    mOnConnectListener.onConnectCanceled();
                }
                break;

            default:
                throw new IllegalArgumentException("Unknown state");
        }
    }
}
