package com.androidbase.okhttp;

/*
There is no longer a global singleton connection pool. In OkHttp 2.x, all OkHttpClient
instances shared a common connection pool by default. In OkHttp 3.x, each new
OkHttpClient gets its own private connection pool. Applications should avoid creating
many connection pools as doing so prevents connection reuse.

OkHttpClient now implements the new Call.Factory interface.

OkHttp now does cookies. This new cookie model follows the latest RFC.

Form and Multipart bodies are now modeled. upgraded FormBody and FormBody.Builder
upgraded MultipartBody, MultipartBody.Part, and MultipartBody.Builder.

Canceling batches of calls is now the application's responsibility.
*/

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.androidbase.oklog.OkLogInterceptor;
import com.androidbase.utils.AndroidUtils;

import java.io.File;

import okhttp3.Cache;
import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

/**
 * 必须初始先调用OkHttpUtils.getInstance.init(context)
 */
public final class OkHttpUtils {

    static class SingletonHolder {
        static OkHttpUtils INSTANCE = new OkHttpUtils();
    }

    public static OkHttpUtils getInstance() {
        return SingletonHolder.INSTANCE;
    }

    private OkHttpClient mHttpClient = null;
    private Handler mDelivery;

    public void init(Context context) {
        int cacheSize = 16 * 1024 * 1024; // 16 MiB
        Cache cache = new Cache(new File(context.getCacheDir(), "okhttp3"), cacheSize);

        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (AndroidUtils.isAppDebug()) {
            HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
            loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
            builder.addInterceptor(loggingInterceptor);

            OkLogInterceptor okLogInterceptor = new OkLogInterceptor();
            builder.addInterceptor(okLogInterceptor);
        }
        mHttpClient = builder.retryOnConnectionFailure(true).build();
        mDelivery = new Handler(Looper.getMainLooper());
    }

    public OkHttpClient getHttpClient() {
        return mHttpClient;
    }

    public static OkHttpRequest head(final String url) {
        return new OkHttpRequest(RequestMethod.HEAD, url);
    }

    public static OkHttpRequest get(final String url) {
        return new OkHttpRequest(RequestMethod.GET, url);
    }

    public static OkHttpRequest delete(final String url) {
        return new OkHttpRequest(RequestMethod.DELETE, url);
    }

    public static OkHttpRequest post(final String url) {
        return new OkHttpRequest(RequestMethod.POST, url);
    }

    public static OkHttpRequest put(final String url) {
        return new OkHttpRequest(RequestMethod.PUT, url);
    }

    public Handler getDelivery() {
        return mDelivery;
    }

    public void cancelTag(Object tag) {
        for (Call call : mHttpClient.dispatcher().queuedCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }

        for (Call call : mHttpClient.dispatcher().runningCalls()) {
            if (tag.equals(call.request().tag())) {
                call.cancel();
            }
        }
    }
}
