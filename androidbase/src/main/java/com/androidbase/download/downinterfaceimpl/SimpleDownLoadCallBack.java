package com.androidbase.download.downinterfaceimpl;

import com.androidbase.download.downinterface.DownLoadCallBack;

import java.io.File;

public abstract class SimpleDownLoadCallBack implements DownLoadCallBack {

    @Override
    public void onConnected(long total, boolean isRangeSupport) {
    }

    @Override
    public void onPaused(File downloadfile) {
    }
}
