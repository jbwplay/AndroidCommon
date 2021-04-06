package com.androidbase.okhttp.callback;

import android.net.ParseException;
import android.view.View;
import android.widget.Toast;

import com.androidbase.okhttp.OkHttpException;
import com.androidbase.utils.AndroidUtils;
import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;

import org.json.JSONException;

import java.lang.ref.WeakReference;
import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

import androidx.annotation.UiThread;

/*----内部类持有外部类的实例，网络请求结束，activity实例不会销毁----*/
public abstract class StringCallbackWeakReference extends StringCallback {

    //与具体的View进行关联,内部类会持有外部类的实例,则必然造成Activity实例不为NULL,弱应用Activity里面的成员变量,
    //Activity销毁时变量会为NULL
    //必要方法：声明静态匿名内部类+弱引用

    private WeakReference<View> mViewWeakReference;

    public StringCallbackWeakReference(View view) {
        mViewWeakReference = new WeakReference<View>(view);
    }

    @UiThread
    public View getViewWeakReference() {
        return mViewWeakReference == null ? null : mViewWeakReference.get();
    }


    @Override
    public void handleResponse(String response) {
        if (getViewWeakReference() != null) {
            handleHttpRespone(response);
        }
    }

    @Override
    public boolean handleException(Throwable throwable) {
        if (getViewWeakReference() != null) {

            String msg = "";
            if (throwable instanceof ConnectException) {
                msg = "网络不可用";
            } else if (throwable instanceof UnknownHostException) {
                // msg = "未知主机错误";
                msg = "网络不可用";
            } else if (throwable instanceof SocketTimeoutException) {
                msg = "请求网络超时";
            } else if (throwable instanceof OkHttpException) {
                OkHttpException httpException = (OkHttpException) throwable;
                msg = convertStatusCode(httpException);
            } else if (throwable instanceof JsonParseException || throwable instanceof ParseException || throwable instanceof JSONException || throwable instanceof JsonIOException) {
                msg = "数据解析错误";
            }

            if (!msg.isEmpty()) {
                Toast toast = Toast.makeText(AndroidUtils.getContext(), null, Toast.LENGTH_SHORT);
                toast.setText(msg);
            }

        }
        return false;
    }

    private String convertStatusCode(OkHttpException httpException) {
        String msg;
        if (httpException.code() == 500) {
            msg = "服务器发生错误";
        } else if (httpException.code() == 404) {
            msg = "请求地址不存在";
        } else if (httpException.code() == 403) {
            msg = "请求被服务器拒绝";
        } else if (httpException.code() == 307) {
            msg = "请求被重定向到其他页面";
        } else {
            msg = httpException.message();
        }
        return msg;
    }

    public abstract void handleHttpRespone(String response);
}
