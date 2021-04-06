package com.androidbase.download;

public class DownloadException extends Exception {
    private String errorMessage;
    private int errorCode;

    public DownloadException() {

    }

    public DownloadException(Throwable throwable) {
        super(throwable);
    }

    public DownloadException(String detailMessage) {
        super(detailMessage);
        errorMessage = detailMessage;
    }

    public DownloadException(int errorCode, String detailMessage) {
        this(detailMessage);
        this.errorCode = errorCode;
    }

    public DownloadException(String detailMessage, Throwable throwable) {
        super(detailMessage, throwable);
        errorMessage = detailMessage;
    }

    public DownloadException(int errorCode, String detailMessage, Throwable throwable) {
        this(detailMessage, throwable);
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }
}
