package com.androidbase.okhttp;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * <p/>
 * note: read responebody content do not be the mainthread
 * <p/>
 * note: call response.body().byteStream() will show Responprogress
 * <p/>
 */
public class ProgressResponseBody extends ResponseBody {

    private final ResponseBody responseBody;
    private final ResponProgressListener progressListener;
    private BufferedSource bufferedSource;

    public ProgressResponseBody(ResponseBody body, ResponProgressListener listener) {
        this.responseBody = body;
        this.progressListener = listener;
    }

    @Override
    public MediaType contentType() {
        return responseBody.contentType();
    }

    @Override
    public long contentLength() {
        return responseBody.contentLength();
    }

    @Override
    public BufferedSource source() {
        if (bufferedSource == null) {
            bufferedSource = Okio.buffer(source(responseBody.source()));
        }
        return bufferedSource;
    }

    private Source source(Source source) {
        return new ForwardingSource(source) {
            long totalBytesRead = 0L;

            @Override
            public long read(Buffer sink, long byteCount) throws IOException {
                final long bytesRead = super.read(sink, byteCount);
                // read() returns the number of bytes read, or -1 if this source is
                // exhausted.
                totalBytesRead += bytesRead != -1 ? bytesRead : 0;
                OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        if (progressListener != null) {
                            progressListener.onResponseProgress(totalBytesRead, responseBody
                                    .contentLength(), bytesRead == -1);
                        }
                    }
                });
                return bytesRead;
            }
        };
    }
}
