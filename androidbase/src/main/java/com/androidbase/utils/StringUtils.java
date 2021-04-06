package com.androidbase.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The type String utils.
 */
public class StringUtils {

    /**
     * The constant INDEX_NOT_FOUND.
     */
    public static final int INDEX_NOT_FOUND = -1;
    /**
     * The constant EMPTY.
     */
    public static final String EMPTY = "";

    /**
     * The constant ISO_8859_1.
     */
    public static final Charset ISO_8859_1 = StandardCharsets.ISO_8859_1;
    /**
     * The constant US_ASCII.
     */
    public static final Charset US_ASCII = StandardCharsets.US_ASCII;
    /**
     * The constant UTF_16.
     */
    public static final Charset UTF_16 = StandardCharsets.UTF_16;
    /**
     * The constant UTF_16BE.
     */
    public static final Charset UTF_16BE = StandardCharsets.UTF_16BE;
    /**
     * The constant UTF_16LE.
     */
    public static final Charset UTF_16LE = StandardCharsets.UTF_16LE;
    /**
     * The constant UTF_8.
     */
    public static final Charset UTF_8 = StandardCharsets.UTF_8;

    private static byte[] getBytes(String string, Charset charset) {
        return string == null ? null : string.getBytes(charset);
    }

    /**
     * Get bytes iso 8859 1 byte [ ].
     *
     * @param string the string
     * @return the byte [ ]
     */
    public static byte[] getBytesIso8859_1(String string) {
        return getBytes(string, ISO_8859_1);
    }

    /**
     * Get bytes us ascii byte [ ].
     *
     * @param string the string
     * @return the byte [ ]
     */
    public static byte[] getBytesUsAscii(String string) {
        return getBytes(string, US_ASCII);
    }

    /**
     * Get bytes utf 16 byte [ ].
     *
     * @param string the string
     * @return the byte [ ]
     */
    public static byte[] getBytesUtf16(String string) {
        return getBytes(string, UTF_16);
    }

    /**
     * Get bytes utf 16 be byte [ ].
     *
     * @param string the string
     * @return the byte [ ]
     */
    public static byte[] getBytesUtf16Be(String string) {
        return getBytes(string, UTF_16BE);
    }

    /**
     * Get bytes utf 16 le byte [ ].
     *
     * @param string the string
     * @return the byte [ ]
     */
    public static byte[] getBytesUtf16Le(String string) {
        return getBytes(string, UTF_16LE);
    }

    /**
     * Get bytes utf 8 byte [ ].
     *
     * @param string the string
     * @return the byte [ ]
     */
    public static byte[] getBytesUtf8(String string) {
        return getBytes(string, UTF_8);
    }

    private static String newString(byte[] bytes) {
        return bytes == null ? null : new String(bytes, UTF_8);
    }

    /**
     * New string iso 8859 1 string.
     *
     * @param bytes the bytes
     * @return the string
     */
    public static String newStringIso8859_1(byte[] bytes) {
        return new String(bytes, ISO_8859_1);
    }

    /**
     * New string us ascii string.
     *
     * @param bytes the bytes
     * @return the string
     */
    public static String newStringUsAscii(byte[] bytes) {
        return new String(bytes, US_ASCII);
    }

    /**
     * New string utf 16 string.
     *
     * @param bytes the bytes
     * @return the string
     */
    public static String newStringUtf16(byte[] bytes) {
        return new String(bytes, UTF_16);
    }

    /**
     * New string utf 16 be string.
     *
     * @param bytes the bytes
     * @return the string
     */
    public static String newStringUtf16Be(byte[] bytes) {
        return new String(bytes, UTF_16BE);
    }

    /**
     * New string utf 16 le string.
     *
     * @param bytes the bytes
     * @return the string
     */
    public static String newStringUtf16Le(byte[] bytes) {
        return new String(bytes, UTF_16LE);
    }

    /**
     * URL-Encodes a given string using UTF-8, No UnsupportedEncodingException to handle
     * as it is dealt with in this method.
     *
     * @param stringToEncode the string to encode
     * @return the string
     */
    public static String encodeUrl(String stringToEncode) {
        try {
            return URLEncoder.encode(stringToEncode, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * URL-encodes a given string using ISO-8859-1, No UnsupportedEncodingException to
     * handle as it is dealt with in this method.
     *
     * @param stringToEncode the string to encode
     * @return the string
     */
    public static String encodeUrlIso(String stringToEncode) {
        try {
            return URLEncoder.encode(stringToEncode, "ISO-8859-1");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * URL-Decodes a given string using UTF-8. No UnsupportedEncodingException to handle
     * as it is dealt with in this method.
     *
     * @param stringToDecode the string to decode
     * @return the string
     */
    public static String decodeUrl(String stringToDecode) {
        try {
            return URLDecoder.decode(stringToDecode, "UTF-8");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * URL-Decodes a given string using ISO-8859-1. No UnsupportedEncodingException to
     * handle as it is dealt with in this method.
     *
     * @param stringToDecode the string to decode
     * @return the string
     */
    public static String decodeUrlIso(String stringToDecode) {
        try {
            return URLDecoder.decode(stringToDecode, "ISO-8859-1");
        } catch (UnsupportedEncodingException e1) {
            throw new RuntimeException(e1);
        }
    }

    /**
     * Is empty boolean.
     *
     * @param text the text
     * @return the boolean
     */
    public static boolean isEmpty(CharSequence text) {
        return text == null || text.length() == 0;
    }

    /**
     * Is blank boolean.
     *
     * @param cs the cs
     * @return the boolean
     */
    public static boolean isBlank(final CharSequence cs) {
        int strLen;
        if (cs == null || (strLen = cs.length()) == 0) {
            return true;
        }
        for (int i = 0; i < strLen; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Starts with char boolean.
     *
     * @param s the s
     * @param c the c
     * @return the boolean
     */
    public static boolean startsWithChar(String s, char c) {
        if (s.length() == 0) {
            return false;
        }
        return s.charAt(0) == c;
    }

    /**
     * Ends with char boolean.
     *
     * @param s the s
     * @param c the c
     * @return the boolean
     */
    public static boolean endsWithChar(String s, char c) {
        if (s.length() == 0) {
            return false;
        }
        return s.charAt(s.length() - 1) == c;
    }

    /**
     * Trim string.
     *
     * @param text the text
     * @return the string
     */
    public static String trim(String text) {
        return text == null ? null : text.trim();
    }

    /**
     * Trim whitespace string.
     *
     * @param str the str
     * @return the string
     */
    public static String trimWhitespace(String str) {
        if (isEmpty(str)) {
            return str;
        }
        StringBuilder sb = new StringBuilder(str);
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(0))) {
            sb.deleteCharAt(0);
        }
        while (sb.length() > 0 && Character.isWhitespace(sb.charAt(sb.length() - 1))) {
            sb.deleteCharAt(sb.length() - 1);
        }
        return sb.toString();
    }

    /**
     * Upper first letter string.
     *
     * @param s the s
     * @return the string
     */
    public static String upperFirstLetter(final String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (!Character.isLowerCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) - 32)) + s.substring(1);
    }

    /**
     * Lower first letter string.
     *
     * @param s the s
     * @return the string
     */
    public static String lowerFirstLetter(String s) {
        if (s == null || s.length() == 0) {
            return "";
        }
        if (!Character.isUpperCase(s.charAt(0))) {
            return s;
        }
        return String.valueOf((char) (s.charAt(0) + 32)) + s.substring(1);
    }

    /**
     * Reverse string.
     *
     * @param s the s
     * @return the string
     */
    public static String reverse(String s) {
        if (s == null) {
            return "";
        }
        int len = s.length();
        if (len <= 1) {
            return s;
        }
        int mid = len >> 1;
        char[] chars = s.toCharArray();
        char c;
        for (int i = 0; i < mid; ++i) {
            c = chars[i];
            chars[i] = chars[len - i - 1];
            chars[len - i - 1] = c;
        }
        return new String(chars);
    }

    /**
     * Test if the given String starts with the specified prefix, ignoring upper/lower
     * case.
     *
     * @param str    the String to check
     * @param prefix the prefix to look for
     * @return the boolean
     * @see String#startsWith String#startsWithString#startsWithString#startsWithString#startsWith
     */
    public static boolean startsWithIgnoreCase(String str, String prefix) {
        if (str == null || prefix == null) {
            return false;
        }
        if (str.startsWith(prefix)) {
            return true;
        }
        if (str.length() < prefix.length()) {
            return false;
        }
        String lcStr = str.substring(0, prefix.length()).toLowerCase();
        String lcPrefix = prefix.toLowerCase();
        return lcStr.equals(lcPrefix);
    }

    /**
     * Test if the given String ends with the specified suffix, ignoring upper/lower
     * case.
     *
     * @param str    the String to check
     * @param suffix the suffix to look for
     * @return the boolean
     * @see String#endsWith String#endsWithString#endsWithString#endsWithString#endsWith
     */
    public static boolean endsWithIgnoreCase(String str, String suffix) {
        if (str == null || suffix == null) {
            return false;
        }
        if (str.endsWith(suffix)) {
            return true;
        }
        if (str.length() < suffix.length()) {
            return false;
        }

        String lcStr = str.substring(str.length() - suffix.length()).toLowerCase();
        String lcSuffix = suffix.toLowerCase();
        return lcStr.equals(lcSuffix);
    }

    /**
     * Equals boolean.
     *
     * @param cs1 the cs 1
     * @param cs2 the cs 2
     * @return the boolean
     */
    public static boolean equals(final CharSequence cs1, final CharSequence cs2) {
        if (cs1 == cs2) {
            return true;
        }
        if (cs1 == null || cs2 == null) {
            return false;
        }
        if (cs1 instanceof String && cs2 instanceof String) {
            return cs1.equals(cs2);
        }
        return regionMatches(cs1, false, 0, cs2, 0, Math.max(cs1.length(), cs2.length()));
    }

    /**
     * Equals ignore case boolean.
     *
     * @param str1 the str 1
     * @param str2 the str 2
     * @return the boolean
     */
    public static boolean equalsIgnoreCase(final CharSequence str1, final CharSequence str2) {
        if (str1 == null || str2 == null) {
            return str1 == str2;
        } else if (str1 == str2) {
            return true;
        } else if (str1.length() != str2.length()) {
            return false;
        } else {
            return regionMatches(str1, true, 0, str2, 0, str1.length());
        }
    }

    /**
     * Region matches boolean.
     *
     * @param cs         the cs
     * @param ignoreCase the ignore case
     * @param thisStart  the this start
     * @param substring  the substring
     * @param start      the start
     * @param length     the length
     * @return the boolean
     */
    public static boolean regionMatches(final CharSequence cs, final boolean ignoreCase, final int thisStart, final CharSequence substring, final int start, final int length) {
        if (cs instanceof String && substring instanceof String) {
            return ((String) cs).regionMatches(ignoreCase, thisStart, (String) substring, start, length);
        }
        int index1 = thisStart;
        int index2 = start;
        int tmpLen = length;

        while (tmpLen-- > 0) {
            final char c1 = cs.charAt(index1++);
            final char c2 = substring.charAt(index2++);

            if (c1 == c2) {
                continue;
            }

            if (!ignoreCase) {
                return false;
            }

            // The same check as in String.regionMatches():
            if (Character.toUpperCase(c1) != Character.toUpperCase(c2) && Character.toLowerCase(c1) != Character.toLowerCase(c2)) {
                return false;
            }
        }

        return true;
    }

    /**
     * Gets str length. 统计中英文混排字符长度     *
     *
     * @param str the str
     * @return the str length
     */
    public static int getStrLength(String str) {
        int valueLength = 0;
        if (!StringUtils.isEmpty(str)) {
            for (int i = 0; i < str.length(); i++) {
                String temp = str.substring(i, i + 1);
                if (RegexUtils.isZh(temp)) {
                    valueLength += 2;
                } else {
                    valueLength += 1;
                }
            }
        }
        return valueLength;
    }

    /**
     * 把参数进行排序，生成一个字符串
     *
     * @param data the data
     * @return the sorted content
     */
    public static String getSortedContent(Map<String,String> data) {
        StringBuffer content = new StringBuffer();
        List<String> keys = new ArrayList<String>(data.keySet());
        Collections.sort(keys);
        int index = 0;
        for (String key : keys) {
            String value = data.get(key);
            content.append((index == 0 ? "" : "&")).append(key).append("=").append(value);
            index++;
        }
        return content.toString();
    }

}
