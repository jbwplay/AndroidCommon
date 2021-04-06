package com.androidbase.utils;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import androidx.annotation.NonNull;

public final class GsonUtils {

    private static final String KEY_DEFAULT = "defaultGson";
    private static final String KEY_DELEGATE = "delegateGson";

    private static final Map<String, Gson> GSONS = new ConcurrentHashMap<>();

    public static void setGsonDelegate(Gson delegate) {
        if (delegate == null) {
            return;
        }
        GSONS.put(KEY_DELEGATE, delegate);
    }

    public static void setGson(String key, Gson gson) {
        if (TextUtils.isEmpty(key) || gson == null) {
            return;
        }
        GSONS.put(key, gson);
    }

    public static Gson getGson(String key) {
        return GSONS.get(key);
    }

    public static Gson getGson() {
        Gson gsonDelegate = GSONS.get(KEY_DELEGATE);
        if (gsonDelegate != null) {
            return gsonDelegate;
        }
        Gson gsonDefault = GSONS.get(KEY_DEFAULT);
        if (gsonDefault == null) {
            gsonDefault = createGson();
            GSONS.put(KEY_DEFAULT, gsonDefault);
        }
        return gsonDefault;
    }

    public static String toJson(Object object) {
        return toJson(getGson(), object);
    }

    public static String toJson(@NonNull Gson gson, Object object) {
        return gson.toJson(object);
    }

    public static <T> T fromJson(String json, @NonNull Class<T> type) {
        return fromJson(getGson(), json, type);
    }

    public static <T> T fromJson(String json, @NonNull Type type) {
        return fromJson(getGson(), json, type);
    }

    public static <T> T fromJson(@NonNull Gson gson, String json, @NonNull Class<T> type) {
        return gson.fromJson(json, type);
    }

    public static <T> T fromJson(@NonNull Gson gson, String json, @NonNull Type type) {
        return gson.fromJson(json, type);
    }

    public static Type getListType(@NonNull final Type type) {
        return TypeToken.getParameterized(List.class, type).getType();
    }

    public static Type getSetType(@NonNull final Type type) {
        return TypeToken.getParameterized(Set.class, type).getType();
    }

    public static Type getMapType(@NonNull final Type keyType, @NonNull final Type valueType) {
        return TypeToken.getParameterized(Map.class, keyType, valueType).getType();
    }

    public static Type getArrayType(@NonNull Type type) {
        return TypeToken.getArray(type).getType();
    }

    public static Type getType(@NonNull final Type rawType, @NonNull final Type... typeArguments) {
        return TypeToken.getParameterized(rawType, typeArguments).getType();
    }

    private static Gson createGson() {
        return new GsonBuilder().serializeNulls().disableHtmlEscaping().create();
    }

}
