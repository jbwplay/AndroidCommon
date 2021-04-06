package com.androidbase.utils;

import android.os.Environment;

import com.androidbase.utils.javaio.FileUtils;

import java.io.File;
import java.io.IOException;

public class CleanUtils {

    /**
     * /data/data/com.xxx.xxx/cache
     */
    public static void cleanInternalCache() {
        try {
            FileUtils.deleteDir(AndroidUtils.getContext().getCacheDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * /data/data/com.xxx.xxx/files
     */
    public static void cleanInternalFiles() {
        try {
            FileUtils.deleteDir(AndroidUtils.getContext().getFilesDir());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * /data/data/com.xxx.xxx/databases
     *
     * @return {@code true}: 清除成功<br>{@code false}: 清除失败
     */
    public static void cleanInternalDbs() {
        try {
            FileUtils.deleteDir(AndroidUtils.getContext()
                    .getFilesDir()
                    .getParent() + File.separator + "databases");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * /data/data/com.xxx.xxx/shared_prefs
     */
    public static void cleanInternalSP() {
        try {
            FileUtils.deleteDir(AndroidUtils.getContext()
                    .getFilesDir()
                    .getParent() + File.separator + "shared_prefs");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * /storage/emulated/0/android/data/com.xxx.xxx/cache
     */
    public static void cleanExternalCache() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            try {
                FileUtils.deleteDir(AndroidUtils.getContext()
                        .getExternalCacheDir()
                        .getParent() + File.separator + "shared_prefs");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * /storage/emulated/0/android/data/com.xxx.xxx/files
     */
    public static void cleanExternalFiles() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            try {
                FileUtils.deleteDir(AndroidUtils.getContext()
                        .getExternalFilesDir(null)
                        .getParent() + File.separator + "shared_prefs");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

}
