package com.androidbase.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Parcelable;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.Telephony;
import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.RequiresApi;

import static android.provider.Settings.*;

public final class IntentUtils {

    public static void openCalendar(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_CALENDAR);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    public static void openContacts(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_CONTACTS);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    public static void pickContact(Context context) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        startActivity(context, intent);
    }

    public static void viewContact(Context context, String name) {
        Intent intent = new Intent(Intent.ACTION_VIEW, getUriOfContactByName(context, name));
        startActivity(context, intent);
    }

    public static void editContact(Context context, String name) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setData(getUriOfContactByName(context, name));
        startActivity(context, intent);
    }

    public static void insertContact(Context context, String name, String phone, String email, String company, String job, String notes) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setType(ContactsContract.Contacts.CONTENT_TYPE);
        intent.putExtra(ContactsContract.Intents.Insert.NAME, name);
        intent.putExtra(ContactsContract.Intents.Insert.PHONE, phone);
        intent.putExtra(ContactsContract.Intents.Insert.EMAIL, email);
        intent.putExtra(ContactsContract.Intents.Insert.COMPANY, company);
        intent.putExtra(ContactsContract.Intents.Insert.JOB_TITLE, job);
        intent.putExtra(ContactsContract.Intents.Insert.NOTES, notes);
        startActivity(context, intent);
    }

    private static Uri getUriOfContactByName(Context context, String name) {
        try (Cursor cursor = context.getContentResolver()
                .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID}, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + "=? ", new String[]{name}, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                long idContact = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                return ContentUris.withAppendedId(ContactsContract.Contacts.CONTENT_URI, idContact);
            }
        }
        return null;
    }

    public static void shareTextIntent(Context context, final String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        startActivity(context, intent);
    }

    public static void shareImageIntent(Context context, String content, String imagePath) {
        if (StringUtils.isEmpty(imagePath)) {
            return;
        }
        shareImageIntent(context, content, new File(imagePath));
    }

    public static void shareImageIntent(Context context, String content, File image) {
        if (image == null || !image.isFile()) {
            return;
        }
        shareImageIntent(context, content, UriUtils.file2Uri(context, image));
    }

    public static void shareImageIntent(Context context, String content, Uri uri) {
        Intent fileintent = new Intent(Intent.ACTION_SEND);
        fileintent.putExtra(Intent.EXTRA_TEXT, content);
        fileintent.putExtra(Intent.EXTRA_STREAM, uri);
        fileintent.setType("image/*");
        startActivity(context, fileintent);
    }

    public static Intent shareImageChooseIntent(Context context, String content, Uri uri) {
        Intent fileintent = new Intent(Intent.ACTION_SEND);
        fileintent.putExtra(Intent.EXTRA_TEXT, content);
        fileintent.putExtra(Intent.EXTRA_STREAM, uri);
        final List<Intent> fileIntents = new ArrayList<>();
        final PackageManager packageManager = AndroidUtils.getContext()
                .getPackageManager();
        @SuppressLint("QueryPermissionsNeeded") //Android 11+
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(fileintent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent i = new Intent(fileintent);
            i.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            i.setPackage(packageName);
            fileIntents.add(i);
        }
        Intent i = new Intent(Intent.ACTION_GET_CONTENT);
        i.addCategory(Intent.CATEGORY_OPENABLE);
        i.setType("image/*");
        Intent chooserIntent = Intent.createChooser(i, "Image Chooser");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, fileIntents.toArray(new Parcelable[]{}));
        return chooserIntent;
    }

    public void openEmail(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_EMAIL);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    public void sendEmail(Context context, String to, String subject, String message) {
        sendEmail(context, new String[]{to}, subject, message);
    }

    public void sendEmail(Context context, String[] to, String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, to);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        startActivity(context, intent);
    }

    public void sendEmail(Context context, String[] addresses, String[] cc, String[] bcc, String subject, String message) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        //intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_CC, cc);
        intent.putExtra(Intent.EXTRA_BCC, bcc);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, message);
        //intent.putExtra(Intent.EXTRA_STREAM, attachment);
        startActivity(context, intent);
    }

    public void createEvent(Context context, String title, String description) {
        Intent intent = new Intent(Intent.ACTION_EDIT);
        intent.setType("vnd.android.cursor.item/event");
        intent.putExtra("title", title);
        intent.putExtra("description", description);
        startActivity(context, intent);
    }

    public void createEvent(Context context, String title, String description, String location, long begin, long end, int color, boolean allDay) {
        Intent intent = new Intent(Intent.ACTION_INSERT);
        intent.setData(CalendarContract.Events.CONTENT_URI);
        intent.putExtra(CalendarContract.Events.TITLE, title);
        intent.putExtra(CalendarContract.Events.DESCRIPTION, description);
        intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location);
        intent.putExtra(CalendarContract.Events.EVENT_COLOR, color);
        // intent.putExtra(CalendarContract.Events.EVENT_TIMEZONE, timeZone);
        intent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, begin);
        intent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, end);
        intent.putExtra(CalendarContract.EXTRA_EVENT_ALL_DAY, allDay);
        startActivity(context, intent);
    }

    public void openMessages(Context context) {
        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_APP_MESSAGING);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(context, intent);
    }

    public void createEmptySms(Context context) {
        createSms(context, null, (String[]) null);
    }

    public void createEmptySms(Context context, String phoneNumber) {
        createSms(context, null, new String[]{phoneNumber});
    }

    public void createEmptySms(Context context, String[] phoneNumbers) {
        createSms(context, null, phoneNumbers);
    }

    public void createSms(Context context, String body) {
        createSms(context, body, (String[]) null);
    }

    public void createSms(Context context, String body, String phoneNumber) {
        createSms(context, body, new String[]{phoneNumber});
    }

    public void createSms(Context context, String body, String[] phoneNumbers) {
        Uri smsUri;
        if (phoneNumbers == null || phoneNumbers.length == 0) {
            smsUri = Uri.parse("smsto:");
        } else {
            smsUri = Uri.parse("smsto:" + Uri.encode(TextUtils.join(",", phoneNumbers)));
        }

        Intent intent = new Intent(Intent.ACTION_SENDTO, smsUri);
        intent.setPackage(Telephony.Sms.getDefaultSmsPackage(context));
        if (body != null) {
            intent.putExtra("sms_body", body);
        }
        startActivity(context, intent);
    }

    public void showDialNumber(Context context) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:"));
        startActivity(context, intent);
    }

    public void showDialNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phoneNumber.replace(" ", "")));
        startActivity(context, intent);
    }

    public void callNumber(Context context, String phoneNumber) {
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber.replace(" ", "")));
        startActivity(context, intent);
    }

    public void setting(Context context) {
        Intent intent = new Intent(ACTION_SETTINGS);
        startActivity(context, intent);
    }

    public void bluetoothSetting(Context context) {
        Intent intent = new Intent(ACTION_BLUETOOTH_SETTINGS);
        startActivity(context, intent);
    }

    public void dateSetting(Context context) {
        Intent intent = new Intent(ACTION_DATE_SETTINGS);
        startActivity(context, intent);
    }

    public void displaySetting(Context context) {
        Intent intent = new Intent(ACTION_DISPLAY_SETTINGS);
        startActivity(context, intent);
    }

    public void wifiSetting(Context context) {
        Intent intent = new Intent(ACTION_WIFI_SETTINGS);
        startActivity(context, intent);
    }

    public void applicationSetting(Context context) {
        Intent intent = new Intent(ACTION_APPLICATION_SETTINGS);
        startActivity(context, intent);
    }

    public void airplaneModeSetting(Context context) {
        Intent intent = new Intent(ACTION_AIRPLANE_MODE_SETTINGS);
        startActivity(context, intent);
    }

    public void inputMethodSetting(Context context) {
        Intent intent = new Intent(ACTION_INPUT_METHOD_SETTINGS);
        startActivity(context, intent);
    }

    public void internalStorageSetting(Context context) {
        Intent intent = new Intent(ACTION_INTERNAL_STORAGE_SETTINGS);
        startActivity(context, intent);
    }

    public void privacySetting(Context context) {
        Intent intent = new Intent(ACTION_PRIVACY_SETTINGS);
        startActivity(context, intent);
    }

    public void soundSetting(Context context) {
        Intent intent = new Intent(ACTION_SOUND_SETTINGS);
        startActivity(context, intent);
    }

    public void applicationDetailSetting(Context context, String packageName) {
        Intent intent = new Intent(ACTION_APPLICATION_DETAILS_SETTINGS, Uri.parse("package:" + packageName));
        startActivity(context, intent);
    }

    public void dataRoamingSetting(Context context) {
        Intent intent = new Intent(ACTION_DATA_ROAMING_SETTINGS);
        startActivity(context, intent);
    }

    public void deviceInfoSetting(Context context) {
        Intent intent = new Intent(ACTION_DEVICE_INFO_SETTINGS);
        startActivity(context, intent);
    }

    public void manageApplicationSetting(Context context) {
        Intent intent = new Intent(ACTION_MANAGE_APPLICATIONS_SETTINGS);
        startActivity(context, intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void manageWriteSetting(Context context) {
        Intent intent = new Intent(ACTION_MANAGE_WRITE_SETTINGS);
        startActivity(context, intent);
    }

    public void networkOperatorSetting(Context context) {
        Intent intent = new Intent(ACTION_NETWORK_OPERATOR_SETTINGS);
        startActivity(context, intent);
    }

    public void wifiIpSetting(Context context) {
        Intent intent = new Intent(ACTION_WIFI_IP_SETTINGS);
        startActivity(context, intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    public void ignoreBatteryOptimizationSetting(Context context) {
        Intent intent = new Intent(ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
        startActivity(context, intent);
    }

    public void manageAllApplicationSetting(Context context) {
        Intent intent = new Intent(ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
        startActivity(context, intent);
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public void manageDefaultAppsSetting(Context context) {
        Intent intent = new Intent(ACTION_MANAGE_DEFAULT_APPS_SETTINGS);
        startActivity(context, intent);
    }


    @RequiresApi(api = Build.VERSION_CODES.M)
    public void notificationPolicyAccessSetting(Context context) {
        // 勿扰权限
        Intent intent = new Intent(ACTION_NOTIFICATION_POLICY_ACCESS_SETTINGS);
        startActivity(context, intent);
    }

    private static void startActivity(Context context, final Intent intent) {
        try {
            if (!(context instanceof Activity)) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            context.startActivity(intent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
