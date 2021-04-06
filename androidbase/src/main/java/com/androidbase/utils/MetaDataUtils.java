package com.androidbase.utils;

import android.app.Activity;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ServiceInfo;

import androidx.annotation.NonNull;

public final class MetaDataUtils {

    public static String getMetaDataInApp(@NonNull final String key) {
        String value = "";
        PackageManager pm = AndroidUtils.getContext().getPackageManager();
        String packageName = AndroidUtils.getContext().getPackageName();
        try {
            ApplicationInfo ai = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
            value = String.valueOf(ai.metaData.get(key));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String getMetaDataInActivity(@NonNull final Activity activity, @NonNull final String key) {
        return getMetaDataInActivity(activity.getClass(), key);
    }

    public static String getMetaDataInActivity(@NonNull final Class<? extends Activity> clz, @NonNull final String key) {
        String value = "";
        PackageManager pm = AndroidUtils.getContext().getPackageManager();
        ComponentName componentName = new ComponentName(AndroidUtils.getContext(), clz);
        try {
            ActivityInfo ai = pm.getActivityInfo(componentName, PackageManager.GET_META_DATA);
            value = String.valueOf(ai.metaData.get(key));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String getMetaDataInService(@NonNull final Service service, @NonNull final String key) {
        return getMetaDataInService(service.getClass(), key);
    }

    public static String getMetaDataInService(@NonNull final Class<? extends Service> clz, @NonNull final String key) {
        String value = "";
        PackageManager pm = AndroidUtils.getContext().getPackageManager();
        ComponentName componentName = new ComponentName(AndroidUtils.getContext(), clz);
        try {
            ServiceInfo info = pm.getServiceInfo(componentName, PackageManager.GET_META_DATA);
            value = String.valueOf(info.metaData.get(key));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

    public static String getMetaDataInReceiver(@NonNull final BroadcastReceiver receiver, @NonNull final String key) {
        return getMetaDataInReceiver(receiver.getClass(), key);
    }

    public static String getMetaDataInReceiver(@NonNull final Class<? extends BroadcastReceiver> clz, @NonNull final String key) {
        String value = "";
        PackageManager pm = AndroidUtils.getContext().getPackageManager();
        ComponentName componentName = new ComponentName(AndroidUtils.getContext(), clz);
        try {
            ActivityInfo info = pm.getReceiverInfo(componentName, PackageManager.GET_META_DATA);
            value = String.valueOf(info.metaData.get(key));
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return value;
    }

}
