package com.androidbase.utils;

import android.Manifest;
import android.content.Context;
import android.os.Vibrator;

import androidx.annotation.RequiresPermission;

/**
 * detail: 震动相关工具类
 *
 * @author Ttt
 * <pre>
 *     所需权限
 *     <uses-permission android:name="android.permission.VIBRATE" />
 * </pre>
 */
public final class VibrationUtils {

    /**
     * 震动
     *
     * @param milliseconds 震动时长 ( 毫秒 )
     * @return {@code true} success, {@code false} fail
     */
    @RequiresPermission(Manifest.permission.VIBRATE)
    public static boolean vibrate(final long milliseconds) {
        try {
            Vibrator vibrator = (Vibrator) AndroidUtils.getContext()
                    .getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(milliseconds);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * pattern 模式震动
     *
     * @param pattern new long[]{400, 800, 1200, 1600}, 就是指定在 400ms、800ms、1200ms、1600ms
     *                这些时间点交替启动、关闭手机震动器
     * @param repeat  指定 pattern 数组的索引, 指定 pattern 数组中从 repeat 索引开始的震动进行循环, -1 表示只震动一次, 非
     *                -1 表示从 pattern 数组指定下标开始重复震动
     * @return {@code true} success, {@code false} fail
     */
    @RequiresPermission(Manifest.permission.VIBRATE)
    public static boolean vibrate(final long[] pattern, final int repeat) {
        if (pattern == null) {
            return false;
        }
        try {
            Vibrator vibrator = (Vibrator) AndroidUtils.getContext()
                    .getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(pattern, repeat);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 取消震动
     *
     * @return {@code true} success, {@code false} fail
     */
    @RequiresPermission(Manifest.permission.VIBRATE)
    public static boolean cancel() {
        try {
            Vibrator vibrator = (Vibrator) AndroidUtils.getContext()
                    .getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.cancel();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}