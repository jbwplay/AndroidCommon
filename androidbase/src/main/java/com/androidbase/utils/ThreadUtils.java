package com.androidbase.utils;

import android.os.Looper;
import android.util.Log;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public final class ThreadUtils {
    private static final String TAG = ThreadUtils.class.getSimpleName();

    public static ThreadPoolExecutor newCachedThreadPool() {
        return new ThreadPoolExecutor(0, Integer.MAX_VALUE, 60L, TimeUnit.SECONDS, new SynchronousQueue<Runnable>(), new CounterThreadFactory("AndroidBase"), new LogDiscardPolicy());
    }

    public static ThreadPoolExecutor newFixedThreadPool(int nThreads) {
        return new ThreadPoolExecutor(nThreads, nThreads, 0L, TimeUnit.MILLISECONDS, new LinkedBlockingQueue<Runnable>(), new CounterThreadFactory("AndroidBase"), new LogDiscardPolicy());
    }

    public static ThreadPoolExecutor newFixedThreadPool() {
        return newFixedThreadPool(getCalcThreads());
    }

    public static ThreadPoolExecutor newSingleThreadExecutor() {
        return newFixedThreadPool(1);
    }

    public static int getCalcThreads() {
        // 获取 CPU 核心数
        int cpuNumber = Runtime.getRuntime().availableProcessors();
        if (cpuNumber <= 5) {
            return 5;
        } else {
            if (cpuNumber * 2 + 1 >= 10) {
                return 10;
            } else {
                // 默认返回支持的数量 * 2 + 1
                return cpuNumber * 2 + 1;
            }
        }
    }

    public static class LogDiscardPolicy implements RejectedExecutionHandler {

        public LogDiscardPolicy() {
        }

        public void rejectedExecution(Runnable r, ThreadPoolExecutor e) {
            Log.e(TAG, "rejectedExecution() " + r + " is discard.");
        }
    }

    public static class CounterThreadFactory implements ThreadFactory {
        private int count;
        private final String name;

        public CounterThreadFactory(String name) {
            this.name = name;
        }

        @Override
        public Thread newThread(Runnable r) {
            Thread thread = new Thread(r);
            thread.setName(name + "-thread #" + count++);
            return thread;
        }
    }

    public static boolean isMain() {
        return Looper.myLooper() == Looper.getMainLooper();
    }
}
