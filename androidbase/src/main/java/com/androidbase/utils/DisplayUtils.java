package com.androidbase.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.Build;
import android.text.TextPaint;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.util.ArrayList;
import java.util.List;

public class DisplayUtils {

    /**
     * 获取当前的屏幕尺寸
     *
     * @return
     */
    public static int getWidth() {
        Display display = ((WindowManager) AndroidUtils.getContext()
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.x;
    }

    /**
     * 获取当前的屏幕尺寸
     *
     * @return
     */
    public static int getHeight() {
        Display display = ((WindowManager) AndroidUtils.getContext()
                .getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        return size.y;
    }

    /**
     * 获取原始的屏幕尺寸
     *
     * @param context {@link Context}
     * @return 屏幕尺寸
     */
    @SuppressLint("ObsoleteSdkInt")
    public static int[] getRawScreenSize(Context context) {
        int[] size = new int[2];
        WindowManager w = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display d = w.getDefaultDisplay();
        DisplayMetrics metrics = new DisplayMetrics();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // Android 4.2时,使用getRealMetrics（getRealSize）来获取屏幕高度
            d.getRealMetrics(metrics);
        } else {
            // Andoird 4.0时,引入了虚拟导航键,使用getMetrics之类的方式,获取的高度是
            // 去除了导航栏的高度的,在4.0和4.2之间还没有的getRealMetrics。
            d.getMetrics(metrics);
        }
        size[0] = metrics.widthPixels;
        size[1] = metrics.heightPixels;
        return size;
    }

    /**
     * Default status dp = 24 or 25 mhdpi = dp * 1 hdpi = dp * 1.5 xhdpi = dp * 2 xxhdpi =
     * dp * 3 eg : 1920x1080, xxhdpi, => status/all = 25/640(dp) = 75/1080(px)
     * <p>
     * don't forget toolbar's dp = 48
     *
     * @return px
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        Resources res = context.getResources();
        int resourceId = res.getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = res.getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    public static int getNavigationBarHeight() {
        int resourceId = AndroidUtils.getResources()
                .getIdentifier("navigation_bar_height", "dimen", "android");
        return resourceId > 0 ? AndroidUtils.getResources()
                .getDimensionPixelSize(resourceId) : 0;
    }

    public static void showSoftInput(final Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void showSoftInput(final View view) {
        view.setFocusable(true);
        view.setFocusableInTouchMode(true);
        view.requestFocus();
        InputMethodManager imm = (InputMethodManager) AndroidUtils.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.showSoftInput(view, InputMethodManager.SHOW_FORCED);
    }

    public static void hideSoftInput(final Activity activity) {
        View view = activity.getCurrentFocus();
        if (view == null) {
            view = new View(activity);
        }
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void hideSoftInput(final View view) {
        InputMethodManager imm = (InputMethodManager) AndroidUtils.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static void toggleSoftInput() {
        InputMethodManager imm = (InputMethodManager) AndroidUtils.getContext()
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm == null) {
            return;
        }
        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
    }

    public static float dp2px(float dp) {
        return dp * AndroidUtils.getDisplayMetrics().density;
    }

    public static int dp2px(int dp) {
        return (int) (dp * AndroidUtils.getDisplayMetrics().density + 0.5f);
    }

    public static float px2dp(float px) {
        return px / AndroidUtils.getDisplayMetrics().density;
    }

    public static int px2dp(int px) {
        return (int) (px / AndroidUtils.getDisplayMetrics().density + 0.5f);
    }

    public static float sp2px(float sp) {
        return sp * AndroidUtils.getDisplayMetrics().scaledDensity;
    }

    public static int sp2px(int sp) {
        return (int) (sp * AndroidUtils.getDisplayMetrics().scaledDensity + 0.5f);
    }

    public static float px2sp(float px) {
        return px / AndroidUtils.getDisplayMetrics().scaledDensity;
    }

    public static int px2sp(int px) {
        return (int) (px / AndroidUtils.getDisplayMetrics().scaledDensity + 0.5f);
    }

    public static void measureView(View view) {
        if (view == null) {
            return;
        }
        ViewGroup.LayoutParams layoutParams = view.getLayoutParams();
        if (layoutParams == null) {
            layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        }
        int nWidth = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int nHeight = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(nWidth, nHeight);
    }

    public static void measureView(View parent, View view) {
        if (parent == null || view == null) {
            return;
        }
        int nWidth = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredWidth(), View.MeasureSpec.AT_MOST);
        int nHeight = View.MeasureSpec.makeMeasureSpec(parent.getMeasuredHeight(), View.MeasureSpec.AT_MOST);
        view.measure(nWidth, nHeight);
    }

    private static int getStringIndexByMaxPix(String str, int maxPix, TextPaint paint) {
        if (StringUtils.isEmpty(str)) {
            return 0;
        }
        int currentIndex = 0;
        for (int i = 0; i < str.length(); i++) {
            String temp = str.substring(0, i + 1);
            float valueLength = paint.measureText(temp);
            if (valueLength > maxPix) {
                currentIndex = i - 1;
                break;
            } else if (valueLength == maxPix) {
                currentIndex = i;
                break;
            }
        }

        if (currentIndex == 0) {
            currentIndex = str.length() - 1;
        }
        return currentIndex;
    }

    public static List<String> getDrawRowStr(String text, int maxWPix, TextPaint paint) {
        String[] texts = null;
        if (text.contains("\n")) {
            texts = text.split("\n");
        } else {
            texts = new String[1];
            texts[0] = text;
        }

        List<String> mStrList = new ArrayList<String>();
        for (int i = 0; i < texts.length; i++) {
            String textLine = texts[i];
            while (true) {
                int endIndex = getStringIndexByMaxPix(textLine, maxWPix, paint);
                if (endIndex == 0 || endIndex == textLine.length() - 1) {
                    mStrList.add(textLine);
                } else {
                    mStrList.add(textLine.substring(0, endIndex + 1));
                }

                if (textLine.length() > endIndex + 1) {
                    textLine = textLine.substring(endIndex + 1);
                } else {
                    break;
                }
            }
        }
        return mStrList;
    }

    public static void fullScreen(Activity activity, boolean fullScreen) {
        if (fullScreen) {
            activity.getWindow()
                    .clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        } else {
            activity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            activity.getWindow()
                    .setFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN, WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

}
