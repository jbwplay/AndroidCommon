package com.androidbase.okhttp;

import android.text.TextUtils;

import com.androidbase.okhttp.callback.FileCallback;
import com.androidbase.okhttp.callback.HttpCallback;
import com.androidbase.okhttp.callback.StringCallback;
import com.androidbase.okhttp.transformer.FileTransformer;
import com.androidbase.okhttp.transformer.HttpTransformer;
import com.androidbase.okhttp.transformer.StringTransformer;
import com.androidbase.utils.DigestUtils;
import com.androidbase.utils.MiscUtils;
import com.google.gson.Gson;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Headers;
import okhttp3.HttpUrl;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class OkHttpRequest {
    public final static String APPLICATION_OCTET_STREAM = "application/octet-stream";
    public final static MediaType MEDIA_TYPE_OCTET_STREAM = MediaType.parse("application/octet-stream; " + "charset=utf-8");
    public final static String CONTENT_TYPE_FORM_ENCODED = "application/x-www-form-urlencoded";
    public final static MediaType MEDIA_TYPE_FORM_ENCODED = MediaType.parse("application/x-www-form-urlencoded; " + "charset=utf-8");

    public static final MediaType MEDIA_TYPE_PLAIN = MediaType.parse("text/plain;" + "charset=utf-8");
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json;" + "charset=utf-8");

    protected final RequestMethod method;
    protected final HttpUrl httpUrl;
    protected RequestParam params;
    protected ResponProgressListener listener;
    protected HttpTransformer mHttpTransformer;
    protected HttpCallback mHttpCallback;

    public OkHttpRequest(final RequestMethod method, String url) {
        MiscUtils.notNull(method, "http method can not be null");
        MiscUtils.notEmpty(url, "http url can not be null or empty");
        this.method = method;
        this.params = new RequestParam();
        this.httpUrl = parseUrlAndQueryString(url);
        MiscUtils.notNull(params, "http params can not be null");
        MiscUtils.notNull(this.httpUrl, "http url can not be null");
    }

    private HttpUrl parseUrlAndQueryString(final String fullUrl) {
        final String[] urlParts = fullUrl.split("\\?");
        String url = urlParts[0];
        if (urlParts.length > 1) {
            this.params.queries(parseQueryString(urlParts[1]));
        }
        return HttpUrl.parse(url);
    }

    public OkHttpRequest listener(final ResponProgressListener listener) {
        this.listener = listener;
        return this;
    }

    public OkHttpRequest header(String name, String value) {
        this.params.header(name, value);
        return this;
    }

    public OkHttpRequest headers(Map<String, String> headers) {
        if (headers != null) {
            this.params.headers(headers);
        }
        return this;
    }

    public OkHttpRequest query(String key, String value) {
        MiscUtils.notEmpty(key, "key must not be null or empty.");
        this.params.query(key, value);
        return this;
    }

    public OkHttpRequest queries(Map<String, String> queries) {
        this.params.queries(queries);
        return this;
    }

    public OkHttpRequest form(String key, String value) {
        if (supportBody()) {
            this.params.form(key, value);
        }
        return this;
    }

    public OkHttpRequest forms(Map<String, String> forms) {
        if (supportBody()) {
            this.params.forms(forms);
        }
        return this;
    }

    protected OkHttpRequest part(final MultiPartFile part) {
        this.params.parts.add(part);
        return this;
    }

    public OkHttpRequest parts(Collection<MultiPartFile> parts) {
        if (supportBody()) {
            for (final MultiPartFile part : parts) {
                part(part);
            }
        }
        return this;
    }

    public OkHttpRequest file(String key, File file) {
        if (supportBody()) {
            this.params.file(key, file);
        }
        return this;
    }

    public OkHttpRequest file(String key, File file, String contentType) {
        if (supportBody()) {
            this.params.file(key, file, contentType);
        }
        return this;
    }

    public OkHttpRequest file(String key, File file, String contentType, String fileName) {
        if (supportBody()) {
            this.params.file(key, file, contentType, fileName);
        }
        return this;
    }

    public OkHttpRequest json(String key, Object value) {
        if (supportBody()) {
            this.params.json(key, value);
        }
        return this;
    }

    public OkHttpRequest jsons(Map<String, Object> jsons) {
        if (supportBody()) {
            this.params.jsons(jsons);
        }
        return this;
    }

    protected boolean supportBody() {
        return RequestMethod.supportBody(method);
    }

    public HttpUrl url() {
        return buildUrlWithQueries();
    }

    public RequestMethod method() {
        return method;
    }

    protected Map<String, String> headers() {
        return this.params.headers;
    }

    HttpUrl buildUrlWithQueries() {
        final HttpUrl.Builder builder = httpUrl.newBuilder();
        for (final Map.Entry<String, String> entry : params.queries().entrySet()) {
            builder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        return builder.build();
    }

    protected Map<String, String> queries() {
        return this.params.queries;
    }

    protected Map<String, String> forms() {
        return this.params.forms;
    }

    protected List<MultiPartFile> parts() {
        return this.params.parts;
    }

    protected Map<String, Object> jsons() {
        return this.params.jsons;
    }


    protected boolean hasParts() {
        return this.params.parts.size() > 0;
    }

    protected boolean hasForms() {
        return this.params.forms.size() > 0;
    }

    protected boolean hasjsons() {
        return this.params.jsons.size() > 0;
    }

    protected RequestBody getRequestBody() throws IOException {
        if (!supportBody()) {
            return null;
        }

        RequestBody requestBody;
        if (hasParts()) {
            final MultipartBody.Builder multipart = new MultipartBody.Builder().setType(MultipartBody.FORM);
            for (final MultiPartFile part : parts()) {
                if (part.getBody() != null) {
                    multipart.addFormDataPart(part.getName(), part.getFileName(), part.getBody());
                }
            }
            for (Map.Entry<String, String> entry : forms().entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                multipart.addFormDataPart(key, value == null ? "" : value);
            }
            requestBody = multipart.build();
        } else if (hasForms()) {
            final FormBody.Builder bodyBuilder = new FormBody.Builder();
            for (Map.Entry<String, String> entry : forms().entrySet()) {
                final String key = entry.getKey();
                final String value = entry.getValue();
                bodyBuilder.add(key, value == null ? "" : value);
            }
            requestBody = bodyBuilder.build();
        } else if (hasjsons()) {
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, new Gson().toJson(jsons()));
        } else {
            //default JSON
            requestBody = RequestBody.create(MEDIA_TYPE_JSON, "");
        }
        return requestBody;
    }

    public RequestBody wrapRequestBody(RequestBody requestBody) {
        return new ProgressRequestBody(requestBody, new RequestProgressListener() {
            @Override
            public void onRequestProgress(final long bytesWritten, final long contentLength, final long networkSpeed) {
                OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mHttpCallback != null) {
                            mHttpCallback.onRequestProgress(bytesWritten, contentLength, networkSpeed);
                        }
                    }
                });
            }
        });
    }

    public static Map<String, String> parseQueryString(String queryString) {
        if (TextUtils.isEmpty(queryString)) {
            return null;
        }
        try {
            Map<String, String> queries = new HashMap<String, String>();
            for (String param : queryString.split("&")) {
                /*String[] pair = param.split("=");
                String key = URLDecoder.decode(pair[0], "UTF-8");
                if (pair.length > 1) {
                    String value = URLDecoder.decode(pair[1], "UTF-8");
                    queries.put(key, value);
                }*/
                int equalsOffset = param.indexOf('=');
                if (equalsOffset != -1) {
                    String key = URLDecoder.decode(param.substring(0, equalsOffset), "UTF-8");
                    String value = URLDecoder.decode(param.substring(equalsOffset + 1), "UTF-8");
                    queries.put(key, value);
                }
            }
            return queries;
        } catch (UnsupportedEncodingException ex) {
            throw new RuntimeException(ex);
        }
    }

    public Request generateRequest() {
        Request.Builder requestBuilder = new Request.Builder();
        try {
            if (!supportBody()) {
                requestBuilder.url(url())
                        .tag(DigestUtils.md5Hex(url().toString()))
                        .headers(Headers.of(headers()))
                        .method(method().name(), null);
            } else {
                RequestBody requestBody = wrapRequestBody(getRequestBody());
                requestBuilder.url(url())
                        .tag(DigestUtils.md5Hex(url().toString()))
                        .headers(Headers.of(headers()))
                        .method(method().name(), requestBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestBuilder.build();
    }

    private Call generateCall(Request request) {
        /** Returns a shallow copy of this OkHttpClient. Can Customize Http Request Head*/
        final OkHttpClient.Builder clientbuildclon = OkHttpUtils.getInstance()
                .getHttpClient()
                .newBuilder();
        if (mHttpCallback != null) {
            clientbuildclon.addNetworkInterceptor(new ProgressInterceptor(mHttpCallback));
        }
        return clientbuildclon.build().newCall(request);
    }

    public Response execute() throws IOException {
        final Request request = generateRequest();
        Call call = generateCall(request);
        return call.execute();
    }

    public <T> void execute(HttpCallback<T> httpCallback) {
        execute(httpCallback, null, null);
    }

    public <T> void execute(final HttpCallback<T> httpCallback, String filefold, String filename) {
        if (((HttpCallback) httpCallback) instanceof StringCallback) {
            mHttpTransformer = new StringTransformer();
        } else if (((HttpCallback) httpCallback) instanceof FileCallback) {
            mHttpTransformer = new FileTransformer(filefold, filename);
        }
        mHttpCallback = httpCallback;
        Request request = generateRequest();
        Call call = generateCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(final Call call, final IOException e) {
                OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                    @Override
                    public void run() {
                        if (mHttpCallback != null && mHttpTransformer != null) {
                            mHttpCallback.handleException(e);
                        }
                    }
                });
            }

            @Override
            public void onResponse(final Call call, final Response response) throws
                    IOException {
                if (call.isCanceled()) {
                    OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                        @Override
                        public void run() {
                            if (mHttpCallback != null && mHttpTransformer != null) {
                                // Emulate OkHttp's behavior of throwing/delivering an
                                // IOException on cancellation.
                                mHttpCallback.handleException(new IOException("Canceled"));
                            }
                        }
                    });
                } else {
                    if (response.isSuccessful()) {
                        final T responType = (T) mHttpTransformer.transform(response);
                        OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                            @Override
                            public void run() {
                                if (mHttpCallback != null && mHttpTransformer != null) {
                                    mHttpCallback.handleResponse(responType);
                                }
                            }
                        });
                    } else {
                        OkHttpUtils.getInstance().getDelivery().post(new Runnable() {
                            @Override
                            public void run() {
                                if (mHttpCallback != null && mHttpTransformer != null) {
                                    // erroneous (status 400-599)
                                    mHttpCallback.handleException(new OkHttpException(response));
                                }
                            }
                        });
                    }
                }
            }
        });
    }
}
