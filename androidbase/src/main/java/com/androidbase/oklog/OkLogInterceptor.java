package com.androidbase.oklog;

import android.util.Log;

import com.androidbase.oklog.BaseLogDataInterceptor.RequestLogData;
import com.androidbase.oklog.BaseLogDataInterceptor.ResponseLogData;
import com.androidbase.utils.AndroidUtils;
import com.androidbase.utils.DateUtils;
import com.androidbase.utils.StringUtils;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class OkLogInterceptor implements Interceptor {

    private final static int LOGMAXSIZE = 20 * 1024; //20K MAX Size

    private final LogDataInterceptor logDataInterceptor;

    public OkLogInterceptor() {
        this.logDataInterceptor = new LogDataInterceptor();
    }

    @NonNull
    @Override
    public Response intercept(@NonNull Chain chain) throws IOException {
        RequestLogData<Request> requestLogData = logDataInterceptor.processRequest(chain);
        LogDataBuilder logDataBuilder = requestLogData.getLogData();
        long startNs = System.nanoTime();
        Response response;
        try {
            response = chain.proceed(requestLogData.getRequest());
        } catch (Exception e) {
            logDataBuilder.requestFailed();
            logDataBuilder.responseBody("<-- HTTP FAILED: " + e.getMessage());
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        logDataBuilder.responseDurationMs(tookMs);

        ResponseLogData<Response> responseLogData = logDataInterceptor.processResponse(logDataBuilder, response);
        if (logDataBuilder.getResponseBodySize() <= LOGMAXSIZE && logDataBuilder.getRequestContentLength() <= LOGMAXSIZE) {
            Log.i("okhttp", logDataBuilder.toString());
        }
        return responseLogData.getResponse();
    }

}
