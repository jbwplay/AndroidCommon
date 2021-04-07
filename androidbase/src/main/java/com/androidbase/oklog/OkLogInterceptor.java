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
            // 数据库记录操作
            throw e;
        }
        long tookMs = TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startNs);
        logDataBuilder.responseDurationMs(tookMs);

        ResponseLogData<Response> responseLogData = logDataInterceptor.processResponse(logDataBuilder, response);
        // 数据库记录操作
        // 进行日志打印
        String logdata = logDataBuilder.toString();
        if (logdata.length() > 4000) {
            int chunkCount = logdata.length() / 4000;
            for (int i = 0; i <= chunkCount; i++) {
                int max = 4000 * (i + 1);
                if (max >= logdata.length()) {
                    Log.i("okhttp", logdata.substring(4000 * i));
                } else {
                    Log.i("okhttp", logdata.substring(4000 * i, max));
                }
            }
        } else {
            Log.i("okhttp", logdata);
        }
        return responseLogData.getResponse();
    }

}
