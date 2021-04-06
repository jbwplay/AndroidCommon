package com.androidbase.utils;

import java.nio.charset.Charset;

public final class HexUtils {

    private static final char[] DIGITS_LOWER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private static final char[] DIGITS_UPPER = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    public static boolean isHexNumber(String value) {
        int index = (value.startsWith("-") ? 1 : 0);
        return (value.startsWith("0x", index) || value.startsWith("0X", index) || value.startsWith("#", index));
    }

    public static char[] encodeHex(byte[] data) {
        return encodeHex(data, true);
    }

    public static char[] encodeHex(String str, Charset charset) {
        return encodeHex(new String(str).getBytes(charset), true);
    }

    public static char[] encodeHex(byte[] data, boolean toLowerCase) {
        return encodeHex(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    public static String encodeHexStr(byte[] data) {
        return encodeHexStr(data, true);
    }

    public static String encodeHexStr(String data, Charset charset) {
        return encodeHexStr(new String(data).getBytes(charset), true);
    }

    public static String encodeHexStr(String data) {
        return encodeHexStr(data, Charset.forName("UTF-8"));
    }

    public static String encodeHexStr(byte[] data, boolean toLowerCase) {
        return encodeHexStr(data, toLowerCase ? DIGITS_LOWER : DIGITS_UPPER);
    }

    private static char[] encodeHex(byte[] data, char[] toDigits) {
        int l = data.length;
        char[] out = new char[l << 1];
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = toDigits[(0xF0 & data[i]) >>> 4];
            out[j++] = toDigits[0x0F & data[i]];
        }
        return out;
    }

    private static String encodeHexStr(byte[] data, char[] toDigits) {
        return new String(encodeHex(data, toDigits));
    }

    public static String decodeHexStr(String hexStr) {
        return decodeHexStr(hexStr, Charset.forName("UTF-8"));
    }

    public static String decodeHexStr(String hexStr, Charset charset) {
        if (hexStr == null || hexStr.length() == 0) {
            return hexStr;
        }
        return decodeHexStr(hexStr.toCharArray(), charset);
    }

    public static String decodeHexStr(char[] hexData, Charset charset) {
        return new String(decodeHex(hexData), charset);
    }

    public static byte[] decodeHex(String hexStr) {
        if (hexStr == null || hexStr.length() == 0) {
            return null;
        }
        return decodeHex(hexStr.toCharArray());
    }

    public static byte[] decodeHex(char[] hexData) {

        int len = hexData.length;

        if ((len & 0x01) != 0) {
            throw new RuntimeException("Odd number of characters.");
        }

        byte[] out = new byte[len >> 1];

        for (int i = 0, j = 0; j < len; i++) {
            int f = toDigit(hexData[j], j) << 4;
            j++;
            f = f | toDigit(hexData[j], j);
            j++;
            out[i] = (byte) (f & 0xFF);
        }

        return out;
    }

    private static int toDigit(char ch, int index) {
        int digit = Character.digit(ch, 16);
        if (digit == -1) {
            throw new RuntimeException("Illegal hexadecimal character " + ch + " at index " + index);
        }
        return digit;
    }
}