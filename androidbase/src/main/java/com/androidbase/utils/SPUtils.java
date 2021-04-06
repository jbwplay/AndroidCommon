package com.androidbase.utils;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.Set;

public class SPUtils {

    private static final String SHAREDPREFERENCES_NAME = "mylib_sprefs";
    private static SharedPreferences sp;

    public static SharedPreferences getPreferences(String name) {
        if (sp == null) {
            sp = AndroidUtils.getContext()
                    .getSharedPreferences(name, Context.MODE_PRIVATE);
        }
        return sp;
    }

    public static boolean get(String key, boolean defValue) {
        return get(SHAREDPREFERENCES_NAME, key, defValue);
    }

    public static int get(String key, int defValue) {
        return get(SHAREDPREFERENCES_NAME, key, defValue);
    }

    public static float get(String key, float defValue) {
        return get(SHAREDPREFERENCES_NAME, key, defValue);
    }

    public static long get(String key, long defValue) {
        return get(SHAREDPREFERENCES_NAME, key, defValue);
    }

    public static String get(String key, String defValue) {
        return get(SHAREDPREFERENCES_NAME, key, defValue);
    }

    public static Set<String> get(String key, Set<String> defValue) {
        return get(SHAREDPREFERENCES_NAME, key, defValue);
    }

    public static boolean get(String name, String key, boolean defValue) {
        return getPreferences(name).getBoolean(key, defValue);
    }

    public static int get(String name, String key, int defValue) {
        return getPreferences(name).getInt(key, defValue);
    }

    public static float get(String name, String key, float defValue) {
        return getPreferences(name).getFloat(key, defValue);
    }

    public static long get(String name, String key, long defValue) {
        return getPreferences(name).getLong(key, defValue);
    }

    public static String get(String name, String key, String defValue) {
        return getPreferences(name).getString(key, defValue);
    }

    public static Set<String> get(String name, String key, Set<String> defValue) {
        return getPreferences(name).getStringSet(key, defValue);
    }

    public static void put(String key, boolean value) {
        put(SHAREDPREFERENCES_NAME, key, value);
    }

    public static void put(String key, int value) {
        put(SHAREDPREFERENCES_NAME, key, value);
    }

    public static void put(String key, float value) {
        put(SHAREDPREFERENCES_NAME, key, value);
    }

    public static void put(String key, long value) {
        put(SHAREDPREFERENCES_NAME, key, value);
    }

    public static void put(String key, String value) {
        put(SHAREDPREFERENCES_NAME, key, value);
    }

    public static void put(String key, Set<String> value) {
        put(SHAREDPREFERENCES_NAME, key, value);
    }

    public static void put(String name, String key, boolean value) {
        getPreferences(name).edit().putBoolean(key, value).apply();
    }

    public static void put(String name, String key, int value) {
        getPreferences(name).edit().putInt(key, value).apply();
    }

    public static void put(String name, String key, float value) {
        getPreferences(name).edit().putFloat(key, value).apply();
    }

    public static void put(String name, String key, long value) {
        getPreferences(name).edit().putLong(key, value).apply();
    }

    public static void put(String name, String key, String value) {
        getPreferences(name).edit().putString(key, value).apply();
    }

    public static void put(String name, String key, Set<String> value) {
        getPreferences(name).edit().putStringSet(key, value).apply();
    }

    public static void remove(String key) {
        remove(SHAREDPREFERENCES_NAME, key);
    }

    public static void remove(String name, String key) {
        getPreferences(name).edit().remove(key).apply();
    }


    public static void clear() {
        clear(SHAREDPREFERENCES_NAME);
    }

    public static void clear(String name) {
        getPreferences(name).edit().clear().apply();
    }

}
