package com.androidbase.utils.cache;

import android.util.Log;

/**
 * detail: 缓存内部工具类
 */
final class SdFileCacheUtils {

    private SdFileCacheUtils() {
    }

    // 日志 TAG
    private static final String TAG = SdFileCacheUtils.class.getSimpleName();

    // 空格
    private static final char mSeparator = ' ';

    /**
     * 判断缓存的 String 数据是否到期
     *
     * @param data 待存储数据
     * @return {@code true} 到期了, {@code false} 还没有到期
     */
    protected static boolean isDue(final String data) {
        if (data == null) {
            return true;
        }
        return isDue(data.getBytes());
    }

    /**
     * 判断缓存的 byte 数据是否到期
     *
     * @param data 待存储数据
     * @return {@code true} 到期了, {@code false} 还没有到期
     */
    protected static boolean isDue(final byte[] data) {
        // 获取时间数据信息
        String[] strs = getDateInfoFromDate(data);
        // 判断是否过期
        if (strs != null && strs.length == 2) {
            // 保存的时间
            String saveTimeStr = strs[0];
            // 判断是否 0 开头, 是的话裁剪
            while (saveTimeStr.startsWith("0")) {
                saveTimeStr = saveTimeStr.substring(1);
            }
            // 转换时间
            long saveTime = Long.parseLong(saveTimeStr); // 保存时间
            long deleteAfter = Long.parseLong(strs[1]); // 过期时间
            // 判断当前时间是否大于 保存时间 + 过期时间
            return System.currentTimeMillis() > saveTime + deleteAfter * 1000;
        }
        return false;
    }

    /**
     * 保存数据, 创建时间信息
     *
     * @param second  时间 ( 秒 )
     * @param strInfo 字符串信息
     * @return 字符串, 包含创建时间、存储数据等
     */
    protected static String newStringWithDateInfo(final int second, final String strInfo) {
        return createDateInfo(second) + strInfo;
    }

    /**
     * 保存数据, 创建时间信息
     *
     * @param second 时间 ( 秒 )
     * @param data   待存储数据
     * @return byte[], 包含创建时间、存储数据等
     */
    protected static byte[] newByteArrayWithDateInfo(final int second, final byte[] data) {
        if (data != null) {
            try {
                byte[] dateArys = createDateInfo(second).getBytes();
                return arrayCopy(dateArys, data);
            } catch (Exception e) {
                Log.e(TAG, e + "newByteArrayWithDateInfo");
            }
        }
        return null;
    }

    /**
     * 拼接数组
     *
     * @param prefix 第一个数组
     * @param suffix 第二个数组
     * @return 拼接后的数组
     */
    public static byte[] arrayCopy(final byte[] prefix, final byte[] suffix) {
        // 获取数据长度
        int prefixLength = (prefix != null) ? prefix.length : 0;
        int suffixLength = (suffix != null) ? suffix.length : 0;
        // 数据都为 null, 则直接跳过
        if (prefixLength + suffixLength == 0) {
            return null;
        }
        // 创建数组
        byte[] arrays = new byte[prefixLength + suffixLength];
        // 进行判断处理
        if (prefixLength != 0) {
            System.arraycopy(prefix, 0, arrays, 0, prefixLength);
        }
        if (suffixLength != 0) {
            System.arraycopy(suffix, 0, arrays, prefixLength, suffixLength);
        }
        return arrays;
    }

    /**
     * 创建时间信息
     *
     * @param second 时间 ( 秒 )
     * @return 时间信息字符串
     */
    private static String createDateInfo(final int second) {
        String currentTime = String.valueOf(System.currentTimeMillis());
        while (currentTime.length() < 13) {
            currentTime = "0" + currentTime;
        }
        return currentTime + "-" + second + mSeparator;
    }

    /**
     * 清空时间信息
     *
     * @param strInfo 存储数据
     * @return 存储数据
     */
    protected static String clearDateInfo(final String strInfo) {
        if (strInfo != null && hasDateInfo(strInfo.getBytes())) {
            return strInfo.substring(strInfo.indexOf(mSeparator) + 1);
        }
        return strInfo;
    }

    /**
     * 清空时间信息
     *
     * @param data 存储数据
     * @return 存储数据
     */
    protected static byte[] clearDateInfo(final byte[] data) {
        if (hasDateInfo(data)) {
            try {
                return copyOfRange(data, indexOf(data, mSeparator) + 1, data.length);
            } catch (Exception e) {
                Log.e(TAG, e + "clearDateInfo");
            }
        }
        return data;
    }

    /**
     * 判断是否时间信息
     *
     * @param data 时间数据
     * @return {@code true} yes, {@code false} no
     */
    private static boolean hasDateInfo(final byte[] data) {
        return data != null && data.length > 15 && data[13] == '-' && indexOf(data, mSeparator) > 14;
    }

    /**
     * 获取时间信息 ( 保存时间、过期时间 )
     *
     * @param data 时间数据
     * @return String[]
     */
    private static String[] getDateInfoFromDate(final byte[] data) {
        if (hasDateInfo(data)) {
            try {
                // 保存时间
                String saveDate = new String(copyOfRange(data, 0, 13));
                // 过期时间
                String deleteAfter = new String(copyOfRange(data, 14, indexOf(data, mSeparator)));
                // 返回数据
                return new String[]{saveDate, deleteAfter};
            } catch (Exception e) {
                Log.e(TAG, e + "getDateInfoFromDate");
            }
        }
        return null;
    }

    /**
     * 获取字符所在的索引
     *
     * @param data 数据
     * @param ch   字符
     * @return 对应的索引
     */
    private static int indexOf(final byte[] data, final char ch) {
        if (data != null) {
            for (int i = 0; i < data.length; i++) {
                if (data[i] == ch) {
                    return i;
                }
            }
        }
        return -1;
    }

    /**
     * 复制 byte[] 范围
     *
     * @param original 原始数据
     * @param from     开始位置
     * @param to       所需位置
     * @return byte[]
     * @throws Exception 索引异常
     */
    private static byte[] copyOfRange(final byte[] original, final int from, final int to) throws
            Exception {
        int newLength = to - from;
        if (newLength < 0) {
            throw new IllegalArgumentException(from + " > " + to);
        }

        byte[] copy = new byte[newLength];
        System.arraycopy(original, from, copy, 0, Math.min(original.length - from, newLength));
        return copy;
    }

}