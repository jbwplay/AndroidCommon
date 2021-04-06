package com.androidbase.oklog;

import com.androidbase.utils.StringUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;

public class LogDataBuilder {
    private static final int JSON_INDENT = 4;

    public enum BodyState {

        PLAIN_BODY(1), NO_BODY(2), ENCODED_BODY(3), BINARY_BODY(4), CHARSET_MALFORMED(5);

        private final int intValue;

        BodyState(int intValue) {
            this.intValue = intValue;
        }

        int getIntValue() {
            return intValue;
        }
    }

    private String requestMethod;
    private String requestUrl;
    private String requestUrlPath;
    private String protocol;
    private String requestContentType;
    private long requestContentLength;
    private List<HeaderDataBuilder> requestHeaders;
    private String requestBody;
    private BodyState requestBodyState;
    private boolean requestFailed;

    private int responseCode;
    private String responseMessage;
    private String responseUrl;
    private long responseDurationMs;
    private long responseContentLength;
    private List<HeaderDataBuilder> responseHeaders;
    private BodyState responseBodyState;
    private long responseBodySize;
    private String responseBody;

    LogDataBuilder() {
        this.requestBodyState = BodyState.PLAIN_BODY;
        this.responseBodyState = BodyState.PLAIN_BODY;
    }

    LogDataBuilder requestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
        return this;
    }

    LogDataBuilder requestUrl(String requestUrl) {
        this.requestUrl = requestUrl;
        return this;
    }

    LogDataBuilder requestUrlPath(String requestUrlPath) {
        this.requestUrlPath = requestUrlPath;
        return this;
    }

    LogDataBuilder protocol(String protocol) {
        this.protocol = protocol;
        return this;
    }

    LogDataBuilder requestContentType(String contentType) {
        this.requestContentType = contentType;
        return this;
    }

    LogDataBuilder requestContentLength(long contentLength) {
        this.requestContentLength = contentLength;
        return this;
    }

    LogDataBuilder addRequestHeader(String name, String value) {
        if (this.requestHeaders == null) {
            this.requestHeaders = new ArrayList<>();
        }
        this.requestHeaders.add(new HeaderDataBuilder(name, value));
        return this;
    }

    LogDataBuilder requestBody(String requestBody) {
        this.requestBody = requestBody;
        return this;
    }

    LogDataBuilder requestBodyState(BodyState requestBodyState) {
        this.requestBodyState = requestBodyState;
        return this;
    }

    public LogDataBuilder requestFailed() {
        this.requestFailed = true;
        return this;
    }

    LogDataBuilder responseCode(int responseCode) {
        this.responseCode = responseCode;
        return this;
    }

    LogDataBuilder responseMessage(String responseMessage) {
        this.responseMessage = responseMessage;
        return this;
    }

    LogDataBuilder responseUrl(String responseUrl) {
        this.responseUrl = responseUrl;
        return this;
    }

    public LogDataBuilder responseDurationMs(long responseDurationMs) {
        this.responseDurationMs = responseDurationMs;
        return this;
    }

    LogDataBuilder responseContentLength(long responseContentLength) {
        this.responseContentLength = responseContentLength;
        return this;
    }

    LogDataBuilder addResponseHeader(String name, String value) {
        if (this.responseHeaders == null) {
            this.responseHeaders = new ArrayList<>();
        }
        this.responseHeaders.add(new HeaderDataBuilder(name, value));
        return this;
    }

    LogDataBuilder responseBodyState(BodyState responseBodyState) {
        this.responseBodyState = responseBodyState;
        return this;
    }

    LogDataBuilder responseBodySize(long responseBodySize) {
        this.responseBodySize = responseBodySize;
        return this;
    }

    LogDataBuilder responseBody(String responseBody) {
        this.responseBody = responseBody;
        return this;
    }

    String getRequestMethod() {
        return requestMethod;
    }

    String getRequestUrl() {
        return requestUrl;
    }

    String getRequestUrlPath() {
        return requestUrlPath;
    }

    String getProtocol() {
        return protocol;
    }

    String getRequestContentType() {
        return requestContentType;
    }

    long getRequestContentLength() {
        return requestContentLength;
    }

    List<HeaderDataBuilder> getRequestHeaders() {
        return requestHeaders;
    }

    String getRequestBody() {
        return requestBody;
    }

    BodyState getRequestBodyState() {
        return requestBodyState;
    }

    boolean isRequestFailed() {
        return requestFailed;
    }

    int getResponseCode() {
        return responseCode;
    }

    String getResponseMessage() {
        return responseMessage;
    }

    String getResponseUrl() {
        return responseUrl;
    }

    long getResponseDurationMs() {
        return responseDurationMs;
    }

    long getResponseContentLength() {
        return responseContentLength;
    }

    List<HeaderDataBuilder> getResponseHeaders() {
        if (responseHeaders == null) {
            responseHeaders = new ArrayList<>();
        }
        return responseHeaders;
    }

    BodyState getResponseBodyState() {
        return responseBodyState;
    }

    long getResponseBodySize() {
        return responseBodySize;
    }

    String getResponseBody() {
        if (StringUtils.isEmpty(responseBody)) {
            requestBody = "";
        }
        return responseBody;
    }

    @NonNull
    @Override
    public String toString() {
        StringBuilder logger = new StringBuilder();
        String requestStartMessage = "--> " + requestMethod + ' ' + requestUrl + ' ' + protocol;
        if (!StringUtils.isEmpty(getRequestBody())) {
            requestStartMessage += " (" + requestContentLength + "-byte body)";
            logger.append("\r\n");
        }
        logger.append(requestStartMessage);
        logger.append("\r\n");

        if (!StringUtils.isEmpty(getRequestBody())) {
            if (!StringUtils.isEmpty(getRequestContentType())) {
                logger.append("Content-Type: ").append(getRequestContentType());
                logger.append("\r\n");
            }
            if (requestContentLength != -1) {
                logger.append("Content-Length: ").append(requestContentLength);
                logger.append("\r\n");
            }
        }

        List<HeaderDataBuilder> headerDataBuilderReqList = getRequestHeaders();
        for (int i = 0, count = headerDataBuilderReqList.size(); i < count; i++) {
            HeaderDataBuilder headerDataBuilder = headerDataBuilderReqList.get(i);
            if (!"Content-Type".equalsIgnoreCase(headerDataBuilder.getName()) && !"Content-Length".equalsIgnoreCase(headerDataBuilder
                    .getName())) {
                logger.append(headerDataBuilder.getName())
                        .append(": ")
                        .append(headerDataBuilder.getValue());
                logger.append("\r\n");
            }
        }

        if (requestBodyState == BodyState.NO_BODY) {
            logger.append("--> END ").append(requestMethod);
            logger.append("\r\n");
        } else if (requestBodyState == BodyState.ENCODED_BODY) {
            logger.append("--> END ").append(requestMethod).append(" (encoded body omitted)");
            logger.append("\r\n");
        } else if (requestBodyState == BodyState.BINARY_BODY) {
            logger.append("--> END ")
                    .append(requestMethod)
                    .append(" (binary ")
                    .append(requestContentLength)
                    .append("-byte body ")
                    .append("omitted)");
            logger.append("\r\n");
        } else {
            logger.append(getRequestBody());
            logger.append("--> END ")
                    .append(requestMethod)
                    .append(" (")
                    .append(requestContentLength)
                    .append("-byte body)");
            logger.append("\r\n");
        }

        String bodySize = responseContentLength != -1 ? responseContentLength + "-byte" : "unknown-length";
        logger.append("<-- ")
                .append(responseCode)
                .append(' ')
                .append(responseMessage)
                .append(' ')
                .append(responseUrl)
                .append(" (")
                .append(responseDurationMs)
                .append("ms")
                .append(", ")
                .append(bodySize)
                .append(" body")
                .append(')');
        logger.append("\r\n");

        List<HeaderDataBuilder> headerDataBuilderRspList = getResponseHeaders();
        for (int i = 0, count = headerDataBuilderRspList.size(); i < count; i++) {
            HeaderDataBuilder headerDataBuilder = headerDataBuilderRspList.get(i);
            logger.append(headerDataBuilder.getName())
                    .append(": ")
                    .append(headerDataBuilder.getValue());
            logger.append("\r\n");
        }

        if (responseBodyState == BodyState.NO_BODY) {
            logger.append("<-- END HTTP");
            logger.append("\r\n");
        } else if (responseBodyState == BodyState.ENCODED_BODY) {
            logger.append("<-- END HTTP (encoded body omitted)");
            logger.append("\r\n");
        } else if (responseBodyState == BodyState.BINARY_BODY) {
            logger.append("<-- END HTTP (binary ")
                    .append(responseContentLength)
                    .append("-byte body omitted)");
            logger.append("\r\n");
        } else {
            logger.append(formatBodyToJson(responseBody));
            logger.append("\r\n");
            logger.append("<-- END HTTP (")
                    .append(responseBody.getBytes().length)
                    .append("-byte body)");
            logger.append("\r\n");
        }
        return logger.toString();
    }

    static final class HeaderDataBuilder {

        private final String name;
        private final String value;

        private HeaderDataBuilder(String name, String value) {
            this.name = name;
            this.value = value;
        }

        String getName() {
            return name;
        }

        String getValue() {
            return value;
        }

        @Override
        public String toString() {
            return "HeaderDataBuilder{" + "name='" + name + '\'' + ", value='" + value + '\'' + '}';
        }
    }

    public static String formatBodyToJson(String body) {
        if (StringUtils.isEmpty(body)) {
            return "body is null";
        }
        String result;
        try {
            if (body.startsWith("{")) {
                JSONObject jsonObject = new JSONObject(body);
                result = jsonObject.toString(JSON_INDENT);
            } else if (body.startsWith("[")) {
                JSONArray jsonArray = new JSONArray(body);
                result = jsonArray.toString(JSON_INDENT);
            } else {
                result = body;
            }
        } catch (JSONException e) {
            result = body;
        }

        return result;
    }

}
