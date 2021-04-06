package com.androidbase.download;

import com.androidbase.download.downinterface.DownLoadCallBack;

public class DownloadStatus {
    public static final int STATUS_STARTED = 0x101;
    public static final int STATUS_CONNECTING = 0x102;
    public static final int STATUS_CONNECTED = 0x103;
    public static final int STATUS_PROGRESS = 0x104;
    public static final int STATUS_PAUSED = 0x105;
    public static final int STATUS_CANCELED = 0x106;
    public static final int STATUS_COMPLETED = 0x107;
    public static final int STATUS_FAILED = 0x108;

    private int status;
    private long time;
    private long length;
    private long finished;
    private int percent;
    private boolean acceptRanges;
    private DownloadException exception;

    private DownLoadCallBack callBack;

    public DownloadStatus() {

    }

    public DownloadStatus(DownloadStatus downloadStatus) {
        status = downloadStatus.getStatus();
        time = downloadStatus.getTime();
        length = downloadStatus.getLength();
        finished = downloadStatus.getFinished();
        percent = downloadStatus.getPercent();
        acceptRanges = downloadStatus.isAcceptRanges();
        exception = (DownloadException) downloadStatus.getException();
        callBack = downloadStatus.getCallBack();
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public int getPercent() {
        return percent;
    }

    public void setPercent(int percent) {
        this.percent = percent;
    }

    public boolean isAcceptRanges() {
        return acceptRanges;
    }

    public void setAcceptRanges(boolean acceptRanges) {
        this.acceptRanges = acceptRanges;
    }

    public Exception getException() {
        return exception;
    }

    public void setException(DownloadException exception) {
        this.exception = exception;
    }

    public DownLoadCallBack getCallBack() {
        return callBack;
    }

    public void setCallBack(DownLoadCallBack callBack) {
        this.callBack = callBack;
    }
}
