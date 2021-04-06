package com.androidbase.download;

import java.net.HttpURLConnection;

public class DownLoadConstants {

    public static final class HTTP {
        public static final int CONNECT_TIME_OUT = 10 * 1000;
        public static final int READ_TIME_OUT = 10 * 1000;
        public static final String GET = "GET";
    }

    public static final int HTTP_TEMPORARY_REDIRECT = 307;
    public static final int HTTP_PERMANENT_REDIRECT = 308;

    public static boolean isRedirection(int code) {
        return code == HttpURLConnection.HTTP_MOVED_PERM || code == HttpURLConnection.HTTP_MOVED_TEMP || code == HttpURLConnection.HTTP_SEE_OTHER || code == HttpURLConnection.HTTP_MULT_CHOICE || code == HTTP_TEMPORARY_REDIRECT || code == HTTP_PERMANENT_REDIRECT;
    }

}
