package com.androidbase.utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;

import java.io.File;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import com.androidbase.download.DownloadManager;
import com.androidbase.okhttp.OkHttpUtils;

import static android.app.ActivityManager.RunningAppProcessInfo;
import static android.content.pm.PackageManager.GET_SERVICES;

public class AndroidUtils {

    private static final String TAG = AndroidUtils.class.getSimpleName();

    private static Context mContext;

    // must be first initialize on Application onCreate
    public static void init(@NonNull Context context) {
        //工具类设置application
        mContext = context;
        //下载设置application
        DownloadManager.getInstance().init(context);
        //网络访问设置application
        OkHttpUtils.getInstance().init(context);
    }

    public static Context getContext() {
        if (AndroidUtils.mContext == null) {
            throw new NullPointerException("Call AndroidUtils.initialize(context) within your Application onCreate() method.");
        }

        return AndroidUtils.mContext.getApplicationContext();
    }

    public static Resources getResources() {
        return AndroidUtils.getContext().getResources();
    }

    public static DisplayMetrics getDisplayMetrics() {
        return AndroidUtils.getResources().getDisplayMetrics();
    }
    public static int id(Context context, String resourceName, TYPE type) {
        Resources resources = context.getResources();
        return resources.getIdentifier(resourceName, type.getString(), context.getPackageName());
    }

    public enum TYPE {
        ATTR("attr"),
        ARRAY("array"),
        ANIM("anim"),
        BOOL("bool"),
        COLOR("color"),
        DIMEN("dimen"),
        DRAWABLE("drawable"),
        ID("id"),
        INTEGER("integer"),
        LAYOUT("layout"),
        MENU("menu"),
        MIPMAP("mipmap"),
        RAW("raw"),
        STRING("string"),
        STYLE("style"),
        STYLEABLE("styleable");

        private String string;

        TYPE(String string) {
            this.string = string;
        }

        public String getString() {
            return string;
        }
    }

    public static int getAppVersionCode() {
        try {
            if (mContext != null) {
                PackageManager pm = mContext.getPackageManager();
                if (pm != null) {
                    PackageInfo pi;
                    pi = pm.getPackageInfo(mContext.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionCode;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getAppVersionName() {
        try {
            if (mContext != null) {
                PackageManager pm = mContext.getPackageManager();
                if (pm != null) {
                    PackageInfo pi;
                    pi = pm.getPackageInfo(mContext.getPackageName(), 0);
                    if (pi != null) {
                        return pi.versionName;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void installApk(File apkFile) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        Uri uri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            uri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + "." + "fileprovider", apkFile);
            intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        } else {
            uri = Uri.fromFile(apkFile);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        mContext.startActivity(intent);
    }

    public static void uninstallApk(String packageName) {
        Intent intent = new Intent(Intent.ACTION_DELETE);
        Uri packageURI = Uri.parse("package:" + packageName);
        intent.setData(packageURI);
        mContext.startActivity(intent);
    }

    public static boolean isMainProcess() {
        ActivityManager am = ((ActivityManager) mContext.getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        // Detecting Application Class Running on Main Process on a Multiprocess app
        // The Current Running Process
        String mainProcessName = mContext.getPackageName();
        int myPid = android.os.Process.myPid();
        for (RunningAppProcessInfo info : processes) {
            if (info.pid == myPid && mainProcessName.equals(info.processName)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isInServiceProcess(Context context, Class<? extends Service> serviceClass) {
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo;
        try {
            packageInfo = packageManager.getPackageInfo(context.getPackageName(), GET_SERVICES);
        } catch (Exception e) {
            Log.d("Androidbase", "Could not get package info for " + context.getPackageName());
            return false;
        }
        String mainProcess = packageInfo.applicationInfo.processName;

        ComponentName component = new ComponentName(context, serviceClass);
        ServiceInfo serviceInfo;
        try {
            serviceInfo = packageManager.getServiceInfo(component, PackageManager.GET_DISABLED_COMPONENTS);
        } catch (PackageManager.NameNotFoundException ignored) {
            // Service is disabled.
            return false;
        }
        if (serviceInfo.processName.equals(mainProcess)) {
            Log.d("Androidbase", "Did not expect service " + serviceClass + " to run in main process " + mainProcess);
            // Technically we are in the service process, but we're not in the service dedicated process.
            return false;
        }

        int myPid = android.os.Process.myPid();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.RunningAppProcessInfo myProcess = null;
        List<ActivityManager.RunningAppProcessInfo> runningProcesses;
        try {
            runningProcesses = activityManager.getRunningAppProcesses();
        } catch (SecurityException exception) {
            Log.d("Androidbase", "Could not get running app processes %d", exception);
            return false;
        }
        if (runningProcesses != null) {
            for (ActivityManager.RunningAppProcessInfo process : runningProcesses) {
                if (process.pid == myPid) {
                    myProcess = process;
                    break;
                }
            }
        }
        if (myProcess == null) {
            Log.d("Androidbase", "Could not find running process for " + myPid);
            return false;
        }
        return myProcess.processName.equals(serviceInfo.processName);
    }

    public static boolean isAppDebug() {
        return isAppDebug(mContext.getPackageName());
    }

    public static boolean isAppDebug(final String packageName) {
        if (StringUtils.isEmpty(packageName)) {
            return false;
        }
        try {
            PackageManager pm = mContext.getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(packageName, 0);
            return ai != null && (ai.flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean isActivityAlive(Activity activity) {
        return activity != null && !activity.isFinishing() && !activity.isDestroyed();
    }

    public static boolean startActivity(Context context, Class<?> cls, Bundle extras) {
        Intent intent = new Intent();
        if (extras != null) {
            intent.putExtras(extras);
        }
        intent.setClass(context, cls);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        context.startActivity(intent);
        return true;
    }

    public static void restartApplication() {
        try {
            Intent intent = AndroidUtils.getContext()
                    .getPackageManager()
                    .getLaunchIntentForPackage(AndroidUtils.getContext()
                            .getPackageName());
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            AndroidUtils.getContext().startActivity(intent);
        } catch (Exception e) {
            Log.e(TAG, e + "restartApplication");
        }
    }

}
