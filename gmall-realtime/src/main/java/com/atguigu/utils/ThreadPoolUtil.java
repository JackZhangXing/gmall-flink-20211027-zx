package com.atguigu.utils;

import lombok.SneakyThrows;

import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * ThreadPoolUtil
 *
 * @author Star Zhang
 * @date 2022/4/26 21:53
 */
public class ThreadPoolUtil {
    private static ThreadPoolExecutor threadPoolExecutor = null;

    public ThreadPoolUtil() {
    }

    public static ThreadPoolExecutor getThreadPoolExecutor() {

        if (threadPoolExecutor == null) {
            synchronized (ThreadPoolUtil.class) {
                if (threadPoolExecutor == null) {
                    threadPoolExecutor = new ThreadPoolExecutor(4,
                            20,
                            60,
                            TimeUnit.SECONDS, new LinkedBlockingDeque<>());
                }
            }
        }
        return threadPoolExecutor;
    }

    public static void main(String[] args) {
        ThreadPoolExecutor threadPoolExecutor = getThreadPoolExecutor();

        for (int i = 0; i < 10; i++) {

            threadPoolExecutor.execute(new Runnable() {
                @SneakyThrows
                @Override
                public void run() {
                    System.out.println("***********"+Thread.currentThread()+"********");
                    Thread.sleep(2000);
                }
            });
        }
    }
}
