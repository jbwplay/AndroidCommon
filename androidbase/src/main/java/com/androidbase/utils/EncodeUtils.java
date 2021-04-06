package com.androidbase.utils;

import android.os.Build;
import android.text.Html;
import android.util.Base64;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;

/**
 * Uri网络传输数据编码
 * The type Encode utils.
 */
public final class EncodeUtils {

    /**
     * Url encode string.
     *
     * @param input the input
     * @return the string
     */
    public static String urlEncode(final String input) {
        return urlEncode(input, "UTF-8");
    }

    /**
     * Url encode string.
     *
     * @param input   the input
     * @param charset the charset
     * @return the string
     */
    public static String urlEncode(final String input, final String charset) {
        try {
            return URLEncoder.encode(input, charset);
        } catch (UnsupportedEncodingException e) {
            return input;
        }
    }

    /**
     * Url decode string.
     *
     * @param input the input
     * @return the string
     */
    public static String urlDecode(final String input) {
        return urlDecode(input, "UTF-8");
    }

    /**
     * Url decode string.
     *
     * @param input   the input
     * @param charset the charset
     * @return the string
     */
    public static String urlDecode(final String input, final String charset) {
        try {
            return URLDecoder.decode(input, charset);
        } catch (UnsupportedEncodingException e) {
            return input;
        }
    }

    /**
     * Base 64 encode byte [ ].
     *
     * @param input the input
     * @return the byte [ ]
     */
    public static byte[] base64Encode(final String input) {
        return base64Encode(input.getBytes());
    }

    /**
     * Base 64 encode byte [ ].
     *
     * @param input the input
     * @return the byte [ ]
     */
    public static byte[] base64Encode(final byte[] input) {
        return Base64.encode(input, Base64.NO_WRAP);
    }

    /**
     * Base 64 encode 2 string string.
     *
     * @param input the input
     * @return the string
     */
    public static String base64Encode2String(final byte[] input) {
        return Base64.encodeToString(input, Base64.NO_WRAP);
    }

    /**
     * Base 64 decode byte [ ].
     *
     * @param input the input
     * @return the byte [ ]
     */
    public static byte[] base64Decode(final String input) {
        return Base64.decode(input, Base64.NO_WRAP);
    }

    /**
     * Base 64 decode byte [ ].
     *
     * @param input the input
     * @return the byte [ ]
     */
    public static byte[] base64Decode(final byte[] input) {
        return Base64.decode(input, Base64.NO_WRAP);
    }

    /**
     * Base 64 url safe encode byte [ ].
     *
     * @param input the input
     * @return the byte [ ]
     */
    public static byte[] base64UrlSafeEncode(final String input) {
        return Base64.encode(input.getBytes(), Base64.URL_SAFE);
    }

    /**
     * Html encode string.
     *
     * @param input the input
     * @return the string
     */
    public static String htmlEncode(final CharSequence input) {
        StringBuilder sb = new StringBuilder();
        char c;
        for (int i = 0, len = input.length(); i < len; i++) {
            c = input.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;"); //$NON-NLS-1$
                    break;
                case '>':
                    sb.append("&gt;"); //$NON-NLS-1$
                    break;
                case '&':
                    sb.append("&amp;"); //$NON-NLS-1$
                    break;
                case '\'':
                    //http://www.w3.org/TR/xhtml1
                    // The named character reference &apos; (the apostrophe, U+0027) was
                    // introduced in XML 1.0 but does not appear in HTML. Authors should
                    // therefore use &#39; instead of &apos; to work as expected in HTML 4
                    // user agents.
                    sb.append("&#39;"); //$NON-NLS-1$
                    break;
                case '"':
                    sb.append("&quot;"); //$NON-NLS-1$
                    break;
                default:
                    sb.append(c);
            }
        }
        return sb.toString();
    }

    /**
     * Html decode char sequence.
     *
     * @param input the input
     * @return the char sequence
     */
    public static CharSequence htmlDecode(final String input) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return Html.fromHtml(input, Html.FROM_HTML_MODE_LEGACY);
        } else {
            return Html.fromHtml(input);
        }
    }
}
