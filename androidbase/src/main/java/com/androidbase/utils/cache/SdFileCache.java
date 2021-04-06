package com.androidbase.utils.cache;

import android.util.Log;

import com.androidbase.utils.AndroidUtils;
import com.androidbase.utils.javaio.StreamUtil;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

/**
 * detail: 缓存工具类
 */
public final class SdFileCache {

    private SdFileCache() {
    }

    // 日志 TAG
    private static final String TAG = SdFileCache.class.getSimpleName();

    // 缓存文件名
    private static final String DEF_FILE_NAME = SdFileCache.class.getSimpleName();

    // 过期小时 ( 单位秒 ) = 1 小时
    public static final int TIME_HOUR = 60 * 60;
    // 一天 24 小时
    public static final int TIME_DAY = TIME_HOUR * 24;
    // 缓存最大值 50 MB
    public static final int MAX_SIZE = 1000 * 1000 * 50;
    // 不限制存放数据的数量
    public static final int MAX_COUNT = Integer.MAX_VALUE;
    // 不同地址配置缓存对象
    private static Map<String, SdFileCache> sInstanceMaps = new HashMap<>();
    // 缓存管理类
    private SdFileCacheManager mCache;
    // 缓存地址
    private static String sCachePath = null;

    /**
     * 获取 DevCache ( 默认缓存文件名 )
     *
     * @return {@link SdFileCache}
     */
    public static SdFileCache newCache() {
        return newCache(DEF_FILE_NAME);
    }

    /**
     * 获取 DevCache ( 自定义缓存文件名 )
     *
     * @param cacheName 缓存文件名
     * @return {@link SdFileCache}
     */
    public static SdFileCache newCache(final String cacheName) {
        return newCache(new File(getCachePath(), cacheName), MAX_SIZE, MAX_COUNT);
    }

    /**
     * 获取 DevCache ( 自定义缓存文件地址 )
     *
     * @param cacheDir 缓存文件地址
     * @return {@link SdFileCache}
     */
    public static SdFileCache newCache(final File cacheDir) {
        return newCache(cacheDir, MAX_SIZE, MAX_COUNT);
    }

    /**
     * 获取 DevCache ( 自定义缓存大小 )
     *
     * @param maxSize  文件最大大小
     * @param maxCount 最大存储数量
     * @return {@link SdFileCache}
     */
    public static SdFileCache newCache(final long maxSize, final int maxCount) {
        return newCache(new File(getCachePath(), DEF_FILE_NAME), maxSize, maxCount);
    }

    /**
     * 获取 DevCache ( 自定义缓存文件地址、大小等 )
     *
     * @param cacheDir 缓存文件地址
     * @param maxSize  文件最大大小
     * @param maxCount 最大存储数量
     * @return {@link SdFileCache}
     */
    public static SdFileCache newCache(final File cacheDir, final long maxSize, final int maxCount) {
        if (cacheDir == null) {
            return null;
        }
        // 判断是否存在缓存信息
        SdFileCache manager = sInstanceMaps.get(cacheDir.getAbsoluteFile() + myPid());
        if (manager == null) {
            // 初始化新的缓存信息, 并且保存
            manager = new SdFileCache(cacheDir, maxSize, maxCount);
            sInstanceMaps.put(cacheDir.getAbsolutePath() + myPid(), manager);
        }
        return manager;
    }

    /**
     * 获取进程 id ( android.os.Process.myPid() )
     *
     * @return 进程 id
     */
    private static String myPid() {
        return "_" + android.os.Process.myPid();
    }

    /**
     * 最终初始化方法
     *
     * @param cacheDir 缓存文件地址
     * @param maxSize  文件最大大小
     * @param maxCount 最大存储数量
     * @return {@link SdFileCache} 缓存工具类对象
     */
    private SdFileCache(final File cacheDir, final long maxSize, final int maxCount) {
        Exception e = null;
        if (cacheDir == null) {
            e = new Exception("cacheDir is null");
        } else if (!cacheDir.exists() && !cacheDir.mkdirs()) {
            e = new Exception("can't make dirs in " + cacheDir.getAbsolutePath());
        }
        if (e != null) {
            Log.e(TAG, e + "private DevCache()");
        }
        mCache = new SdFileCacheManager(cacheDir, maxSize, maxCount);
    }

    /**
     * 保存 String 数据到缓存中
     *
     * @param key   保存的 key
     * @param value 保存的 String 数据
     * @return {@code true} success, {@code false} fail
     */
    public boolean put(final String key, final String value) {
        File file = mCache.newFile(key);
        if (file == null || value == null) {
            return false;
        }
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new FileWriter(file), 1024);
            bw.write(value);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e + "put");
        } finally {
            StreamUtil.close(bw);
            mCache.put(file);
        }
        return false;
    }

    /**
     * 保存 String 数据到缓存中
     *
     * @param key      保存的 key
     * @param value    保存的 String 数据
     * @param saveTime 保存的时间, 单位: 秒
     * @return {@code true} success, {@code false} fail
     */
    public boolean put(final String key, final String value, final int saveTime) {
        if (key != null && value != null) {
            return put(key, SdFileCacheUtils.newStringWithDateInfo(saveTime, value));
        }
        return false;
    }

    /**
     * 读取 String 数据
     *
     * @param key 保存的 key
     * @return 字符串数据
     */
    public String getAsString(final String key) {
        File file = mCache.get(key);
        if (file == null) {
            return null;
        }
        if (!file.exists()) {
            return null;
        }
        boolean removeFile = false;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            StringBuilder builder = new StringBuilder();
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                builder.append(currentLine);
            }
            // 读取内容
            String readString = builder.toString();
            if (!SdFileCacheUtils.isDue(readString)) {
                return SdFileCacheUtils.clearDateInfo(readString);
            } else {
                Log.d(TAG, "getAsString key: %s file has expired" + key);
                removeFile = true;
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, e + "getAsString");
            return null;
        } finally {
            StreamUtil.close(br);
            if (removeFile) {
                remove(key);
            }
        }
    }

    /**
     * 保存 byte 数据到缓存中
     *
     * @param key  保存的 key
     * @param data 保存的数据
     * @return {@code true} success, {@code false} fail
     */
    public boolean put(final String key, final byte[] data) {
        if (key == null || data == null) {
            return false;
        }
        File file = mCache.newFile(key);
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(file);
            fos.write(data);
            return true;
        } catch (Exception e) {
            Log.e(TAG, e + "put byte[]");
        } finally {
            StreamUtil.close(fos);
            mCache.put(file);
        }
        return false;
    }

    /**
     * 获取对应 key 的 File 输入流
     *
     * @param key 保存的 key
     * @return {@link InputStream}
     * @throws FileNotFoundException 文件不存在
     */
    public InputStream get(final String key) throws FileNotFoundException {
        File file = mCache.get(key);
        if (file != null && file.exists()) {
            return new FileInputStream(file);
        }
        return null;
    }

    /**
     * 保存 byte 数据到缓存中
     *
     * @param key      保存的 key
     * @param data     保存的数据
     * @param saveTime 保存的时间, 单位: 秒
     * @return {@code true} success, {@code false} fail
     */
    public boolean put(final String key, final byte[] data, final int saveTime) {
        return put(key, SdFileCacheUtils.newByteArrayWithDateInfo(saveTime, data));
    }

    /**
     * 获取 byte[] 数据
     *
     * @param key 保存的 key
     * @return byte[]
     */
    public byte[] getAsBinary(final String key) {
        RandomAccessFile raFile = null;
        boolean removeFile = false;
        try {
            File file = mCache.get(key);
            if (!file.exists()) {
                return null;
            }
            raFile = new RandomAccessFile(file, "r");
            byte[] byteArray = new byte[(int) raFile.length()];
            raFile.read(byteArray);
            if (!SdFileCacheUtils.isDue(byteArray)) {
                return SdFileCacheUtils.clearDateInfo(byteArray);
            } else {
                Log.d(TAG, "getAsBinary - key: %s file has expired" + key);
                removeFile = true;
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, e + "getAsBinary");
            return null;
        } finally {
            StreamUtil.close(raFile);
            if (removeFile) {
                remove(key);
            }
        }
    }

    /**
     * 获取缓存文件
     *
     * @param key 保存的 key
     * @return 缓存的文件
     */
    public File file(final String key) {
        File file = mCache.newFile(key);
        if (file != null && file.exists()) {
            return file;
        }
        return null;
    }

    /**
     * 移除某个 key 的数据
     *
     * @param key 保存的 key
     * @return {@code true} yes, {@code false} no
     */
    public boolean remove(final String key) {
        return mCache.remove(key);
    }

    /**
     * 清除所有数据
     */
    public void clear() {
        mCache.clear();
    }

    /**
     * 获取应用内部存储缓存路径
     *
     * @return 应用内部存储缓存路径
     */
    private static String getCachePath() {
        if (sCachePath == null) {
            sCachePath = AndroidUtils.getContext()
                    .getExternalCacheDir()
                    .getAbsolutePath();
        }
        return sCachePath;
    }
}