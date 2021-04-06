package com.androidbase.okhttp;

import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.RequestBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.ForwardingSink;
import okio.Okio;
import okio.Sink;

public class ProgressRequestBody extends RequestBody {

    protected RequestBody delegate;
    protected RequestProgressListener listener;
    protected CountingSink countingSink;
    private long mPreviousTime;

    public ProgressRequestBody(RequestBody delegate, RequestProgressListener listener) {
        this.delegate = delegate;
        this.listener = listener;
    }

    @Override
    public MediaType contentType() {
        return delegate.contentType();
    }

    @Override
    public long contentLength() {
        try {
            return delegate.contentLength();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public RequestBody getDelegate() {
        return delegate;
    }

    public void setDelegate(RequestBody delegate) {
        this.delegate = delegate;
    }

    @Override
    public void writeTo(BufferedSink sink) throws IOException {
        mPreviousTime = System.currentTimeMillis();
        countingSink = new CountingSink(sink);
        BufferedSink bufferedSink = Okio.buffer(countingSink);
        delegate.writeTo(bufferedSink);
        bufferedSink.flush();
    }

    protected final class CountingSink extends ForwardingSink {
        private long bytesWritten = 0;
        private long contentLength = 0;

        public CountingSink(Sink delegate) {
            super(delegate);
        }

        @Override
        public void write(Buffer source, long byteCount) throws IOException {
            super.write(source, byteCount);
            if (contentLength <= 0) {
                contentLength = contentLength();
            }
            bytesWritten += byteCount;

            long totalTime = (System.currentTimeMillis() - mPreviousTime) / 1000;
            if (totalTime == 0) {
                totalTime += 1;
            }
            final long networkSpeed = bytesWritten / totalTime;
            OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                @Override
                public void run() {
                    if (listener != null) {
                        listener.onRequestProgress(bytesWritten, contentLength, networkSpeed);
                    }
                }
            });
        }

    }
}