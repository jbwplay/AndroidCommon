package com.androidbase.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;

import androidx.annotation.RequiresPermission;

import static android.Manifest.permission.CHANGE_WIFI_STATE;
import static android.content.Context.WIFI_SERVICE;

public final class DeviceUtils {

    public static boolean isDeviceRooted() {
        String su = "su";
        String[] locations = {"/system/bin/", "/system/xbin/", "/sbin/", "/system/sd/xbin/", "/system/bin/failsafe/", "/data/local/xbin/", "/data/local/bin/", "/data/local/", "/system/sbin/", "/usr/bin/", "/vendor/bin/"};
        for (String location : locations) {
            if (new File(location + su).exists()) {
                return true;
            }
        }
        return false;
    }

    public static String getSDKVersionName() {
        return android.os.Build.VERSION.RELEASE;
    }

    public static int getSDKVersionCode() {
        return android.os.Build.VERSION.SDK_INT;
    }

    public static String getManufacturer() {
        return Build.MANUFACTURER;
    }

    public static String getModel() {
        String model = Build.MODEL;
        if (model != null) {
            model = model.trim().replaceAll("\\s*", "");
        } else {
            model = "";
        }
        return model;
    }

    public static String[] getABIs() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            return Build.SUPPORTED_ABIS;
        } else {
            if (!TextUtils.isEmpty(Build.CPU_ABI2)) {
                return new String[]{Build.CPU_ABI, Build.CPU_ABI2};
            }
            return new String[]{Build.CPU_ABI};
        }
    }

    public static boolean isTablet() {
        return (Resources.getSystem()
                .getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_LARGE;
    }

    public static boolean isEmulator() {
        boolean checkProperty = Build.FINGERPRINT.startsWith("generic") || Build.FINGERPRINT
                .toLowerCase()
                .contains("vbox") || Build.FINGERPRINT.toLowerCase()
                .contains("test-keys") || Build.MODEL.contains("google_sdk") || Build.MODEL
                .contains("Emulator") || Build.MODEL.contains("Android SDK built for x86") || Build.MANUFACTURER
                .contains("Genymotion") || (Build.BRAND.startsWith("generic") && Build.DEVICE
                .startsWith("generic")) || "google_sdk".equals(Build.PRODUCT);
        if (checkProperty) {
            return true;
        }

        String operatorName = "";
        TelephonyManager tm = (TelephonyManager) AndroidUtils.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        if (tm != null) {
            String name = tm.getNetworkOperatorName();
            if (name != null) {
                operatorName = name;
            }
        }
        boolean checkOperatorName = operatorName.toLowerCase().equals("android");
        if (checkOperatorName) {
            return true;
        }

        String url = "tel:" + "123456";
        Intent intent = new Intent();
        intent.setData(Uri.parse(url));
        intent.setAction(Intent.ACTION_DIAL);
        boolean checkDial = intent.resolveActivity(AndroidUtils.getContext()
                .getPackageManager()) == null;
        if (checkDial) {
            return true;
        }
        return false;
    }

    /**
     * Return the sim operator using mnc.
     *
     * @return the sim operator
     */
    public static String getSimOperatorByMnc() {
        TelephonyManager tm = (TelephonyManager) AndroidUtils.getContext()
                .getSystemService(Context.TELEPHONY_SERVICE);
        String operator = tm.getSimOperator();
        if (operator == null) {
            return "";
        }
        switch (operator) {
            case "46000":
            case "46002":
            case "46007":
            case "46020":
                return "????????????";
            case "46001":
            case "46006":
            case "46009":
                return "????????????";
            case "46003":
            case "46005":
            case "46011":
                return "????????????";
            default:
                return operator;
        }
    }

    @SuppressLint({"MissingPermission", "HardwareIds"})
    public static String getDeviceId(Context context) {
        String uniqueDeviceId = SPUtils.get("deviceuuid", "");
        if (!StringUtils.isEmpty(uniqueDeviceId)) {
            return uniqueDeviceId;
        }
        try {
            //IMEI : (International Mobile Equipment Identity)???????????????????????????????????????
            //MEID : (Mobile Equipment IDentifier)??????????????????56bit CDMA???????????????????????????
            uniqueDeviceId = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
                    .getDeviceId();
        } catch (Exception e) {
            //??????????????????
            //????????????????????????
            // notAuth premission
        }

        if (!TextUtils.isEmpty(uniqueDeviceId) && !uniqueDeviceId.startsWith("000000")) {
            return uniqueDeviceId;
        } else {
            //????????????,????????????????????????????????????,??????????????????????????????
            String SimSerialNumber = null;
            try {
                SimSerialNumber = ((TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE))
                        .getSimSerialNumber();
            } catch (Exception e) {
                //????????????????????????
                // notAuth premission
            }
            if (!TextUtils.isEmpty(SimSerialNumber)) {
                uniqueDeviceId = SimSerialNumber;
            } else {
                //ANDROID_ID?????????????????????????????????????????????64bit???????????????????????????wipe???????????????
                //?????????????????????????????????????????????????????????bug??????????????????????????????????????????ANDROID_ID???9774d56d682e549c
                String androidId = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
                if (!TextUtils.isEmpty(androidId) && !"9774d56d682e549c".equals(androidId)) {
                    uniqueDeviceId = androidId;
                } else {
                    uniqueDeviceId = new Installation().id(context);
                }
            }
        }

        return uniqueDeviceId;
    }

    /**
     * ?????????????????????????????????????????????????????????????????????ID?????????????????????????????????????????????????????? ???????????????????????????????????????ID???????????????????????????????????????????????????????????????????????????ID???
     * ?????????????????????????????????ID?????????????????????????????????????????????????????????????????? ID??????Installtion ID??????????????????????????????????????????
     */
    static class Installation {
        private String sID = null;

        public synchronized String id(Context context) {
            if (sID == null) {
                File installation = new File(context.getFilesDir(), "INSTALLATION");
                try {
                    if (!installation.exists()) {
                        writeInstallationFile(installation);
                    }
                    sID = readInstallationFile(installation);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
            return sID;
        }

        private String readInstallationFile(File installation) throws IOException {
            RandomAccessFile f = new RandomAccessFile(installation, "r");
            byte[] bytes = new byte[(int) f.length()];
            f.readFully(bytes);
            f.close();
            return new String(bytes);
        }

        private void writeInstallationFile(File installation) throws IOException {
            FileOutputStream out = new FileOutputStream(installation);
            String id = MiscUtils.getRandomUUIDToString();
            out.write(id.getBytes());
            out.close();
        }
    }

}
